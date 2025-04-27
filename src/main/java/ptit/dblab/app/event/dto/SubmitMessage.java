package ptit.dblab.app.event.dto;

import lombok.*;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldNameConstants
@ToString
public class SubmitMessage {
    private String sql;
    private String sessionPrefix;
    private String questionId;
    private String questionContestId;
    private String userId;
    private String submitId;
    private String typeDatabaseId;
    private boolean isSubmitContest = false;
}
