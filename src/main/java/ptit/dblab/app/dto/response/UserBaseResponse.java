package ptit.dblab.app.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBaseResponse {
    private String id;
    private String avatar;
    private String userCode;
    private String firstName;
    private String lastName;
}
