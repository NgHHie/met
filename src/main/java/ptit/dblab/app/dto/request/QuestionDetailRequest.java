package ptit.dblab.app.dto.request;

import ptit.dblab.shared.enumerate.TypeQuestion;
import ptit.dblab.app.dto.response.IdResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class QuestionDetailRequest {
    private String id;
    private String sqlQuery;

    private List<String> tableUses;

    private IdResponse typeDatabase;

    private String queryAnswer;

    private List<TestCaseRequest> testcases;

    private int maxTimeExec;
}
