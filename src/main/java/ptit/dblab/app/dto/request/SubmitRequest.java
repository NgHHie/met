package ptit.dblab.app.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import ptit.dblab.shared.enumerate.TypeQuestion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SubmitRequest {
	private String sql="";
	private String[] queries;
	private String prefixTable;
	private String questionId;
	private String questionContestId;
	private TypeQuestion typeQuestion;
	private String queryAnswer;
	private String typeDatabaseId;
	private int timeOutExpect;
	private String[] tableUses;
	@JsonProperty("isSubmitContest")
	private boolean isSubmitContest;
	private String contestId;
}
