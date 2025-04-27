package ptit.dblab.app.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableCreatedRequest {
    private String displayName;

    private String query;

    private Boolean isPublic;
}
