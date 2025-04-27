package ptit.dblab.app.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopUserResponse {
    String id;
    String userCode;
    String fullName;
    String avatar;
    int numQuestionDone;
    float totalPoints;
    int rank;
}
