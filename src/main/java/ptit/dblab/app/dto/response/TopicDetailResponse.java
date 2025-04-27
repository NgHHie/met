package ptit.dblab.app.dto.response;

import ptit.dblab.shared.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class TopicDetailResponse extends BaseResponse {
    private String title;
    private String content;
    private UserBaseResponse user;
    private int views;
}
