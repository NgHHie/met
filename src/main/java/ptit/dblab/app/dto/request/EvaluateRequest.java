package ptit.dblab.app.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class EvaluateRequest {
    private String question;
    private String sql;
    private String submitStatus;
    private String sqlAnswer;
}
