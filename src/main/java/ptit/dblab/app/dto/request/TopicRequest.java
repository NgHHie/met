package ptit.dblab.app.dto.request;

import ptit.dblab.shared.common.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopicRequest {
    @NotNull
    private String title;
    @NotNull
    private String content;
    private BaseRequest user;
}
