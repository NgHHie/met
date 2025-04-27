package ptit.dblab.app.dto.response;

import ptit.dblab.app.interfaceProjection.UserQuestionSubmitDetail;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserSubmitContestStatResponse {
    private String id;
    private String userCode;
    private String fullName;
    private int rank;
    private int numQuestionDone;
    private int totalSubmitAc;
    private float totalPoints;
    private int totalSubmit;
    private List<UserQuestionSubmitDetail> details;
}