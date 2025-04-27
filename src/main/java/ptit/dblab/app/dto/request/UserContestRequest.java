package ptit.dblab.app.dto.request;

import ptit.dblab.shared.common.BaseRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserContestRequest {
    private String id;
    private BaseRequest contest;
    private BaseRequest user;
}
