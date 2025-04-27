package ptit.dblab.app.event;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseProducerService<T> {

  protected final KafkaTemplate<String, T> kafkaTemplate;
  public static final String SENT_LOG_TEMPLATE = "Sent message: {} with offset: {}";

  @SuppressWarnings({"DuplicatedCode"})
  public void send(T data) {
    Message<T> message = MessageBuilder
        .withPayload(data)
        .setHeader(KafkaHeaders.TOPIC, this.getTopicName())
        .build();
    CompletableFuture<SendResult<String, T>> future = kafkaTemplate.send(message);
    future.whenComplete((sr, ex) -> {
      if (ex != null) {
        log.error("Unable to send message : {}", message, ex);
        handleFailure();
        future.completeExceptionally(ex);
      } else {
        log.info(SENT_LOG_TEMPLATE, message, sr.getRecordMetadata().offset());
        future.complete(sr);
      }
    });
  }

  @SuppressWarnings({"DuplicatedCode"})
  public void send(T data, Map<String, Object> header) {
    MessageBuilder<T> messageBuilder = MessageBuilder
        .withPayload(data)
        .setHeader(KafkaHeaders.TOPIC, this.getTopicName());
    header.forEach(messageBuilder::setHeader);
    CompletableFuture<SendResult<String, T>> future = kafkaTemplate.send(messageBuilder.build());
    future.whenComplete((sr, ex) -> {
      if (ex != null) {
        log.error("Unable to send message : {}", messageBuilder, ex);
        handleFailure();
        future.completeExceptionally(ex);
      } else {
        log.info(SENT_LOG_TEMPLATE, messageBuilder, sr.getRecordMetadata().offset());
        future.complete(sr);
      }
    });
  }

  protected abstract String getTopicName();

  protected abstract void handleFailure();
}
