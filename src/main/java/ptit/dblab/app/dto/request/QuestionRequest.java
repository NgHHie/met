package ptit.dblab.app.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ptit.dblab.app.enumerate.QuestionStatus;
import ptit.dblab.shared.enumerate.TypeQuestion;
import ptit.dblab.app.enumerate.LevelQuestion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionRequest {
	private String questionCode;
	private String title;
	private String content;
	private String image;
	private float point;
	private LevelQuestion level;
	
	private TypeQuestion type;

	private Boolean isShare = false;

	private boolean enable = false;

	private String constraints;

	private List<QuestionDetailRequest> questionDetails;

	private QuestionStatus status;

}
