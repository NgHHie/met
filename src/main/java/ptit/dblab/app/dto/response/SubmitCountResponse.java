package ptit.dblab.app.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class SubmitCountResponse {
    private LocalDate submitDate;
    private int totalSub;
}
