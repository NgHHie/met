package ptit.dblab.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class AuthRequest {
    @NotBlank(message = "Username is mandatory")
    private String username;
    @NotBlank(message = "Username is mandatory")
    private String password;
}
