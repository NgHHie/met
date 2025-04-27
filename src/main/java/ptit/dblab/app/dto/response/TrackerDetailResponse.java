package ptit.dblab.app.dto.response;

import ptit.dblab.app.interfaceProjection.UserIpCountProjection;
import ptit.dblab.app.interfaceProjection.UserTrackerProjection;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
public class TrackerDetailResponse {
    private List<UserTrackerProjection> actions;
    private UserIpCountProjection countIp;
}
