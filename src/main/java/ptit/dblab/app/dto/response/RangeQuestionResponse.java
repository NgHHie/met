package ptit.dblab.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RangeQuestionResponse {
    private String range;
    private int numUser;
}
