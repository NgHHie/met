package ptit.dblab.app.utils;

import ptit.dblab.shared.enumerate.TypeQuestion;
import ptit.dblab.shared.utils.ContextUtil;
import ptit.dblab.shared.utils.SqlUtil;
import ptit.dblab.app.dto.response.SubmitResponse;
import ptit.dblab.app.entity.TableCreated;
import ptit.dblab.app.entity.TypeDatabase;
import ptit.dblab.app.enumerate.ErrorCode;
import ptit.dblab.app.service.SqlExecutorService;
import ptit.dblab.app.service.TypeDatabaseService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TableUtil {
    private static final Logger log = LoggerFactory.getLogger(TableUtil.class);

    private final ContextUtil contextUtil;

    private final SqlExecutorService sqlExecutorService;

    private final TypeDatabaseService typeDatabaseService;

    private final SqlUtil sqlUtil;

    public boolean createTemporaryTables(List<TableCreated> tableUses, String typeDatabaseId) throws Exception {
        for (TableCreated tableUsed : tableUses) {
            String tableNameWithPrefix = contextUtil.getUser().getSessionPrefix() +"_"+tableUsed.getName();
            log.info("=========== creating temporary table: {} ==========",tableNameWithPrefix);
            if(Objects.isNull(tableUsed.getQuery())) return  false;
            String queryCreate = sqlUtil.addPrefixSQL(tableUsed.getQuery(), contextUtil.getUser().getSessionPrefix(),true);
            log.info("====== query create table: {}",queryCreate);
            SubmitResponse responseQuery = sqlExecutorService.sqlExecuteSingleSql(queryCreate,typeDatabaseId);
            if (responseQuery.getStatus() == ErrorCode.ERROR.getCode()) {
                log.info("========== error when create temp table {}",tableNameWithPrefix);
                return false;
            }
            log.info("========== create tempo table temp_" + tableNameWithPrefix + " done!");
        }
        return true;
    }

    public boolean clearData(List<TableCreated> tableUses,String typeDatabaseId) throws Exception {
        for (int i = tableUses.size() -1 ; i >= 0; i--) {
            TableCreated tableUsed = tableUses.get(i);
            String tableNameWithPrefix = contextUtil.getUser().getSessionPrefix() +"_"+tableUsed.getName();
            String queryDropTemp = "DELETE FROM temp_" + tableNameWithPrefix;
            SubmitResponse responseQuery = sqlExecutorService.sqlExecuteSingleSql(queryDropTemp,typeDatabaseId);
            if (responseQuery.getStatus() == ErrorCode.ERROR.getCode()) {
                log.error("clear data failed for table {}",tableNameWithPrefix);
                return false;
            }
        }
        log.info("clear data finished!");
        return true;
    }

    public void dropForeignKeys(List<TableCreated> tableUses,String typeDatabaseId) {
        TypeDatabase typeDatabase = typeDatabaseService.getTypeDatabase(typeDatabaseId);
        String tableListName = tableUses.stream()
                .map(table -> "'" + table.getName() + "'")
                .collect(Collectors.joining(","));
        String queryGetConstant = typeDatabase.getQueryGetConstrant().replace("#tableNames",tableListName);
        SubmitResponse responseQuery = sqlExecutorService.sqlExecuteSingleSql(queryGetConstant,typeDatabaseId);
        List<Map<String, Object>> data = (List<Map<String, Object>>) responseQuery.getResult();
        for (Map<String, Object> row : data) {
            String tableName = (String) row.get("table_name");
            String foreignKey = (String) row.get("constraint_name");
            String dropFkQuery = "ALTER TABLE " + tableName + " DROP FOREIGN KEY " + foreignKey;
            sqlExecutorService.sqlExecuteSingleSql(dropFkQuery,typeDatabaseId);
        }
    }

    public void dropTables(List<TableCreated> tableUses,String typeDatabaseId) throws Exception {
        for (int i = tableUses.size() -1 ; i >= 0; i--) {
            TableCreated tableUsed = tableUses.get(i);
            String tableNameWithPrefix = contextUtil.getUser().getSessionPrefix() +"_"+tableUsed.getName();
            String queryDropTemp = "DROP TABLE IF EXISTS temp_" + tableNameWithPrefix;
            SubmitResponse responseQuery = sqlExecutorService.sqlExecuteSingleSql(queryDropTemp,typeDatabaseId);
            if(Objects.nonNull(responseQuery) && responseQuery.getStatus() == ErrorCode.ERROR.getCode()){
                throw new Exception("error when drop table "+ tableNameWithPrefix);
            }
            log.info("****** drop tempo table temp_" + tableNameWithPrefix + " done!");
        }
    }

    public void dropProcedure(String procedureName,String typeDatabaseId) throws Exception {
        String queryDropProcedure = "DROP PROCEDURE IF EXISTS " + procedureName;
        SubmitResponse responseQuery = sqlExecutorService.sqlExecuteSingleSql(queryDropProcedure,typeDatabaseId);
        if(Objects.nonNull(responseQuery) && responseQuery.getStatus() == ErrorCode.SUCCESS.getCode()){
            log.info("drop procedure {} success!", procedureName);
            return;
        }
        log.info("error when drop procedure {} success!", procedureName);
    }

    public boolean createProcedure(String query,String typeDatabaseId) throws Exception {
        SubmitResponse response = sqlExecutorService.sqlExecuteSingleSql(query,typeDatabaseId);
        if(Objects.nonNull(response) && response.getStatus() == ErrorCode.SUCCESS.getCode()){
            log.info("======= create procedure success!");
            return true;
        }
        return false;
    }

    public void dropTemporaryTables(List<TableCreated> tableUses,String typeDatabaseId) throws Exception {
       try {
           log.info("========= drop tempo tables ===========");
           dropTables(tableUses,typeDatabaseId);
       } catch (Exception e) {
            log.error(e.getMessage());
            log.info("========= drop tempo tables failed, retry drop with drop foreignKey ========");
            dropForeignKeys(tableUses,typeDatabaseId);
            dropTables(tableUses,typeDatabaseId);
       }
    }

    public TableCreated getTable(String tableName,List<TableCreated> tableCreateds) {
        for(TableCreated tableCreated : tableCreateds) {
            if(tableCreated.getName().equalsIgnoreCase(tableName)) {
                return tableCreated;
            }
        }
        return null;
    }

    public String getQueryCheck(String tableName,TypeQuestion typeQuestion,String typeDatabaseId) {
        if(typeQuestion == TypeQuestion.ALTER || typeQuestion == TypeQuestion.CREATE) {
            TypeDatabase typeDatabase = typeDatabaseService.getTypeDatabase(typeDatabaseId);
            return typeDatabase.getQueryGetInfoTable().replace("#tableName", tableName);
        } else {
            return "SELECT * FROM #tableName;".replace("#tableName", tableName);
        }
    }
}
