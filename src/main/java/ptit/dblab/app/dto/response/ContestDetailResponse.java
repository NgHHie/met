package ptit.dblab.app.dto.response;

import ptit.dblab.shared.common.BaseResponse;
import ptit.dblab.app.enumerate.ContestStatus;
import ptit.dblab.app.enumerate.ModeContest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ContestDetailResponse extends BaseResponse {
    private String contestCode;
    private String name;

    private LocalDateTime startDatetime;

    private LocalDateTime endDatetime;

    private ModeContest mode;

    private ContestStatus status;

    private Boolean isTracker;

    private List<QuestionContestResponse> questions;
}
