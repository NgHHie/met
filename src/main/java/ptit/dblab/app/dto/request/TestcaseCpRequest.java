package ptit.dblab.app.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TestcaseCpRequest {
    private String testcaseDevId;

    private String inputData;

    private String expectOutput;

    private int maxTimeExec;

    @JsonIgnore
    private String databaseId;
}
