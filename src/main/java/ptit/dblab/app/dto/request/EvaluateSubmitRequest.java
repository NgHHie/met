package ptit.dblab.app.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EvaluateSubmitRequest {
    private String submitId;
    private String evaluate;
}
