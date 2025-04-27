package ptit.dblab.app.feignClient;

import ptit.dblab.app.dto.request.SubmitRequest;
import ptit.dblab.app.dto.response.SubmitResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        name = "judge-sqlserver",
//        url = "${party-service-client.sqlserver.url}",
        contextId = "sqlServerClient"
)
public interface SqlServerClient {
    @PostMapping("/api/sql-server/executes")
    List<SubmitResponse> executeQuery(@RequestBody SubmitRequest request);

    @PostMapping("/api/sql-server/execute-sql")
    SubmitResponse executeSingleSql(@RequestBody SubmitRequest request);
}
