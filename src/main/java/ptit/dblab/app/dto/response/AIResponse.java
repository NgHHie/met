package ptit.dblab.app.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AIResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<ChoiceResponse> choices;
    private String systemFingerprint;
}
