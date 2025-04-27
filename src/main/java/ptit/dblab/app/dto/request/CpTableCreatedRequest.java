package ptit.dblab.app.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CpTableCreatedRequest {
    private String name;
    private String prefix;
    private String typeDatabaseId;
    private String query;
}
