package ptit.dblab.app.service;

import java.util.Arrays;
import java.util.List;

import ptit.dblab.app.feignClient.MysqlClient;
import ptit.dblab.app.feignClient.SqlServerClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ptit.dblab.app.dto.request.SubmitRequest;
import ptit.dblab.app.dto.response.SubmitResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class SqlExecutorService {

	private final MysqlClient mysqlClient;
	private final SqlServerClient sqlServerClient;

	@Value("${party-service-client.mysql.typeDatabase}")
	private String typeDatabaseSql;

	@Value("${party-service-client.sqlserver.typeDatabase}")
	private String typeDatabaseSqlServer;

	//mysql
	public List<SubmitResponse> sqlExecutes(String[] queries, String typeDatabaseId) {
		SubmitRequest request = new SubmitRequest();
		request.setQueries(queries);
		if(typeDatabaseSql.equals(typeDatabaseId)) {
			return mysqlClient.executeQuery(request);
		} else if(typeDatabaseSqlServer.equals(typeDatabaseId)) {
			log.info("******* CALL SQL SERVER execute query {}", Arrays.toString(queries));
			return sqlServerClient.executeQuery(request);
		}
		log.info("Typedatabase not support {}", typeDatabaseId);
		return null;
	}

	public SubmitResponse sqlExecuteSingleSql(String sql,String typeDatabaseId) {
		SubmitRequest request = new SubmitRequest();
		request.setSql(sql);
		if(typeDatabaseSql.equals(typeDatabaseId)) {
			log.info("====== CALL MYSQL execute query: {}",sql);
			return mysqlClient.executeSingleSql(request);
		} else if(typeDatabaseSqlServer.equals(typeDatabaseId)) {
			log.info("******* CALL SQL SERVER execute query {}", sql);
			return sqlServerClient.executeSingleSql(request);
		}
		log.info("Typedatabase not support {}", typeDatabaseId);
		return null;
	}

}
