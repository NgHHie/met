package ptit.dblab.app.service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import net.sf.jsqlparser.JSQLParserException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.data.domain.Sort;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.multipart.MultipartFile;
import ptit.dblab.app.dto.response.*;
import ptit.dblab.app.enumerate.ErrorCode;
import ptit.dblab.app.enumerate.QuestionStatus;
import ptit.dblab.app.utils.AppUtils;
import ptit.dblab.shared.enumerate.TypeQuestion;
import ptit.dblab.shared.exception.core.ValidateException;
import ptit.dblab.shared.utils.ContextUtil;
import ptit.dblab.app.dto.request.*;
import ptit.dblab.app.dto.request.*;
import ptit.dblab.app.entity.*;
import ptit.dblab.app.entity.*;
import ptit.dblab.app.enumerate.LevelQuestion;
import ptit.dblab.app.enumerate.Role;
import ptit.dblab.app.feignClient.SubmitServiceClient;
import ptit.dblab.app.interfaceProjection.QuestionStat;
import ptit.dblab.app.interfaceProjection.UserDoneQuestionProjection;
import ptit.dblab.app.repository.QuestionDetailRepository;
import ptit.dblab.app.utils.SequenceUtil;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ptit.dblab.shared.common.BaseService;
import ptit.dblab.app.mapper.QuestionMapper;
import ptit.dblab.app.repository.QuestionRepository;
import ptit.dblab.app.utils.Util;
import ptit.dblab.shared.utils.JsonUtil;
import ptit.dblab.shared.utils.SqlUtil;


@Service
@Slf4j
public class QuestionService extends BaseService<Question, QuestionRepository>{

	private final QuestionMapper questionMapper;
	private final SequenceUtil sequenceUtil;
	private final QuestionDetailRepository questionDetailRepository;
	private final TableCreateServcie tableCreateServcie;
	private final ProcessService processService;
	private final StatService statService;
	private final SubmitServiceClient submitServiceClient;
	private final UserService userService;
	private final JsonUtil jsonUtil;

	private final Util util;
    private final ContextUtil contextUtil;
    private final QuestionRepository questionRepository;
	private final SqlUtil sqlUtil;
	private final SqlExecutorService sqlExecutorService;
	private final SubmitService submitService;

	public QuestionService(QuestionRepository repository, QuestionMapper questionMapper,
                           SequenceUtil sequenceUtil, QuestionDetailRepository questionDetailRepository,
						   TableCreateServcie tableCreateServcie, ProcessService processService,
						   StatService statService, SubmitServiceClient submitServiceClient,
						   UserService userService, JsonUtil jsonUtil, Util util, ContextUtil contextUtil,
						   QuestionRepository questionRepository, SqlUtil sqlUtil, SqlExecutorService sqlExecutorService,
						   @Lazy SubmitService submitService) {
		super(repository);
		this.questionMapper = questionMapper;
		this.sequenceUtil = sequenceUtil;
        this.questionDetailRepository = questionDetailRepository;
        this.tableCreateServcie = tableCreateServcie;
        this.processService = processService;
        this.statService = statService;
        this.submitServiceClient = submitServiceClient;
        this.userService = userService;
        this.jsonUtil = jsonUtil;
        this.util = util;
        this.contextUtil = contextUtil;
        this.questionRepository = questionRepository;
        this.sqlUtil = sqlUtil;
        this.sqlExecutorService = sqlExecutorService;
        this.submitService = submitService;
    }


	@Transactional
	public QuestionBasicResponse create(QuestionRequest request) {
		log.info("****************** Create Question ****************");
        String newCode = sequenceUtil.generateNewQuestionCode();
        Question question = questionMapper.toEntity(request);
        question.setQuestionCode(newCode);
		question.setIsSynchorus(false);
		String prefixCode = sequenceUtil.generatePrefixQuestionCode();
		question.setPrefixCode(prefixCode);
		question.setEnable(false);
		question.setStatus(QuestionStatus.REVIEW);
		if(Objects.nonNull(question.getQuestionDetails())) {
			for(QuestionDetail questionDetail : question.getQuestionDetails()) {
				questionDetail.setQuestion(question);
			}
		}
        this.save(question);
        return questionMapper.toBasicResponse(question);
	}

	@Transactional
	public void update(String id, QuestionRequest request) {
		Question currentQuestion = this.findById(id);
		System.out.println(currentQuestion);
		log.info("****************** update ***************");
		questionMapper.update(currentQuestion, questionMapper.toEntity(request));
		List<String> tableCreatedIds = new ArrayList<>();
		for(QuestionDetail questionDetail : currentQuestion.getQuestionDetails()) {
			System.out.println(questionDetail.getTestcases());
			if(Objects.isNull(questionDetail.getTestcases())) {
				throw new ValidateException("Testcase không được bỏ trống");
			}
			for(Testcase test : questionDetail.getTestcases()) {
				test.setQuestionDetail(questionDetail);
			}
			questionDetail.setQuestion(currentQuestion);

			QuestionDetailRequest detailRequest = getQuestionDetailRequest(request.getQuestionDetails(),questionDetail.getTypeDatabase().getId());
			if(Objects.nonNull(detailRequest)) {
				log.info("************ check update OK ********");
				List<TableDetail> tableDetails = new ArrayList<TableDetail>();
				for (String tableId : detailRequest.getTableUses()) {
					TableDetail table = util.containTableId(questionDetail.getTableUses(), tableId);
					if (table != null) {
						tableDetails.add(table);
					} else {
						table = new TableDetail();
						table.setQuestionDetail(questionDetail);
						TableCreated tableCreated = new TableCreated();
						tableCreated.setId(tableId);
						table.setTableCreated(tableCreated);
						tableDetails.add(table);
					}
				}
				if(Objects.isNull(questionDetail.getTableUses())) {
					questionDetail.setTableUses(tableDetails);
				} else {
					questionDetail.getTableUses().clear();
					questionDetail.getTableUses().addAll(tableDetails);
				}
				tableCreatedIds.addAll(detailRequest.getTableUses());
			}
		}
		currentQuestion.setIsSynchorus(true);
		this.save(currentQuestion);

		//update data in submit service
		List<TableCreated> tableCreatedChooses = tableCreateServcie.getListTableCreatedByIds(tableCreatedIds);
		List<CpTableCreatedRequest> cpTableCreatedRequests = getCpTableCreatedRequests(tableCreatedChooses);
		ResponseEntity<?> setupTableResponse = submitServiceClient.setupTableData(cpTableCreatedRequests);
		if(!setupTableResponse.getStatusCode().is2xxSuccessful()) {
			throw new ValidateException("Failed to call submit service to setup table data");
		}
		DataRequest contestDataRequest = createContestData(currentQuestion);
		log.info("\n\n=========== contestData: {} \n\n\n",contestDataRequest.toString());
		ResponseEntity<?> setupTestcaseDataResponse = submitServiceClient.setupTestcaseData(contestDataRequest);
		if(!setupTestcaseDataResponse.getStatusCode().is2xxSuccessful()) {
			throw new ValidateException("Failed to call submit service to setup testcase data");
		}
		//update order generate table
		for(QuestionDetail questionDetail : currentQuestion.getQuestionDetails()) {
			List<TableCreated> tableCreateds = tableCreateServcie.getListTableCreatedByQuestionDetailId(questionDetail.getId());
			processService.sortTables(tableCreateds,questionDetail.getId());
		}
	}

	@Transactional
	public QuestionCreatedSummaryResponse createQuestionFromImport(List<QuestionRequest> requests) {
		log.info("****************** Create Question from import ****************");
		List<QuestionCreatedSummaryResponse.QuestionSummaryItem> successItems = new ArrayList<>();
		List<QuestionCreatedSummaryResponse.QuestionSummaryItem> failedItems = new ArrayList<>();
		for(QuestionRequest request : requests) {

			QuestionBasicResponse basicResponse = this.createQuestion(request);
			QuestionCreatedSummaryResponse.QuestionSummaryItem item =QuestionCreatedSummaryResponse.QuestionSummaryItem.builder()
					.id(basicResponse.getId())
					.title(basicResponse.getTitle())
					.build();
			if(basicResponse.getStatus() != QuestionStatus.ERROR) {
				successItems.add(item);
			} else {
				failedItems.add(item);
			}
		}
	return QuestionCreatedSummaryResponse.builder()
			.totalQuestionImported(successItems.size())
			.totalQuestionRequest(requests.size())
			.success(successItems)
			.failed(failedItems)
			.build();
	}

	@Transactional
    protected QuestionBasicResponse createQuestion(QuestionRequest request) {
		try {
			if(request.getStatus() == QuestionStatus.ERROR) {
				return QuestionBasicResponse.builder()
						.status(QuestionStatus.ERROR)
						.title(request.getTitle())
						.build();
			}
			//-------------------- CREATE BASE QUESTION ------------------
			String newCode = sequenceUtil.generateNewQuestionCode();
			Question currentQuestion = new Question();
			currentQuestion.setTitle(request.getTitle());
			currentQuestion.setContent(request.getContent());
			currentQuestion.setQuestionCode(newCode);
			currentQuestion.setIsSynchorus(false);
			currentQuestion.setEnable(false);
			currentQuestion.setStatus(QuestionStatus.REVIEW);
			String prefixCode = sequenceUtil.generatePrefixQuestionCode();
			currentQuestion.setPrefixCode(prefixCode);

			questionMapper.update(currentQuestion, questionMapper.toEntity(request));

			//-------------------- QUESTION DETAIL ------------------
			List<String> tableCreatedIds = new ArrayList<>();
			for(QuestionDetail questionDetail : currentQuestion.getQuestionDetails()) {
				if(Objects.isNull(questionDetail.getTestcases())) {
					throw new ValidateException("Testcase không được bỏ trống");
				}

				//---------------- CREATE TABLE -----------------
				//----------------- may be not use cause by error ---------------
				String typeDatabaseId = questionDetail.getTypeDatabase().getId();
//				String[] sqls = questionDetail.getSqlQuery().trim().split(";");
//				List<String> tableCreateIds = new ArrayList<>();
//				try {
//					List<SubmitResponse> results = sqlExecutorService.sqlExecutes(sqls,questionDetail.getTypeDatabase().getId());
//					for(SubmitResponse result : results) {
//						if(result.getTypeQuery() != null && result.getTableEffect() != null) {
//							String prefix = this.contextUtil.getUser().getUserPrefix()+currentQuestion.getPrefixCode()+"_";
//							if(result.getTypeQuery().equals(TypeQuestion.CREATE.name())) {
//								log.info("=============== CREATE TABLE {} ================",result.getTableEffect());
//								TableCreated tableCreated = new TableCreated();
//								tableCreated.setTableNameWithPrefix(result.getTableEffect());
//								tableCreated.setTypeDatabaseId(typeDatabaseId);
//								log.info("prefix: {}",prefix);
//								String queryCreated = result.getDescription().toLowerCase().replaceAll(prefix.toLowerCase(),"");
//								log.info("query created: {}", queryCreated);
//								tableCreated.setQuery(queryCreated);
//								tableCreated.setDisplayName(tableCreated.getTableNameWithPrefix());
//								tableCreateServcie.save(tableCreated);
//								tableCreateIds.add(tableCreated.getId());
//							} else if(result.getTypeQuery().equals(TypeQuestion.DROP.name())) {
//								tableCreateServcie.deleteTableByNameAndPrefix(result.getTableEffect(),typeDatabaseId);
//							}
//						}
//					}
//				} catch (Exception e) {
//					log.error("Cannot create table");
//				}

				questionDetail.setQuestion(currentQuestion);

				QuestionDetailRequest detailRequest = getQuestionDetailRequest(request.getQuestionDetails(),questionDetail.getTypeDatabase().getId());
//				if(Objects.nonNull(detailRequest)) {
//					log.info("************ check update OK ********");
//					List<TableDetail> tableDetails = new ArrayList<TableDetail>();
//					for (String tableId : tableCreateIds) {
//						TableDetail table = util.containTableId(questionDetail.getTableUses(), tableId);
//						if (table != null) {
//							tableDetails.add(table);
//						} else {
//							table = new TableDetail();
//							table.setQuestionDetail(questionDetail);
//							TableCreated tableCreated = new TableCreated();
//							tableCreated.setId(tableId);
//							table.setTableCreated(tableCreated);
//							tableDetails.add(table);
//						}
//					}
//					if(Objects.isNull(questionDetail.getTableUses())) {
//						questionDetail.setTableUses(tableDetails);
//					} else {
//						questionDetail.getTableUses().clear();
//						questionDetail.getTableUses().addAll(tableDetails);
//					}
//					tableCreatedIds.addAll(detailRequest.getTableUses());
//				}

				//----------------------- CREATE DATA TESTCASE -------------------
//				List<SubmitRequest> executes = new ArrayList<>();
				for(Testcase test : questionDetail.getTestcases()) {
					test.setQuestionDetail(questionDetail);
//					SubmitRequest submitRequest = new SubmitRequest();
//					submitRequest.setSql(test.getQuery_input());
//					submitRequest.setPrefixTable(String.join(",", tableCreateIds));
//					submitRequest.setQueryAnswer(questionDetail.getQueryAnswer());
//					submitRequest.setTypeDatabaseId(typeDatabaseId);
//					executes.add(submitRequest);
				}

				//------------------- call api create testcase (dont use) -------------------
				//
//				List<SubmitResponse> testcaseResponse = new ArrayList<>();
//				try {
//					testcaseResponse = submitService.createTestCase(executes,false);
//				} catch (Exception e) {
//					log.error("Errror when create output testcase");
//				}
//				for(int i = 0; i < testcaseResponse.size(); i++) {
//					Testcase testcase = questionDetail.getTestcases().get(i);
//					SubmitResponse testResponse = testcaseResponse.get(i);
//					if(testResponse.getStatus() == ErrorCode.SUCCESS.getCode()) {
//						testcase.setExpect_result(jsonUtil.convertObjectToString(testResponse.getResult()));
//					} else {
//						testcase.setExpect_result(testResponse.getResult().toString());
//					}
//				}
			}

			currentQuestion.setIsSynchorus(false);

			this.save(currentQuestion);

			return questionMapper.toBasicResponse(currentQuestion);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return QuestionBasicResponse.builder()
					.title(request.getTitle())
					.status(QuestionStatus.ERROR)
					.build();
		}
	}

	/**
	 * Read a one‐sheet Excel file where columns are:
	 * 0: stt
	 * 1: domain (ignored)
	 * 2: domain_description (ignored)
	 * 3: sql_complexity (ignored)
	 * 4: sql_complexity_description (ignored)
	 * 5: sql_type            → TypeQuestion
	 * 6: sql_task_type       (ignored)
	 * 7: sql_task_type_description (ignored)
	 * 8: content (noi dung cau hoi) ->QuestionRequest.content + sql_prompt
	 * 9: sql_prompt          → QuestionRequest.title
	 * 10: sql_context         → QuestionDetailRequest.sqlQuery
	 * 11: sql                 → QuestionDetailRequest.queryAnswer
	 * 12: sql_explanation    → QuestionRequest.constraints
	 * 13: level              → LevelQuestion
	 * 14–17: context1–4      → TestCaseRequest.query_input
	 */
	@Transactional(readOnly = true)
	public List<QuestionRequest> parseQuestionsFromExcel(MultipartFile file) throws IOException {
		try (Workbook wb = WorkbookFactory.create(file.getInputStream())) {
			Sheet sheet = wb.getSheetAt(0);
			List<QuestionRequest> out = new ArrayList<>();

			for (Row row : sheet) {
				int rowNum = row.getRowNum();
				String rowId = AppUtils.getCellValue(row.getCell(0));
				try {
					if (row.getRowNum() == 0) continue; // skip header

					// 1)---------------  build the QuestionRequest ---------------
					QuestionRequest qr = new QuestionRequest();
					String typeQuery = AppUtils.getCellValue(row.getCell(5));
					log.info("step 1");
					if(typeQuery == null || typeQuery.isEmpty()) {
						try {
							String query = AppUtils.getCellValue(row.getCell(11));
							query = SqlUtil.removeSqlComments(query);
							typeQuery = String.valueOf(sqlUtil.getQueryType(query));
						} catch (Exception e) {
							throw new RuntimeException("Lỗi khi xử lý sql ở column 10 hàng "+rowNum +" rowId: "+rowId);
						}
					}
					qr.setType(typeQuery != null && !typeQuery.equalsIgnoreCase("null") ? TypeQuestion.valueOf(typeQuery) : TypeQuestion.CREATE);
					String content = AppUtils.getCellValue(row.getCell(8));
					String prompt = AppUtils.getCellValue(row.getCell(9));
					qr.setContent(content+"\n"+prompt);
					qr.setTitle(prompt);
					qr.setConstraints(AppUtils.getCellValue(row.getCell(12)));
					LevelQuestion levelQuestion = LevelQuestion.EASY;
					String levelValue = AppUtils.getCellValue(row.getCell(13));
					if(levelValue.equals("1")) levelQuestion = LevelQuestion.EASY;
					else if(levelValue.equals("2")) levelQuestion = LevelQuestion.MEDIUM;
					else if(levelValue.equals("3")) levelQuestion = LevelQuestion.HARD;
					qr.setLevel(levelQuestion);
					qr.setIsShare(false);
					qr.setEnable(false);
					qr.setImage(null);
					qr.setPoint(3);            // adjust if you have a point‐column
					qr.setQuestionCode(null);   // will be set in create(...)

					// 2) --------------------------- BUILD QUESTION DETAIL ---------------------------
					QuestionDetailRequest detail = new QuestionDetailRequest();
					detail.setSqlQuery(AppUtils.getCellValue(row.getCell(10)));
					detail.setQueryAnswer(AppUtils.getCellValue(row.getCell(11)));
					detail.setTableUses(List.of());                  // no table mapping in this template
					detail.setTypeDatabase(null);                    // if you have a DB type column, map here
					detail.setMaxTimeExec(0);                        // or read from a column
					IdResponse database = new IdResponse();
					database.setId("2"); //sql server
					detail.setTypeDatabase(database);
					log.info("data id: {}",AppUtils.getCellValue(row.getCell(0)));
					// 3) ---------------------------- BUILD TESTCASE ---------------------------
					List<TestCaseRequest> tests = new ArrayList<>();
					for (int c = 14; c <= 17; c++) {
						String input = AppUtils.getCellValue(row.getCell(c));
						if (!input.isBlank()) {
							TestCaseRequest tc = new TestCaseRequest();
							String[] sqls = input.split(";");
							List<String> filterData = new ArrayList<>();
							for(String sql : sqls) {
								sql = SqlUtil.removeSqlComments(sql);
								TypeQuestion typeQuestion = TypeQuestion.TRIGGER;
								try {
									typeQuestion = sqlUtil.getQueryType(sql);
								} catch (Exception e) {
									log.error("Error when get sql type from sql");
								}
								if((typeQuestion == TypeQuestion.INSERT || typeQuestion == TypeQuestion.PROCEDURE) || (
										sql.toLowerCase().startsWith("insert")
								)) {
									filterData.add(sql);
								}
							}
							String joined = String.join(";", filterData);
							tc.setQuery_input(joined);
							tc.setExpect_result("");                // no expect column: leave blank or map as needed
							tc.setMaxTimeExec(200);
							tests.add(tc);
						}
					}
					detail.setTestcases(tests);
					qr.setQuestionDetails(List.of(detail));
					qr.setStatus(QuestionStatus.REVIEW);
					out.add(qr);
					log.info("========= Handle row done {}",row.getRowNum());
				} catch (Exception ex) {
					QuestionRequest request = new QuestionRequest();
					request.setStatus(QuestionStatus.ERROR);
					request.setTitle(ex.getMessage());
				}
			}
			return out;
		} catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


	private static List<CpTableCreatedRequest> getCpTableCreatedRequests(List<TableCreated> tableCreatedChooses) {
		List<CpTableCreatedRequest> cpTableCreatedRequests = new ArrayList<>();
		for(TableCreated tableCreated : tableCreatedChooses) {
			CpTableCreatedRequest cpTableCreatedRequest = new CpTableCreatedRequest();
			cpTableCreatedRequest.setQuery(tableCreated.getQuery());
			cpTableCreatedRequest.setName(tableCreated.getName());
			cpTableCreatedRequest.setPrefix(tableCreated.getPrefix());
			cpTableCreatedRequest.setTypeDatabaseId(tableCreated.getTypeDatabaseId());
			cpTableCreatedRequests.add(cpTableCreatedRequest);
		}
		return cpTableCreatedRequests;
	}

	public QuestionResponseAdmin getQuestionRoleAdminById(String id) {
		Question question = this.findById(id);
		QuestionResponseAdmin response = questionMapper.toResponse(question);

		for(QuestionDetailResponseAdmin detailResponseAdmin : response.getQuestionDetails()) {
			QuestionDetail questionDetail = getQuestionDetail(question, detailResponseAdmin.getId());
			if(Objects.nonNull(questionDetail)) {
				List<String> tableIds = new ArrayList<String>();
				for(TableDetail table : questionDetail.getTableUses()) {
					tableIds.add(table.getTableCreated().getId());
				}
				detailResponseAdmin.setTableUses(tableIds);
			}
		}

		//convert constrains
		if(Objects.nonNull(question.getConstraints()) && !question.getConstraints().isEmpty()) {
			List<ConstraintResponse> constraints = jsonUtil.fromJson(new TypeReference<List<ConstraintResponse>>() {}, question.getConstraints());
			response.setConstraints(constraints);
		}
		return response;
	}
	public Page<QuestionBasicResponse> getQuestionListDetail(Pageable pageable, String keyword, LevelQuestion level, String typeDatabaseId, TypeQuestion typeQuestion,String createdBy) {
		if (pageable.getSort().isUnsorted()) {
			pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
									Sort.by(Sort.Direction.DESC, "createdAt"));
		}
		Page<Question> questionPage;
		if(contextUtil.getUser().getRole().equals(Role.ADMIN.name())) {
			questionPage = this.repository.findAll((root, query, criteriaBuilder) -> {
				List<Predicate> predicates = new ArrayList<>();

				if (keyword != null && !keyword.isBlank()) {
					Predicate titlePredicate = criteriaBuilder.like(
							criteriaBuilder.lower(root.get("title")), "%" + keyword.trim().toLowerCase() + "%"
					);
					Predicate questionCodePredicate = criteriaBuilder.like(
							criteriaBuilder.lower(root.get("questionCode")), "%" + keyword.trim().toLowerCase() + "%"
					);
					predicates.add(criteriaBuilder.or(titlePredicate, questionCodePredicate));
				}

				if(level != null) {
					predicates.add(criteriaBuilder.equal(root.get("level"), level));
				}
				if(typeQuestion != null) {
					predicates.add(criteriaBuilder.equal(root.get("type"), typeQuestion));
				}
				if (typeDatabaseId != null) {
					// Create a join between Question and QuestionDetail
					Join<Object, Object> questionDetailsJoin = root.join("questionDetails");

					// Add the predicate to filter by typeDatabase.id
					predicates.add(criteriaBuilder.equal(
							questionDetailsJoin.get("typeDatabase").get("id"), typeDatabaseId
					));
				}
				if(createdBy != null) {
					predicates.add(criteriaBuilder.equal(root.get("createdBy"), createdBy));
				}
				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}, pageable);
		} else {
			questionPage = this.repository.findAllQuestionsByUserId(pageable,contextUtil.getUserId(),keyword,
					Objects.nonNull(level) ? level.name() : null, Objects.nonNull(typeQuestion) ? typeQuestion.name() : null,
					typeDatabaseId);
		}
		return questionPage.map(this::toQuestionBaseResponseWithUserCreated);
	}

	public Page<QuestionBasicResponse> getBasicQuestionListEnable(Pageable pageable,String keyword) {
		Page<Question> questionPage = this.repository.findEnabledQuestions(pageable,keyword);
		List<QuestionBasicResponse> updatedResponses = questionPage
				.stream()
				.map(question -> {
					QuestionBasicResponse response = questionMapper.toBasicResponse(question);
					QuestionStat questionSubInfo = statService.getQuestionSubInfo(question.getId());
					response.setTotalSub(questionSubInfo.getTotalSubmissions());
					float acceptance = (float) questionSubInfo.getTotalSubmitAc() / questionSubInfo.getTotalSubmissions() * 100;
					acceptance = (float) (Math.round(acceptance * 100.0) / 100.0);  // Round to two decimal places
					response.setAcceptance(acceptance);
					return response;
				})
				.collect(Collectors.toList());

		// Convert the updated list back to a Page with the original pageable and total elements
		return new PageImpl<>(updatedResponses, pageable, questionPage.getTotalElements());
	}

	public QuestionDetail getQuestionDetail(String questionId,String typeDatabaseId) {
		log.info("questionID: {}  typeDatabaseId: {}",questionId,typeDatabaseId);
		return questionDetailRepository.findByQuestionIdAndTypeDatabaseId(questionId,typeDatabaseId);
	}

	public QuestionDetail fetchQuestionDetail(String questionId, String typeDatabaseId) {
		return questionDetailRepository.fetchQuestionDetailByQuestionIdAndDatabaseId(questionId,typeDatabaseId);
	}

	public Page<UserDoneQuestionProjection> getListUserDoneQuestion(Pageable pageable,String questionId) {
		return questionRepository.findListUserDoneQuestion(pageable,questionId);
	}

	private DataRequest createContestData(Question question) {
		DataRequest contestDataRequest = new DataRequest();
		List<QuestionCpRequest> questionCpRequests = new ArrayList<>();
		for(QuestionDetail questionDetail : question.getQuestionDetails()) {
			QuestionCpRequest questionCpRequest = new QuestionCpRequest();
			questionCpRequest.setQuestionId(question.getId());
			questionCpRequest.setAnswer(questionDetail.getQueryAnswer());
			questionCpRequest.setDatabaseId(questionDetail.getTypeDatabase().getId());
			List<TestcaseCpRequest> testcaseCpRequests = createTestcaseCpRequests(questionDetail, questionCpRequest);
			questionCpRequest.setTestcases(testcaseCpRequests);
			questionCpRequests.add(questionCpRequest);
		}
		contestDataRequest.setQuestions(questionCpRequests);
		return contestDataRequest;
	}

	private List<TestcaseCpRequest> createTestcaseCpRequests(QuestionDetail questionDetail, QuestionCpRequest questionCpRequest) {
		List<TestcaseCpRequest> testcaseCpRequests = new ArrayList<>();
		for(Testcase testcase : questionDetail.getTestcases()) {
			TestcaseCpRequest testcaseCpRequest = new TestcaseCpRequest();
			testcaseCpRequest.setDatabaseId(questionDetail.getTypeDatabase().getId());
			testcaseCpRequest.setInputData(testcase.getQuery_input());
			testcaseCpRequest.setTestcaseDevId(testcase.getId());
			testcaseCpRequest.setExpectOutput(testcase.getExpect_result());
			testcaseCpRequest.setMaxTimeExec(testcase.getMaxTimeExec());
			testcaseCpRequests.add(testcaseCpRequest);
		}
		return testcaseCpRequests;
	}

	private QuestionDetailRequest getQuestionDetailRequest(List<QuestionDetailRequest> detailRequests,String typeDatabaseId) {
		for(QuestionDetailRequest detailRequest : detailRequests) {
			if(Objects.nonNull(typeDatabaseId) && detailRequest.getTypeDatabase().getId().equals(typeDatabaseId)) {
				return detailRequest;
			}
		}
		log.info("Get questionDetailRequest null for id {}",typeDatabaseId);
		return null;
	}

	private QuestionDetail getQuestionDetail(Question question,String id) {
		for(QuestionDetail questionDetail : question.getQuestionDetails()) {
			if(questionDetail.getId().equals(id)) {
				return questionDetail;
			}
		}
		return null;
	}

	private QuestionBasicResponse toQuestionBaseResponseWithUserCreated(Question question) {
		QuestionBasicResponse response = questionMapper.toBasicResponse(question);
		response.setUserCreated(userService.toUserBaseResponse(question.getCreatedBy()));
		return response;
	}
}
