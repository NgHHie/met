package ptit.dblab.app.dto.response;

import ptit.dblab.app.enumerate.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
    private String id;
    private String username;
    private String firstName;
    private String lastName;

    private String email;

    private Role role;

    private String userCode;
    private String fullName;
    private LocalDateTime createdAt;
}
