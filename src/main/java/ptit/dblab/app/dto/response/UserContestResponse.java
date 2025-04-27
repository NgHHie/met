package ptit.dblab.app.dto.response;

import ptit.dblab.shared.common.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class UserContestResponse extends BaseResponse {
    private UserResponse user;
    private LocalDateTime timeJoined;
}
