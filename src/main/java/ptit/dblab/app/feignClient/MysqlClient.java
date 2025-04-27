package ptit.dblab.app.feignClient;

import ptit.dblab.app.dto.request.SubmitRequest;
import ptit.dblab.app.dto.response.SubmitResponse;
import ptit.dblab.app.feignClient.config.FeignDebugConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        name = "judge-mysql",
//        url = "${party-service-client.mysql.url}",
        contextId = "mysqlClient",
        configuration = {
                FeignDebugConfig.class
        }

)
public interface MysqlClient {
    @PostMapping("/api/mysql/executes")
    List<SubmitResponse> executeQuery(@RequestBody SubmitRequest request);

    @PostMapping("/api/mysql/execute-sql")
    SubmitResponse executeSingleSql(@RequestBody SubmitRequest request);
}