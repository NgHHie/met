package ptit.dblab.app.event.producer;

import ptit.dblab.app.event.BaseProducerService;
import ptit.dblab.app.event.dto.SubmitResultMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "spring.kafka.enable", havingValue = "true")
public class SubmitResultProducer extends BaseProducerService<SubmitResultMessage> {

    @Value("${spring.kafka.topic-and-group.notification}")
    private String topic;

    public SubmitResultProducer(KafkaTemplate<String, SubmitResultMessage> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    protected String getTopicName() {
        return topic;
    }

    @Override
    protected void handleFailure() {

    }
}
