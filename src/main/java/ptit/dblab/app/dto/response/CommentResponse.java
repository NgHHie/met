package ptit.dblab.app.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import ptit.dblab.shared.common.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class CommentResponse extends BaseResponse {
    private String parentId;

    private UserBaseResponse user;

    private String content;

    private long countLike;

    @JsonProperty(namespace = "isUserLike")
    private boolean isUserLike;
}
