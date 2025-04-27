package ptit.dblab.app.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CommonInfoStatResponse {
    private int numberUser;
    private int numberQuestion;
    private int numberContest;
    private int numberClassRoom;
}
