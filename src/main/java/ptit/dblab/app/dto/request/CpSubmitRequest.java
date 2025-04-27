package ptit.dblab.app.dto.request;

import ptit.dblab.app.enumerate.TypeSubmitEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CpSubmitRequest {
    private String sql;
    private String questionId;
    private String developPayload;
    private String userSubId;
    private String databaseId;
    private String submitId;
    private TypeSubmitEnum typeSubmit;
    private String[] tableListGenerate;
    private String constraints;
}
