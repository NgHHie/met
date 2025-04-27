package ptit.dblab.app.dto.response;


import ptit.dblab.shared.enumerate.TypeQuestion;
import ptit.dblab.shared.common.BaseResponse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class QuestionDetailResponse extends BaseResponse{
	private TypeQuestion type;

	private TypeDatabaseResponse typeDatabase;

}
