package ptit.dblab.app.feignClient;

import ptit.dblab.app.dto.request.DataRequest;
import ptit.dblab.app.dto.request.CpSubmitRequest;
import ptit.dblab.app.dto.request.CpTableCreatedRequest;
import ptit.dblab.app.dto.request.EvaluateRequest;
import ptit.dblab.app.dto.response.AIResponse;
import ptit.dblab.app.feignClient.config.ApiKeyFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        name = "JUDGE",
        path = "judge",
//        url = "${party-service-client.submit-svc.url}",
        configuration = {
                ApiKeyFeignConfig.class
        }
)
public interface SubmitServiceClient {

    @PostMapping("/data-center/setup-table")
    ResponseEntity<?> setupTableData(@RequestBody List<CpTableCreatedRequest> cpTableCreatedRequest);

    @PostMapping("/data-center/setup-testcase")
    ResponseEntity<?> setupTestcaseData(@RequestBody DataRequest contestDataRequest);

    @PostMapping("/cp/submit")
    ResponseEntity<?> sendSubmit(@RequestBody CpSubmitRequest submit);

    @PostMapping("/evaluate/submit")
    ResponseEntity<AIResponse> evaluateSubmit(@RequestBody EvaluateRequest request);
}
