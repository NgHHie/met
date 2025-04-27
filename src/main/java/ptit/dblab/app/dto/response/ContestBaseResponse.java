package ptit.dblab.app.dto.response;

import ptit.dblab.app.enumerate.ContestStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ContestBaseResponse {
    private String id;
    private String contestCode;
    private ContestStatus status;
}
