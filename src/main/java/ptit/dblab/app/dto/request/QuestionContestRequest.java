package ptit.dblab.app.dto.request;

import ptit.dblab.shared.common.BaseRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class QuestionContestRequest {
    private String id;
    private float point;
    private BaseRequest question;
    private BaseRequest contest;
}
