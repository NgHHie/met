package ptit.dblab.app.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChoiceResponse {
    private int index;
    private MessageResponse message;
    private Object logprobs;
    private String finishReason;
}
