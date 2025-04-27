package ptit.dblab.app.dto.response;

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
public class QuestionResponseDetail extends BaseResponse {
    private String questionCode;
    private String title;
    private String content;
    private String image;
    private float point;
    private String prefixCode;

    private TypeQuestion type;

    private boolean enable;


    private LevelQuestion level;

    private List<QuestionDetailResponse> questionDetails;
}
