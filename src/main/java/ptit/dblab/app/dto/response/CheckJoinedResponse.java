package ptit.dblab.app.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.Get;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CheckJoinedResponse {
    private Boolean isJoined;
    private List<String> contests;
}
