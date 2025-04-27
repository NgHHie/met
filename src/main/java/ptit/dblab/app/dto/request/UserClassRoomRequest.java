package ptit.dblab.app.dto.request;

import ptit.dblab.shared.common.BaseRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserClassRoomRequest {
    private String id;
    private BaseRequest classRoom;
    private BaseRequest user;
}
