package ptit.dblab.app.dto.response;

import ptit.dblab.shared.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class CheatUserResponse extends BaseResponse {
    private UserBaseResponse user;
    private String contestId;
}
