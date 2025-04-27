package ptit.dblab.app.dto.response;

import ptit.dblab.app.entity.Question;
import ptit.dblab.app.enumerate.QuestionStatus;
import ptit.dblab.shared.enumerate.TypeQuestion;
import org.springframework.stereotype.Service;

import ptit.dblab.shared.common.BaseResponse;
import ptit.dblab.app.enumerate.LevelQuestion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@Service
@SuperBuilder
@NoArgsConstructor
public class QuestionBasicResponse extends BaseResponse {
	private String questionCode;
	private String title;
	private float point;
	
	private TypeQuestion type;
	
	private boolean enable;

	private Boolean isSynchorus;

	private int totalSub;

	private float acceptance;
	
	private LevelQuestion level;
	private String prefixCode;

	private Boolean isShare;

	private UserBaseResponse userCreated;

	private QuestionStatus status;

	private List<QuestionDetailResponse> questionDetails;
}
