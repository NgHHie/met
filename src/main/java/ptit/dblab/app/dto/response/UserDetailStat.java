package ptit.dblab.app.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDetailStat {
    private String userCode;

    private String fullName;

    private int numQuestionDone;

    private int totalSubmit;

    private float totalPoints;

    private int totalCorrectQuestions;

    private int totalSubmitAc;

    private int totalSubmitWa;

    private int totalSubmitTle;

    private int totalSubmitCe;
}
