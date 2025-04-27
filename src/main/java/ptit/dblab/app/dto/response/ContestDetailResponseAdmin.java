package ptit.dblab.app.dto.response;

import ptit.dblab.shared.common.BaseResponse;
import ptit.dblab.app.enumerate.ContestStatus;
import ptit.dblab.app.enumerate.ModeContest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ContestDetailResponseAdmin extends BaseResponse {
    private String contestCode;
    private String name;
    private String description;

    private LocalTime startTime;

    private LocalDate startDay;

    private LocalTime endTime;

    private LocalDate endDay;

    private Boolean isPublic;

    private ModeContest mode;

    private ContestStatus status;

    private Boolean isTracker;

    private List<QuestionContestResponse> questions;
    private List<UserContestResponse> users;
}
