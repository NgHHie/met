package ptit.dblab.app.dto.request;

import ptit.dblab.shared.common.BaseRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {
    private String parentId;

    private BaseRequest user;

    private String content;
}
