package ptit.dblab.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ptit.dblab.shared.exception.core.ValidateException;
import ptit.dblab.shared.utils.ContextUtil;
import ptit.dblab.app.dto.request.TableCreatedRequest;
import ptit.dblab.app.dto.response.SubmitResponse;
import ptit.dblab.app.dto.response.TableCreatedResponse;
import ptit.dblab.app.enumerate.ErrorCode;
import ptit.dblab.app.enumerate.Role;
import ptit.dblab.app.mapper.TableCreatedMapper;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ptit.dblab.shared.common.BaseService;
import ptit.dblab.app.entity.TableCreated;
import ptit.dblab.app.repository.TableCreateRepository;

import net.sf.jsqlparser.JSQLParserException;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
public class TableCreateServcie extends BaseService<TableCreated, TableCreateRepository>{
	
	private final ContextUtil contextUtil;
	private final TableCreatedMapper tableCreatedMapper;
	private final UserService userService;
	private final SqlExecutorService sqlExecutorService;

	public TableCreateServcie(TableCreateRepository repository, ContextUtil contextUtil, TableCreatedMapper tableCreatedMapper, UserService service, UserService userService, SqlExecutorService sqlExecutorService) {
		super(repository);
		this.contextUtil = contextUtil;
        this.tableCreatedMapper = tableCreatedMapper;
		this.userService = userService;
        this.sqlExecutorService = sqlExecutorService;
    }
	
	public List<TableCreatedResponse> getTableCreatedsByUserId(String typeDatabaseId) {
		return tableCreatedMapper.toResponseList(this.repository.getListTableCreatedByUserId(contextUtil.getUserId(),typeDatabaseId));
	}

	public Page<TableCreatedResponse> getTableCreatedFilter(Pageable pageable, String direction, String keyword,String typeDatabaseId) {
		Sort.Direction sortDirection = Sort.Direction.ASC;

		if (direction != null && direction.equalsIgnoreCase("desc")) {
			sortDirection = Sort.Direction.DESC;
		}

		Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by(sortDirection, "createdAt"));

		Page<TableCreated> tableCreatedPage = this.repository.findAll((root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			String userId = contextUtil.getUser().getRole().equals(Role.ADMIN.name()) ? null : contextUtil.getUser().getId();
			if (keyword != null && !keyword.isEmpty()) {
				Predicate prefixPredicate = criteriaBuilder.like(
						criteriaBuilder.lower(root.get("prefix")), "%" + keyword.trim().toLowerCase() + "%"
				);

				Predicate namePredicate = criteriaBuilder.like(
						criteriaBuilder.lower(root.get("name")), "%" + keyword.trim().toLowerCase() + "%"
				);

				Predicate displayNamePredicate = criteriaBuilder.like(
						criteriaBuilder.lower(root.get("displayName")), "%" + keyword.trim().toLowerCase() + "%"
				);
				Predicate combinedPredicate = criteriaBuilder.or(prefixPredicate, namePredicate, displayNamePredicate);

				predicates.add(combinedPredicate);
			}
			if(typeDatabaseId != null) {
				Predicate typeDatabasePredicate= criteriaBuilder.equal(
						criteriaBuilder.lower(root.get("typeDatabaseId")), typeDatabaseId
				);
				predicates.add(typeDatabasePredicate);
			}
			if (userId != null) {
				Predicate createdByPredicate = criteriaBuilder.equal(
						root.get("createdBy"), userId
				);
				predicates.add(createdByPredicate);
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		}, sortedPageable);

		return tableCreatedPage.map(this::mapToResponse);
	}
	
	public TableCreated create(TableCreated request) {
        this.save(request);
        return request;
	}

	public void update(String id, TableCreatedRequest request) {
		TableCreated tableCreated = findById(id);
		tableCreatedMapper.updateFromRequest(tableCreated,request);
		if(Objects.nonNull(tableCreated.getDisplayName()) && !tableCreated.getDisplayName().toLowerCase().contains(tableCreated.getName().toLowerCase())) {
			throw new ValidateException(ErrorCode.DISPLAYNAME_MUST_CONTAIN_TABLE_NAME.getDescription());
		}
		this.save(tableCreated);
	}

	@Transactional
	public void delete(String id) {
		TableCreated currentTableCreated = this.findById(id);
		String tableName = currentTableCreated.getTableNameWithPrefix();
		String typeDatabaseId = currentTableCreated.getTypeDatabaseId();
		String queryDropTemp = "DROP TABLE IF EXISTS " + tableName;
		this.hardDelete(id);
		SubmitResponse response = sqlExecutorService.sqlExecuteSingleSql(queryDropTemp,typeDatabaseId);
		if(Objects.isNull(response) ||  response.getStatus() != ErrorCode.SUCCESS.getCode()) {
			throw new ValidateException(ErrorCode.ERROR.getDescription());
		}
	}

	public void deleteTableByNameAndPrefix(String tableName,String typeDatabaseId) throws JSQLParserException {
		String[] temps = tableName.split("_");
		try {
			log.info("********* DELETE Table {} ********** ", tableName);
            this.repository.deleteByNameAndPrefix(temps[1], temps[0],typeDatabaseId);
        } catch (Exception e) {
            throw new JSQLParserException("Cannot delete table: " + tableName);
        }
	}

	public List<TableCreated> getListTableCreatedByQuestionDetailId(String questionDetailId) {
		return this.repository.findTableCreatedByQuestionDetailId(questionDetailId);
	}

	public List<TableCreated> getListTableCreatedByIds(List<String> ids) {
		return this.repository.findByIdIn(ids);
	}

	private TableCreatedResponse mapToResponse(TableCreated tableCreated) {
		TableCreatedResponse response = tableCreatedMapper.toResponse(tableCreated);
		response.setUserCreated(userService.toUserBaseResponse(tableCreated.getCreatedBy()));
		response.setIsPublic(tableCreated.getIsPublic());
		return response;
	}
}
