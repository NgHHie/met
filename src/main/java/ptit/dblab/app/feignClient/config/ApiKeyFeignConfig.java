package ptit.dblab.app.feignClient.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;

@Slf4j
public class ApiKeyFeignConfig implements RequestInterceptor {

  @Value("${party-service-client.submit-svc.apiKey}")
  private String apiKey;
  @Bean
  public Logger.Level feignLoggerLevel() {
    return Logger.Level.FULL;
  }

  @Override
  public void apply(RequestTemplate template) {
    if (!apiKey.isEmpty()) {
      template.header("apiKey", apiKey);
    }
  }
}
