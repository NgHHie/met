package ptit.dblab.app.service;

import ptit.dblab.shared.exception.core.ValidateException;
import ptit.dblab.shared.utils.TableDependencyResolver;
import ptit.dblab.app.dto.response.SubmitResponse;
import ptit.dblab.app.entity.TableCreated;
import ptit.dblab.app.entity.TypeDatabase;
import ptit.dblab.app.repository.TableDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessService {
    private final SqlExecutorService sqlExecutorService;
    private final TypeDatabaseService typeDatabaseService;
    private final TableDetailRepository tableDetailRepository;

    public void sortTables(List<TableCreated> tableCreateds,String questionDetailId) {
        if(tableCreateds == null || tableCreateds.isEmpty()) {
            throw new ValidateException("List of tables is null or empty");
        }
        String typeDatabaseId = tableCreateds.get(0).getTypeDatabaseId();
        List<String> tableNames = tableCreateds.stream()
                .map(TableCreated::getTableNameWithPrefix)
                .toList();
        TypeDatabase database = typeDatabaseService.getTypeDatabase(typeDatabaseId);
        String tableListName = tableNames.stream()
                .map(name -> "'" + name + "'")
                .collect(Collectors.joining(","));
        String queryGetDependency = database.getQueryGetDependency().replace("#tableNames",tableListName);
        log.info("Query getDependency: {}", queryGetDependency);
        SubmitResponse response = sqlExecutorService.sqlExecuteSingleSql(queryGetDependency,typeDatabaseId);
        log.info("====== response: {}",response);
        TableDependencyResolver tableDependencyResolver = new TableDependencyResolver();
        List<Map<String, Object>> data = (List<Map<String, Object>>) response.getResult();
        for (Map<String, Object> row : data) {
            tableDependencyResolver.addDependency(row.get("table_name").toString(),row.get("referenced_table_name").toString());
        }
        List<String> tableResolves = tableDependencyResolver.getTableCreationOrder();
        log.info("==== tableResolves: {}",tableResolves);
        tableCreateds.sort(Comparator.comparingInt(tc -> tableResolves.indexOf(tc.getTableNameWithPrefix())));
        int index = 0;
        for(TableCreated tableCreated : tableCreateds) {
            tableDetailRepository.updateSequenceNumber(questionDetailId,tableCreated.getId(),index);
            index++;
        }
        log.info("update sequence number done!");
    }

    public void sortTables(List<TableCreated> tableCreateds) {
        if(tableCreateds == null || tableCreateds.isEmpty()) {
            throw new ValidateException("List of tables is null or empty");
        }
        String typeDatabaseId = tableCreateds.get(0).getTypeDatabaseId();
        List<String> tableNames = tableCreateds.stream()
                .map(TableCreated::getTableNameWithPrefix)
                .toList();
        TypeDatabase database = typeDatabaseService.getTypeDatabase(typeDatabaseId);
        String tableListName = tableNames.stream()
                .map(name -> "'" + name + "'")
                .collect(Collectors.joining(","));
        String queryGetDependency = database.getQueryGetDependency().replace("#tableNames",tableListName);
        log.info("Query getDependency: {}", queryGetDependency);
        SubmitResponse response = sqlExecutorService.sqlExecuteSingleSql(queryGetDependency,typeDatabaseId);
        log.info("response: {}",response.getResult().toString());
        TableDependencyResolver tableDependencyResolver = new TableDependencyResolver();
        List<Map<String, Object>> data = (List<Map<String, Object>>) response.getResult();
        for (Map<String, Object> row : data) {
            tableDependencyResolver.addDependency(row.get("table_name").toString(),row.get("referenced_table_name").toString());
        }
        List<String> tableResolves = tableDependencyResolver.getTableCreationOrder();
        log.info("==== tableResolves: {}",tableResolves);
        tableCreateds.sort(Comparator.comparingInt(tc -> tableResolves.indexOf(tc.getTableNameWithPrefix())));
        for (TableCreated tableCreated : tableCreateds) {
            log.info("tableName sort: {}", tableCreated.getName());
        }
    }
}
