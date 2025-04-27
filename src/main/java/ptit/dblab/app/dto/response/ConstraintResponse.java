package ptit.dblab.app.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConstraintResponse {
    private String keyword;
    private int times;
    private String type;
}
