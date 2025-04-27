package ptit.dblab.app.dto.response;

import ptit.dblab.shared.common.BaseResponse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class TestCaseResponse extends BaseResponse{
	private String query_input;
	
	private String expect_result;
	
	private int maxTimeExec;
}
