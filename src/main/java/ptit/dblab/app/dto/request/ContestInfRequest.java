package ptit.dblab.app.dto.request;

import ptit.dblab.app.enumerate.ContestType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContestInfRequest {
    private String contestId;
    @NotNull
    @NotBlank
    private ContestType contestType;
}
