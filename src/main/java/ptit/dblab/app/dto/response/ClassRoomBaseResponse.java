package ptit.dblab.app.dto.response;

import ptit.dblab.shared.common.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ClassRoomBaseResponse extends BaseResponse {
    private String name;
    private String classCode;
    private String description;
    private UserBaseResponse userCreated;
}
