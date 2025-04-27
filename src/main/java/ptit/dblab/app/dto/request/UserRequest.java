package ptit.dblab.app.dto.request;

import ptit.dblab.app.enumerate.Role;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UserRequest {
    @NotBlank(message = "Username is mandatory")
    private String username;

    private String password;

    private String repassword;

    private String avatar;

    @NotBlank(message = "firstName is mandatory")
    private String firstName;
    @NotBlank(message = "lastName is mandatory")
    private String lastName;
    @NotBlank(message = "email is mandatory")
    private String email;
    @NotBlank(message = "phone is mandatory")
    private String phone;

    private LocalDate birthDay;

    private String userCode;

    @Nullable
    private Role role;

    @Override
    public String toString() {
        return "UserRequest{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", birthDay=" + birthDay +
                '}';
    }
}
