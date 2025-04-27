package ptit.dblab.app.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserHisResponse {
    private String firstName;
    private String lastName;
    private String userCode;

    private String fullName;
}
