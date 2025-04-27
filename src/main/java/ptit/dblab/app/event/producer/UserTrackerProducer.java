package ptit.dblab.app.event.producer;

import ptit.dblab.app.dto.request.UserTrackerRequest;
import ptit.dblab.app.event.BaseProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "spring.kafka.enable", havingValue = "true")
public class UserTrackerProducer extends BaseProducerService<UserTrackerRequest> {

    @Value("${spring.kafka.topic-and-group.tracker-log}")
    private String topic;
    public UserTrackerProducer(KafkaTemplate<String, UserTrackerRequest> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    protected String getTopicName() {
        return topic;
    }

    @Override
    protected void handleFailure() {
        log.info("failed to produce user tracker event");
    }
}
