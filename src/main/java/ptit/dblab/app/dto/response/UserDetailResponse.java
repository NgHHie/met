package ptit.dblab.app.dto.response;

import ptit.dblab.shared.common.BaseResponse;
import ptit.dblab.app.enumerate.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class UserDetailResponse extends BaseResponse {
    private String firstName;
    private String lastName;

    private String username;

    private String avatar;
    private String email;
    private String phone;
    private LocalDate birthDay;

    private Role role;

    private String userCode;

    private String userPrefix;

    private String fullName;

    private Boolean isPremium;
}
