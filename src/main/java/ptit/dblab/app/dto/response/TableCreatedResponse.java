package ptit.dblab.app.dto.response;

import ptit.dblab.shared.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class TableCreatedResponse extends BaseResponse {
    private String name;
    private String prefix;

    private String displayName;

    private String query;

    private Boolean isPublic;

    private String typeDatabaseId;

    private UserBaseResponse userCreated;
}
