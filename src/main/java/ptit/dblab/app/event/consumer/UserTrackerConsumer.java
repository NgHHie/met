package ptit.dblab.app.event.consumer;

import ptit.dblab.app.dto.request.UserTrackerRequest;
import ptit.dblab.app.event.BaseConsumerService;
import ptit.dblab.app.service.UserTrackerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.sqm.sql.ConversionException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.RetryTopicConstants;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.handler.invocation.MethodArgumentResolutionException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.kafka.enable", havingValue = "true")
public class UserTrackerConsumer extends BaseConsumerService<UserTrackerRequest> {
    private final UserTrackerService userTrackerService;

    @RetryableTopic(
            kafkaTemplate = "kafkaTemplate",
            exclude = {
                    DeserializationException.class,
                    MessageConversionException.class,
                    ConversionException.class,
                    MethodArgumentResolutionException.class,
                    NoSuchMethodException.class,
                    ClassCastException.class
            },
            dltTopicSuffix = RetryTopicConstants.DEFAULT_DLT_SUFFIX,
            attempts = "1",
            backoff = @Backoff(delay = 3000, multiplier = 1.5, maxDelay = 15000)
    )
    @KafkaListener(
            topics = "${spring.kafka.topic-and-group.tracker-log}",
            groupId = "${spring.kafka.topic-and-group.tracker-log}",
            concurrency = "10",
            autoStartup = "${spring.kafka.enable:false}"
    )
    @Override
    public void receive(UserTrackerRequest data) {
        userTrackerService.saveLogTracker(data);
    }

    @Override
    public void receive(UserTrackerRequest data, Map<String, Object> headers) {

    }
}
