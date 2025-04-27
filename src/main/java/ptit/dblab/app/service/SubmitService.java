package ptit.dblab.app.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;

import ptit.dblab.app.dto.response.*;
import ptit.dblab.shared.enumerate.TypeQuestion;
import ptit.dblab.shared.exception.core.ValidateException;
import ptit.dblab.shared.utils.ContextUtil;
import ptit.dblab.shared.utils.JsonUtil;
import ptit.dblab.shared.utils.SqlUtil;
import ptit.dblab.app.dto.request.CpSubmitRequest;
import ptit.dblab.app.dto.request.EvaluateRequest;
import ptit.dblab.app.entity.*;
import ptit.dblab.app.enumerate.TypeSubmitEnum;
import ptit.dblab.app.event.dto.SubmitMessage;
import ptit.dblab.app.event.dto.SubmitResultMessage;
import ptit.dblab.app.event.producer.SubmitProducer;
import ptit.dblab.app.event.producer.SubmitResultProducer;
import ptit.dblab.app.feignClient.SubmitServiceClient;
import ptit.dblab.app.interfaceProjection.TableCreatedInfProjection;
import ptit.dblab.app.repository.TableCreateRepository;
import ptit.dblab.app.repository.TestcaseRepository;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ptit.dblab.app.dto.request.SubmitRequest;
import ptit.dblab.app.enumerate.ErrorCode;

import net.sf.jsqlparser.JSQLParserException;
import org.springframework.web.multipart.MultipartFile;
import ptit.dblab.app.utils.AppUtils;
import ptit.dblab.app.utils.SequenceUtil;
import ptit.dblab.app.utils.TableUtil;
import ptit.dblab.app.utils.Util;

@RequiredArgsConstructor
@Service
@Slf4j
public class SubmitService{

	private final TableCreateServcie tableCreateServcie;

	private final SqlExecutorService sqlExecutorService;

	private final Util util;

	private final SqlUtil sqlUtil;

	private final TableUtil sqlUtilv2;

	private final JsonUtil jsonUtil;


	private final TableCreateRepository tableCreateRepository;
	private final ContextUtil contextUtil;
	private final SequenceUtil sequenceUtil;
	private final SubmitProducer submitProducer;
	private final QuestionService questionService;
	private final ProcessService processService;
	private final SubmitHistoryService submitHistoryService;
	private final SubmitContestService submitContestService;
	private final SubmitResultProducer submitResultProducer;
	private final ContestService contestService;
	private final SubmitServiceClient submitServiceClient;
	private final TestcaseRepository testcaseRepository;


	//send to kafka
	public SubmitResponse scoreProcess(SubmitRequest request) throws Exception {
		String query = request.getSql().trim();
		SubmitResponse response = util.createResponseScrored(request,null);
		String sessionPrefix = sequenceUtil.generateSessionPrefix();
		submitProducer.send(SubmitMessage.builder()
				.sql(query)
				.userId(contextUtil.getUserId())
				.questionId(request.getQuestionId())
				.questionContestId(request.getQuestionContestId())
				.sessionPrefix(sessionPrefix)
				.submitId(response.getSubmitId())
				.typeDatabaseId(request.getTypeDatabaseId())
				.isSubmitContest(request.isSubmitContest())
				.build());
		return response;
	}

	public SubmitResponse sendSubmit(SubmitRequest request, HttpServletRequest clientRequest) throws Exception {
		String query = SqlUtil.removeSqlComments(request.getSql().trim());
		String clientIp = AppUtils.getClientIp(clientRequest);
		request.setSql(query);
		SubmitResponse response = util.createResponseScrored(request,clientIp);
		QuestionDetail currentQuestion = questionService.getQuestionDetail(request.getQuestionId(),request.getTypeDatabaseId());
		if(Objects.isNull(currentQuestion)) {
			throw new ValidateException(ErrorCode.RESOURCE_NOT_FOUND.getDescription());
		}
		if(request.isSubmitContest()) {
			if(Objects.isNull(request.getContestId())) {
				throw new ValidateException(ErrorCode.CANNOT_SUBMIT_BECAUSE_CONTEST_NOT_OPEN.getDescription());
			}
			if(!contestService.isContestOpen(request.getContestId())) {
				throw new ValidateException(ErrorCode.CANNOT_SUBMIT_BECAUSE_CONTEST_NOT_OPEN.getDescription());
			}
		}
		List<TableCreated> tableUses = currentQuestion.getListTableUseCreated();
		String[] tableListGenerate = AppUtils.convertToFormattedArrayTableUses(tableUses);

		CpSubmitRequest cpSubmitRequest = new CpSubmitRequest();
		cpSubmitRequest.setSql(query);
		cpSubmitRequest.setTypeSubmit(request.isSubmitContest() ? TypeSubmitEnum.CONTEST : TypeSubmitEnum.PRACTICE);
		cpSubmitRequest.setQuestionId(request.getQuestionId());
		cpSubmitRequest.setDatabaseId(request.getTypeDatabaseId());
		cpSubmitRequest.setUserSubId(contextUtil.getUserId());
		cpSubmitRequest.setSubmitId(response.getSubmitId());
		cpSubmitRequest.setTableListGenerate(tableListGenerate);
		cpSubmitRequest.setConstraints(currentQuestion.getQuestion().getConstraints());
		if(request.isSubmitContest()) {
			cpSubmitRequest.setDevelopPayload(request.getQuestionContestId());
		}
		ResponseEntity<?> submitResponse = submitServiceClient.sendSubmit(cpSubmitRequest);
		if(submitResponse.getStatusCode().is2xxSuccessful()) {
			return response;
		}
		throw new ValidateException(ErrorCode.CANNOT_CALL_PARTY_SERVICE.getDescription());
	}

	public void sendRetrySubmit(SubmitRequest request, String submitId, String userId) throws Exception {
		String query = request.getSql().trim();
		QuestionDetail currentQuestion = questionService.fetchQuestionDetail(request.getQuestionId(),request.getTypeDatabaseId());
		if(Objects.isNull(currentQuestion)) {
			throw new ValidateException(ErrorCode.RESOURCE_NOT_FOUND.getDescription());
		}
		List<TableCreated> tableUses = currentQuestion.getListTableUseCreated();
		String[] tableListGenerate = AppUtils.convertToFormattedArrayTableUses(tableUses);
		CpSubmitRequest cpSubmitRequest = new CpSubmitRequest();
		cpSubmitRequest.setSql(query);
		cpSubmitRequest.setTypeSubmit(request.isSubmitContest() ? TypeSubmitEnum.CONTEST : TypeSubmitEnum.PRACTICE);
		cpSubmitRequest.setQuestionId(request.getQuestionId());
		cpSubmitRequest.setDatabaseId(request.getTypeDatabaseId());
		cpSubmitRequest.setUserSubId(userId);
		cpSubmitRequest.setSubmitId(submitId);
		cpSubmitRequest.setTableListGenerate(tableListGenerate);
		if(request.isSubmitContest()) {
			cpSubmitRequest.setDevelopPayload(request.getQuestionContestId());
		}
		ResponseEntity<?> submitResponse = submitServiceClient.sendSubmit(cpSubmitRequest);
		if(!submitResponse.getStatusCode().is2xxSuccessful()) {
			throw new ValidateException(ErrorCode.CANNOT_CALL_PARTY_SERVICE.getDescription());
		}
	}

	//send to api submit svc

	public SubmitResponse executeSqlUser(String query,String questionId,String typeDatabaseId) throws Exception {
		//remove comment in sql
		query = SqlUtil.removeSqlComments(query);
		log.info("query:"+query);
		//validate
		SubmitResponse response = new SubmitResponse();
		if(!sqlUtil.validateQuery(query)) {
			return util.createResponse(ErrorCode.ERROR.getCode(), "Cannot execute sql: "+query);
		}


		QuestionDetail currentQuestion = questionService.getQuestionDetail(questionId,typeDatabaseId);

		if(currentQuestion == null) {
			throw new JSQLParserException("question not found!");
		}

		TypeQuestion typeQuestion = currentQuestion.getTypeQuestion();

		if(typeQuestion != sqlUtil.getQueryType(query)) {
			return util.createResponse(ErrorCode.ERROR.getCode(), "access deny for query: "+query);
		}

		String queryBody = query;

		//check constraints
		if(currentQuestion.getQuestion().getConstraints() != null && !currentQuestion.getQuestion().getConstraints().isEmpty()) {
			List<ConstraintResponse> constraints = util.convertToConstraints(currentQuestion.getQuestion().getConstraints());
			String checkConstraints = util.validateConstraints(queryBody, constraints);
			if(!checkConstraints.equals("VALID")) {
				return util.createResponse(ErrorCode.ERROR.getCode(), checkConstraints);
			}
		}

		if(typeQuestion == TypeQuestion.PROCEDURE) {
			if(sqlUtil.isProcedureQuery(queryBody) && !sqlUtil.isCreateProcedure(queryBody)) {
				return util.createResponse(ErrorCode.ERROR.getCode(), "access deny for query: "+query);
			}
			queryBody = sqlUtil.extractProcedureBody(query);
		}
		Set<String> tableNames = sqlUtil.getTableNames(queryBody);
		List<TableDetail> tableUses = currentQuestion.getTableUses();
		TableDetail tableUse = null;
		for(String tableName : tableNames) {
			tableUse = util.containTableName(tableUses,tableName);
			if(Objects.isNull(tableUse)) {
				return util.createResponse(ErrorCode.ERROR.getCode(), "Cannot execute sql: "+query);
			}
		}
        response = switch (typeQuestion) {
			case SELECT,PROCEDURE -> executeForSelectQuery(currentQuestion, query, false);
            case UPDATE, INSERT, DELETE, DROP, ALTER, TRUNCATE, CREATE ->
                    executeForChangeDataQuery(currentQuestion, query, false);
            default -> response;
        };
		return response;
	}

	public List<SubmitResponse> submitRoleAdmin(String queries,String tablePrefix,String[] tableUses, String typeDatabaseId) throws Exception {
		String[] sqls = queries.trim().split(";");

		//handle remove comment in query
		for (int i = 0; i < sqls.length; i++) {
			sqls[i] = SqlUtil.removeSqlComments(sqls[i]);
		}
		//check for procedure
		if(sqlUtil.isProcedureQuery(queries) && !sqlUtil.isCreateProcedure(queries)) {
			if(Objects.nonNull(tableUses) && tableUses.length > 0) {
				tablePrefix = "";
			}
			queries = sqlUtil.addPrefixToProcedureCall(queries,contextUtil.getUser().getUserPrefix()+tablePrefix);
			log.info("===== query call procedure: {}",queries);
			SubmitResponse result = sqlExecutorService.sqlExecuteSingleSql(queries,typeDatabaseId);
			return Collections.singletonList(result);
		}

		if(sqlUtil.isCreateProcedure(queries)) {
			List<String> filteredSqls = new ArrayList<>();
			for(String sql : sqls) {
				if(sqlUtil.isCreateProcedure(sql)) {
					sql += ";end;";
					filteredSqls.add(sql);
				}
			}
			sqls = filteredSqls.toArray(new String[0]);
		}


		if(!sqlUtil.validateQueries(sqls)) {
			return Collections.singletonList(util.createResponse(ErrorCode.ERROR.getCode(), "SQL query is not valid"));
		}

		if(Objects.nonNull(tableUses) && tableUses.length > 0) {
			List<String> tableUseIds = Arrays.asList(tableUses);
			List<TableCreated> tableCreateds = tableCreateRepository.findByIdIn(tableUseIds);
			Map<String,String> prefixList = new HashMap<>();
			for(TableCreated tableCreated : tableCreateds) {
				if(!prefixList.containsKey(tableCreated.getName().toLowerCase())) {
					prefixList.put(tableCreated.getName().toLowerCase(), tableCreated.getPrefix());
				}
			}
			for (int i = 0; i < sqls.length; i++) {
				TypeQuestion typeQuestion = sqlUtil.getQueryType(sqls[i]);
				if(typeQuestion == TypeQuestion.CREATE) {
					sqls[i] = sqlUtil.addPrefixSQL(sqls[i], tablePrefix,false);
				}
				else {
					sqls[i] = sqlUtil.addMultiPrefixSQL(sqls[i], prefixList,false);
				}
			}
		} else {
			for (int i = 0; i < sqls.length; i++) {
				sqls[i] = sqlUtil.addPrefixSQL(sqls[i], tablePrefix,false);
			}
		}

		List<SubmitResponse> results = sqlExecutorService.sqlExecutes(sqls,typeDatabaseId);
		for(SubmitResponse result : results) {
			if(result.getTypeQuery() != null && result.getTableEffect() != null) {
				log.info("result: {}",	result.toString());
				String prefix = this.contextUtil.getUser().getUserPrefix()+tablePrefix+"_";
				if(result.getTypeQuery().equals(TypeQuestion.CREATE.name())) {
					log.info("=============== CREATE TABLE {} ================",result.getTableEffect());
					TableCreated tableCreated = new TableCreated();
					tableCreated.setTableNameWithPrefix(result.getTableEffect());
					tableCreated.setTypeDatabaseId(typeDatabaseId);
					log.info("prefix: {}",prefix);
					String queryCreated = result.getDescription().toLowerCase().replaceAll(prefix.toLowerCase(),"");
					log.info("query created: {}", queryCreated);
					tableCreated.setQuery(queryCreated);
					tableCreated.setDisplayName(tableCreated.getTableNameWithPrefix());
					tableCreateServcie.save(tableCreated);
				} else if(result.getTypeQuery().equals(TypeQuestion.DROP.name())) {
					tableCreateServcie.deleteTableByNameAndPrefix(result.getTableEffect(),typeDatabaseId);
				}
			}
		}
		return results;
	}

	public List<SubmitResponse> createTestCase(List<SubmitRequest> executes,boolean isUseFile) throws Exception {
		List<SubmitResponse> results = new ArrayList<SubmitResponse>();
		List<TableCreated> tableUses = new ArrayList<>();
		boolean isSetupTableForSelect = false;
		if(executes.isEmpty()) {
			throw new ValidateException("Testcase không được để trống!",ErrorCode.REQUIRED_EXISTS.getCode());
		}
		TypeQuestion typeAnswer = sqlUtil.getQueryType(executes.get(0).getQueryAnswer());
		if(Objects.isNull(typeAnswer) || !typeAnswer.equals(executes.get(0).getTypeQuestion())) {
			throw new ValidateException("Loaị câu hỏi không khớp với loại query đáp án!", ErrorCode.NOT_MATCH_TYPE_QUERY.getCode());
		}
		int number = 1;
		String typeDatabaseId = null;
		String procedureName = null;
		try {
			for (SubmitRequest exec : executes) {
				log.info("*********** create testcase " + number);
				number++;
				exec.setSql(SqlUtil.removeSqlComments(exec.getSql()));
				exec.setQueryAnswer(SqlUtil.removeSqlComments(exec.getQueryAnswer()));
				if (exec.getTypeQuestion() == null) {
					results.add(util.createResponse(ErrorCode.ERROR.getCode(), "type question must be required"));
					return results;
				}
				if(exec.getTypeDatabaseId() == null) {
					results.add(util.createResponse(ErrorCode.ERROR.getCode(), "typeDatabase must be required"));
					return results;
				}
				if(typeDatabaseId == null) {
					typeDatabaseId = exec.getTypeDatabaseId();
				}
				String[] sqls = exec.getSql().trim().split(";");

				if (!sqlUtil.validateQueries(sqls) && (exec.getTypeQuestion() != TypeQuestion.CREATE && exec.getTypeQuestion() != TypeQuestion.ALTER && exec.getTypeQuestion() != TypeQuestion.INSERT)) {
					results.add(util.createResponse(ErrorCode.ERROR.getCode(), "SQL query is not valid"));
				} else {
					String[] temps = exec.getPrefixTable().trim().split(",");
					String sqlProcess = "";
					if (!exec.getPrefixTable().contains("-")) {
						sqlProcess = sqlUtil.addPrefixSQL(exec.getQueryAnswer(), contextUtil.getUser().getSessionPrefix(), true);
					} else {
						List<String> tableUseIds = Arrays.asList(temps);
						if (tableUseIds.isEmpty()) {
							results.add(util.createResponse(ErrorCode.ERROR.getCode(), "cannot execute query: " + exec.getSql()));
							return results;
						}
						tableUses = tableCreateRepository.findByIdIn(tableUseIds);
						//sort tableUses
						processService.sortTables(tableUses);
						sqlProcess = sqlUtil.addPrefixSQL(exec.getQueryAnswer().trim(), contextUtil.getUser().getSessionPrefix(), true);
					}
					//***************** type select *******************
					if (exec.getTypeQuestion() == TypeQuestion.SELECT || exec.getTypeQuestion() == TypeQuestion.PROCEDURE) {
						String queryAnswer = sqlProcess;
						if (!isSetupTableForSelect) {
							setupEnviroment(tableUses,exec.getTypeDatabaseId(),exec.getTypeQuestion());
							isSetupTableForSelect = true;
							if(exec.getTypeQuestion() == TypeQuestion.PROCEDURE) {
								procedureName = sqlUtil.extractProcedureNameWithNoParams(sqlProcess);
								if(!sqlUtilv2.createProcedure(sqlProcess,exec.getTypeDatabaseId())) {
									throw new ValidateException("Error when create procedure: " + exec.getQueryAnswer());
								};
							}
						}
						if(exec.getTypeQuestion() == TypeQuestion.PROCEDURE) {
							queryAnswer = sqlUtil.getQueryCallOrDropProcedure(exec.getSql());
							if(Objects.isNull(queryAnswer)) {
								throw new ValidateException("Must be have query check procedure in input for each testcase");
							}
							queryAnswer = sqlUtil.addPrefixSQL(queryAnswer, contextUtil.getUser().getSessionPrefix(), true);
							log.info("procedure convert: {}", queryAnswer);
						}

						sqlUtilv2.clearData(tableUses,exec.getTypeDatabaseId());
						if (!setupData(exec.getSql(),exec.getTypeDatabaseId(),exec.getTypeQuestion())) {
							results.add(util.createResponse(ErrorCode.ERROR.getCode(), "Failed when run query create output data: "+exec.getSql()));
							return results;
						}
						SubmitResponse response = sqlExecutorService.sqlExecuteSingleSql(queryAnswer,exec.getTypeDatabaseId());
						if (isUseFile) {
							response.setQueryInput(exec.getSql());
						}
						results.add(response);
					} else {
						//***************** type another *******************
						try {
							if(exec.getTypeQuestion() == TypeQuestion.CREATE) {
								List<TableCreated> tableGenerates = new ArrayList<>(tableUses);
								String tableUse = sqlUtil.getTableName(exec.getQueryAnswer());
								tableGenerates.remove(sqlUtilv2.getTable(tableUse,tableUses));
								setupEnviroment(tableGenerates,exec.getTypeDatabaseId(),exec.getTypeQuestion());
							} else {
								setupEnviroment(tableUses,exec.getTypeDatabaseId(),exec.getTypeQuestion());
								sqlUtilv2.clearData(tableUses,exec.getTypeDatabaseId());
							}
							if (!setupData(exec.getSql(),exec.getTypeDatabaseId(),exec.getTypeQuestion())) {
								results.add(util.createResponse(ErrorCode.ERROR.getCode(), "Query create data must be type INSERT"));
								return results;
							}


							String tablequery = sqlUtil.getTableName(exec.getQueryAnswer());

							TableCreated currentTableUse = sqlUtilv2.getTable(tablequery, tableUses);
							if (Objects.isNull(currentTableUse)) {
								results.add(util.createResponse(ErrorCode.ERROR.getCode(), "cannot execute query: " + exec.getSql()));
								return results;
							}
							String tableNameTemp = "temp_"+contextUtil.getUser().getSessionPrefix() + "_" + currentTableUse.getName();
							String queryCheck = sqlUtilv2.getQueryCheck(tableNameTemp,exec.getTypeQuestion(),exec.getTypeDatabaseId());
							log.info("==== query check: {}",queryCheck);
							SubmitResponse responseQuery = sqlExecutorService.sqlExecuteSingleSql(sqlProcess,exec.getTypeDatabaseId());
							if (responseQuery.getStatus() == ErrorCode.SUCCESS.getCode()) {
								SubmitResponse response = sqlExecutorService.sqlExecuteSingleSql(queryCheck,exec.getTypeDatabaseId());
								if (isUseFile) {
									response.setQueryInput(exec.getSql());
								}
								log.info("====== result: {}",response.getResult());
								results.add(response);
							} else {
								results.add(util.createResponse(ErrorCode.ERROR.getCode(), "cannot execute query: " + exec.getSql()));
								return results;
							}

						} catch (Exception e) {
							log.error(e.getMessage());
							results.add(util.createResponse(ErrorCode.ERROR.getCode(), "cannot execute query: " + exec.getSql()));
						} finally {
							sqlUtilv2.dropTemporaryTables(tableUses,exec.getTypeDatabaseId());
							if(Objects.nonNull(procedureName)) {
								log.info("procedure name: {}", procedureName);
								sqlUtilv2.dropProcedure(procedureName,exec.getTypeDatabaseId());
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			results.add(util.createResponse(ErrorCode.ERROR.getCode(), e.getMessage()));
		} finally {
			sqlUtilv2.dropTemporaryTables(tableUses,typeDatabaseId);
		}

		return results;
	}

	private SubmitResponse executeForSelectQuery(QuestionDetail currentQuestion, String query, boolean isSubmit) throws Exception {
		SubmitResponse response = new SubmitResponse();
		String procedureName = null;
		try {
			//setup enviroment
			setupEnviroment(currentQuestion);
			String newQuery = sqlUtil.addPrefixSQL(query, contextUtil.getUser().getSessionPrefix(),true);
			log.info("new query: {}", newQuery);
			if(currentQuestion.getTypeQuestion() == TypeQuestion.PROCEDURE) {
				procedureName = sqlUtil.extractProcedureNameWithNoParams(newQuery);
				log.info("procedure name: {}", procedureName);
			}
			response = checkResult(currentQuestion, newQuery,currentQuestion.getTypeQuestion(),isSubmit);
			return response;
		} catch (Exception e) {
			throw new JSQLParserException(e.getMessage());
		} finally {
			sqlUtilv2.dropTemporaryTables(currentQuestion.getListTableUseCreated(),currentQuestion.getTypeDatabase().getId());
			if(currentQuestion.getTypeQuestion() == TypeQuestion.PROCEDURE) {
				sqlUtilv2.dropProcedure(procedureName,currentQuestion.getTypeDatabase().getId());
			}
		}
	}

	private SubmitResponse executeForChangeDataQuery(QuestionDetail currentQuestion, String query, boolean isSubmit) throws Exception {
		log.info("*************** start score for change query *********************");
		SubmitResponse response = new SubmitResponse();
		List<TableCreated> tableUses = currentQuestion.getListTableUseCreated();
		try {
			if(currentQuestion.getTypeQuestion() == TypeQuestion.CREATE) {
				List<TableCreated> tableForCreate = new ArrayList<>(tableUses);
				String tableNameCreate = sqlUtil.getTableName(query);
				tableForCreate.remove(sqlUtilv2.getTable(tableNameCreate,tableUses));
				setupEnviroment(tableForCreate,currentQuestion.getTypeDatabase().getId(),currentQuestion.getTypeQuestion());
			} else {
				setupEnviroment(currentQuestion);
			}

			String sqlProcess = sqlUtil.addPrefixSQL(query, contextUtil.getUser().getSessionPrefix(), true);

			response = checkResult(currentQuestion,sqlProcess,currentQuestion.getTypeQuestion(),isSubmit);
		} catch (Exception e) {
			e.printStackTrace();
			throw new JSQLParserException(e.getMessage());
		} finally {
			sqlUtilv2.dropTemporaryTables(tableUses,currentQuestion.getTypeDatabase().getId());
		}
		return response;
	}

	private SubmitResponse checkResult(QuestionDetail currentQuestion, String queryProcess, TypeQuestion typeQuestion, boolean isSubmit) throws Exception {
		List<TableCreated> tableUses = currentQuestion.getListTableUseCreated();
		SubmitResponse response;
		if(typeQuestion != TypeQuestion.CREATE && typeQuestion != TypeQuestion.ALTER) {
			sqlUtilv2.clearData(tableUses,currentQuestion.getTypeDatabase().getId());
			if (!setupData(currentQuestion.getTestcases().get(0),currentQuestion.getTypeDatabase().getId(),typeQuestion)) {
				throw new Exception("error when setup data test");
			}
		}

		if (typeQuestion == TypeQuestion.SELECT || typeQuestion == TypeQuestion.PROCEDURE) {
			return sqlExecutorService.sqlExecuteSingleSql(queryProcess,currentQuestion.getTypeDatabase().getId());
		} else {
			String tablequery = sqlUtil.getTableName(queryProcess);
			String querycheck = sqlUtilv2.getQueryCheck(tablequery,typeQuestion,currentQuestion.getTypeDatabase().getId());
			SubmitResponse resultSubmit = sqlExecutorService.sqlExecuteSingleSql(queryProcess,currentQuestion.getTypeDatabase().getId());
			if (resultSubmit.getStatus() == ErrorCode.SUCCESS.getCode()) {
				return sqlExecutorService.sqlExecuteSingleSql(querycheck,currentQuestion.getTypeDatabase().getId());
			} else {
				return resultSubmit;
			}
		}
	}

	private void setupEnviroment(QuestionDetail currentQuestion) throws Exception {
		List<TableCreated> tableUses = currentQuestion.getListTableUseCreated();
		if(currentQuestion.getTypeQuestion() == TypeQuestion.CREATE) {
			return;
		}
		sqlUtilv2.dropTemporaryTables(tableUses,currentQuestion.getTypeDatabase().getId());
		if(!sqlUtilv2.createTemporaryTables(tableUses,currentQuestion.getTypeDatabase().getId())) {
			throw new JSQLParserException("error when create temporary table!");
		}
	}

	private void setupEnviroment(List<TableCreated> tableUses, String typeDatabaseId,TypeQuestion typeQuestion) throws Exception {
		sqlUtilv2.dropTemporaryTables(tableUses,typeDatabaseId);
		if(!sqlUtilv2.createTemporaryTables(tableUses,typeDatabaseId)) {
			throw new JSQLParserException("error when create temporary table!");
		}
	}

	private boolean setupData(Testcase testcase, String typeDatabaseId, TypeQuestion typeQuestion) throws Exception {
		return setupData(testcase.getQuery_input().trim(),typeDatabaseId,typeQuestion);
	}

	private boolean setupData(String queries, String typeDatabaseId,TypeQuestion typeQuestion) throws Exception {
		if(typeQuestion == TypeQuestion.ALTER || typeQuestion == TypeQuestion.CREATE || typeQuestion == TypeQuestion.INSERT) {
			return true;
		}
		String[] sqls = queries.trim().split(";");
		List<String> queryUses = new ArrayList<>();
		if(TypeQuestion.PROCEDURE != typeQuestion && !sqlUtil.validateQueries(sqls)) {
			return false;
		}
		for (int i = 0; i < sqls.length; i++) {
			String trimmedQuery = sqls[i].replaceAll("[\\s\\n]+", " ").trim();
			if(TypeQuestion.INSERT != sqlUtil.getQueryType(trimmedQuery) && TypeQuestion.PROCEDURE != sqlUtil.getQueryType(trimmedQuery)) {
				return false;
			}
			if(sqlUtil.isProcedureQuery(trimmedQuery)) {
				continue;
			}
			sqls[i] = sqlUtil.addPrefixSQL(trimmedQuery, contextUtil.getUser().getSessionPrefix(), true);
			queryUses.add(sqls[i]);
		}
		List<SubmitResponse> results = sqlExecutorService.sqlExecutes(queryUses.toArray(new String[0]), typeDatabaseId);
		for (SubmitResponse submitResponse : results) {
			if (submitResponse.getStatus() == ErrorCode.ERROR.getCode()) {
				return false;
			}
		}
		log.info("setup data done!");
		return true;
	}

	public List<SubmitRequest> readTestCaseFromExcelFile(MultipartFile file, String answer,TypeQuestion typeQuestion,String prefixTable) throws IOException {
		List<SubmitRequest> requests = new ArrayList<>();
		log.info("********** Start reading testcase data from excel **********");
		try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
			Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
			for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Start from row 1 to skip the header
				Row row = sheet.getRow(i);
				if (row != null) {
					SubmitRequest request = new SubmitRequest();
					request.setSql(row.getCell(1).getStringCellValue());
					request.setTimeOutExpect((int) row.getCell(2).getNumericCellValue());
					request.setQueryAnswer(answer);
					request.setTypeQuestion(typeQuestion);
					request.setPrefixTable(prefixTable);
					requests.add(request);
				}
			}
		}
		log.info("********** read total {} testcase data done ! **********",requests.size());
		return requests;
	}

	public List<SubmitRequest> readTestCaseFromFile(MultipartFile file,String answer,TypeQuestion typeQuestion,String prefixTable, String typeDatabaseId) throws IOException {
		List<SubmitRequest> requests = new ArrayList<>();
		log.info("********** Start reading testcase data from file **********");
		StringBuilder currentContent = new StringBuilder(); // To accumulate the content of the current case
		String currentCase = null; // To track the current case
		List<String> queries = new ArrayList<>();
		// Use InputStreamReader to read the content of the MultipartFile
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();

				if (line.startsWith("Case") || line.startsWith("case")) {
					if (currentCase != null && !currentContent.isEmpty()) {
						queries.add(currentContent.toString().trim());
					}

					// Start a new case
					currentCase = line;
					currentContent = new StringBuilder(); // Reset the content for the new case
				} else if (!line.isEmpty()) {
					// Append non-empty lines to the current case content
					currentContent.append(line).append(" ");
				}
			}

			// Print the last case's content if any
			if (currentCase != null && !currentContent.isEmpty()) {
				queries.add(currentContent.toString().trim());
			}
			for (String query : queries) {
				SubmitRequest request = new SubmitRequest();
				request.setSql(query);
				request.setQueryAnswer(answer);
				request.setTypeQuestion(typeQuestion);
				request.setPrefixTable(prefixTable);
				request.setTypeDatabaseId(typeDatabaseId);
				requests.add(request);
			}
		}
		return requests;
	}

	public String generateScriptCreateTable(String typeDatabaseId) {
		List<TableCreatedInfProjection> tableInfos = tableCreateRepository.getTableInfo(typeDatabaseId);
		for(TableCreatedInfProjection tableInfo : tableInfos) {
			TableCreated tableCreated = tableCreateRepository.findById(tableInfo.getId()).orElse(null);
			if(tableCreated != null) {
				tableCreated.setQuery(tableInfo.getQueryCreate());
				tableCreateRepository.save(tableCreated);
			}
		}
		return "ok";
	}

	public void handleSubmitResult(CpSubmitResponse submitResponse) {
		if(Objects.isNull(submitResponse)) {
			throw new ValidateException(ErrorCode.ERROR.getDescription());
		}
		log.info("================ receive response from webhook: {} ==================\n\n",submitResponse.toString());
		CpSubmitRequest request = submitResponse.getRequest();
		if(submitResponse.getStatus() == ErrorCode.ERROR.getCode()) {
			log.error("=== Cannot handle sql because have some error occur in submit service ==");
			return;
		}
		switch (submitResponse.getRequest().getTypeSubmit()) {
			case PRACTICE: {
				submitHistoryService.updateSubmitResult(request.getSubmitId(),submitResponse);
				break;
			}
			case CONTEST: {
				submitContestService.updateSubmitContestResult(submitResponse);
			}
		}
		SubmitResponse response = new SubmitResponse();
		response.setStatus(ErrorCode.SUCCESS.getCode());
		response.setStatusSubmit(submitResponse.getStatusSubmit());
		response.setTimeExec(submitResponse.getTimeExec());
		LocalDateTime now = LocalDateTime.now();
		response.setTimeSubmit(now);
		response.setTestPass(submitResponse.getTestPass());
		response.setTotalTest(submitResponse.getTotalTest());
		response.setSubmitId(request.getSubmitId());
		SubmitResultMessage message = SubmitResultMessage.builder()
				.message(response)
				.destination(request.getUserSubId())
				.build();
		submitResultProducer.send(message);
	}

	public EvaluateResponse getEvaluateFromAI(String submitHisId) {
		if(!contextUtil.getUser().getIsPremium()) {
			throw new ValidateException(ErrorCode.UN_PERMITION.getDescription());
		}
		SubmitHistory submitHistory = submitHistoryService.getSubmitHistoryById(submitHisId);
		if(submitHistory.getEvaluate() != null) {
			throw new ValidateException(ErrorCode.SUBMIT_EXIST_EVALUATE.getDescription());
		}
		QuestionDetail questionDetail = questionService.getQuestionDetail(submitHistory.getQuestion().getId(),submitHistory.getDatabase().getId());
		EvaluateRequest request = EvaluateRequest.builder()
				.question(submitHistory.getQuestion().getTitle())
				.sql(submitHistory.getQuerySub())
				.submitStatus(submitHistory.getStatus().name())
				.sqlAnswer(questionDetail.getQueryAnswer())
				.build();
		try {
			ResponseEntity<AIResponse> response = submitServiceClient.evaluateSubmit(request);
			log.info(response.toString());
			if(response.getStatusCode().is2xxSuccessful()) {
				AIResponse evaluateResponse =response.getBody();
				assert evaluateResponse != null;
				return EvaluateResponse.builder()
						.content(evaluateResponse.getChoices().get(0).getMessage().getContent())
						.build();
			} else {
				throw new ValidateException(ErrorCode.ERROR.getDescription());
			}
		} catch (FeignException e) {
			log.error(e.getMessage(),e);
			if(e.status() == 429) {
				throw new ValidateException(ErrorCode.MAX_REQUEST_LIMIT_EXCEEDED.getDescription());
			}
			throw new ValidateException(ErrorCode.ERROR.getDescription());
		} catch (Exception ex) {
			log.error(ex.getMessage(),ex);
			throw new ValidateException(ErrorCode.ERROR.getDescription());
		}

	}
}
