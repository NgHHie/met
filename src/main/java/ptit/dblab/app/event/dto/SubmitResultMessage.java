package ptit.dblab.app.event.dto;

import ptit.dblab.app.dto.response.SubmitResponse;
import lombok.*;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldNameConstants
@ToString
public class SubmitResultMessage {
    private SubmitResponse message;
    private String destination;
}
