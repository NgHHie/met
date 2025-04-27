package ptit.dblab.app.dto.response;

import ptit.dblab.shared.common.BaseResponse;
import ptit.dblab.app.enumerate.ContestStatus;
import ptit.dblab.app.enumerate.ModeContest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.*;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ContestResponse extends BaseResponse {
    private String contestCode;
    private String name;

    private String description;

    @Setter(AccessLevel.NONE)
    private ZonedDateTime startDatetime;
    @Setter(AccessLevel.NONE)
    private ZonedDateTime endDatetime;

    private Boolean isPublic = true;

    private ModeContest mode;

    private ContestStatus status;

    private int numberUser;

    private int numberQuestion;

    private long duration;

    private UserBaseResponse userCreated;

    public void setStartDatetime(LocalDateTime startDatetime) {
        this.startDatetime = startDatetime.atOffset(ZoneOffset.UTC).toZonedDateTime();
    }

    public void setEndDatetime(LocalDateTime endDatetime) {
        this.endDatetime = endDatetime.atOffset(ZoneOffset.UTC).toZonedDateTime();
    }
}
