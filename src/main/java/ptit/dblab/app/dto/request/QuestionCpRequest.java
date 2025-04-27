package ptit.dblab.app.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class QuestionCpRequest {
    private String questionId;
    private String databaseId;
    private String answer;
    List<TestcaseCpRequest> testcases;
}
