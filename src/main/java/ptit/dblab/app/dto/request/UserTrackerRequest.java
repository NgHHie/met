package ptit.dblab.app.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserTrackerRequest {
    private String actionType;
    private String detail;
    private String contestId;

    private String userId;
}
