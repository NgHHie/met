package ptit.dblab.app.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TestCaseRequest {
	private String id;
	private String query_input;

	private String expect_result;

	private int maxTimeExec;
}
