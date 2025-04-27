package ptit.dblab.app.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "check-cheat")
@Getter
@Setter
@ToString
public class CheatConfig {
    private int maxTimeCopy;
    private int maxTimePaste;
    private int maxTimeSwitchTab;
    private int maxTimeReturnTab;
}
