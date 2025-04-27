package ptit.dblab.app.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TopicCountResponse {
    private long topicCount;
}
