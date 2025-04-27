package ptit.dblab.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserUpdateRequest {
    private String password;

    private String repassword;

    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String avatar;

    private LocalDate birthDay;

    private String userCode;
}
