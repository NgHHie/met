package ptit.dblab.app.dto.response;

import ptit.dblab.shared.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
public class ClassRoomDetailResponse extends BaseResponse {
    private String name;
    private String classCode;
    private String description;
    private List<UserClassResponse> users;
}
