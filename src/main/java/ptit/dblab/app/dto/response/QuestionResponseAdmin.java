package ptit.dblab.app.dto.response;

import ptit.dblab.app.enumerate.QuestionStatus;
import ptit.dblab.shared.enumerate.TypeQuestion;
import ptit.dblab.shared.common.BaseResponse;
import ptit.dblab.app.enumerate.LevelQuestion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class QuestionResponseAdmin extends BaseResponse {
    private String questionCode;
    private String title;
    private String content;
    private String image;
    private float point;
    private String prefixCode;

    private TypeQuestion type;

    private boolean enable;

    private Boolean isSynchorus;

    private Boolean isShare;

    private LevelQuestion level;

    private QuestionStatus status;

    private List<ConstraintResponse> constraints;

    private List<QuestionDetailResponseAdmin> questionDetails;
}
