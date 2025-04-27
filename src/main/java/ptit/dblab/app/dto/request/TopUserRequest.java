package ptit.dblab.app.dto.request;

import ptit.dblab.app.enumerate.ContestType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopUserRequest {
    private ContestType contestType;
}
