package ptit.dblab.app.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter;

@Configuration
public class KafkaConfiguration {

  @Bean("kafkaObjectMapper")
  public ObjectMapper kafkaObjectMapper() {
    val objectMapper = new ObjectMapper()
        /*
         * Deserialization
         * */
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true)
        .configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true)
        .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
        /*
         * Serialization
         * */
        .configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        /*
         * Modules
         * */
        .registerModule(new JavaTimeModule())
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

    val config = objectMapper.getDeserializationConfig();
    objectMapper.setConfig(config.with(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS));

    return objectMapper;
  }

  @Bean
  public ByteArrayJsonMessageConverter byteArrayJsonMessageConverter(
      @Qualifier("kafkaObjectMapper") ObjectMapper kafkaObjectMapper
  ) {
    return new ByteArrayJsonMessageConverter(kafkaObjectMapper);
  }
}
