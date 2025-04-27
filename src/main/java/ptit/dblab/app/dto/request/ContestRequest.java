package ptit.dblab.app.dto.request;

import ptit.dblab.app.enumerate.ContestStatus;
import ptit.dblab.app.enumerate.ModeContest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ContestRequest {
    private String contestCode;
    private String name;

    private String description;

    private LocalTime startTime;

    private LocalDate startDay;

    private LocalTime endTime;

    private LocalDate endDay;

    private Boolean isPublic = false;

    private ContestStatus status;

    private ModeContest mode;

    private Boolean isTracker;

    private List<QuestionContestRequest> questions;
    private List<UserContestRequest> users;
}
