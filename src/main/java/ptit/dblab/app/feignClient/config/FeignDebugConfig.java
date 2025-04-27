package ptit.dblab.app.feignClient.config;


import feign.Logger;
import org.springframework.context.annotation.Bean;

public class FeignDebugConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
