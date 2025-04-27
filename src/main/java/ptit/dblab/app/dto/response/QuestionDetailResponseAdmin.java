package ptit.dblab.app.dto.response;

import java.util.List;

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
public class QuestionDetailResponseAdmin extends BaseResponse{
	private String id;
	private String sqlQuery;
	
	private List<String> tableUses;

	private TypeDatabaseResponse typeDatabase;

	private String queryAnswer;
	
	private List<TestCaseResponse> testcases;

	private String prefixCode;

	private int maxTimeExec;
}
