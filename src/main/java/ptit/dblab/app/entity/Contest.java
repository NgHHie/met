package ptit.dblab.app.entity;

import java.time.*;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import ptit.dblab.shared.common.entity.BaseEntity;

import ptit.dblab.app.enumerate.ContestStatus;
import ptit.dblab.app.enumerate.ModeContest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
public class Contest extends BaseEntity{
	private String contestCode;
	private String name;

	@Column(name = "start_time", nullable = true)
	private LocalTime startTime;

	@Column(name = "start_day", nullable = true)
	private LocalDate startDay;

	@Column(name = "end_time", nullable = true)
	private LocalTime endTime;

	@Column(name = "end_day", nullable = true)
	private LocalDate endDay;

	@Column(name = "is_public", nullable = false)
	private Boolean isPublic = false;

	@Enumerated(EnumType.STRING)
	private ModeContest mode;

	@Enumerated(EnumType.STRING)
	private ContestStatus status = ContestStatus.SCHEDULED;

	@Column(columnDefinition = "TEXT")
	private String description;

	@OneToMany(mappedBy = "contest", cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
	@JsonManagedReference
	private List<QuestionContest> questions;

	@OneToMany(mappedBy = "contest",cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
	@JsonManagedReference
	private List<UserContest> users;

	@Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
	private Boolean isTracker = false;

	public LocalDateTime getStartDateTime() {
		if(Objects.isNull(startTime) || Objects.isNull(startDay)) return null;
		return startDay.atTime(startTime);
	}

	public LocalDateTime getEndDateTime() {
		if(Objects.isNull(endTime) || Objects.isNull(endDay)) return null;
		return endDay.atTime(endTime);
	}

	public ZonedDateTime getStartDateTimeUtc() {
		LocalDateTime startDateTime = getStartDateTime();
		if (startDateTime == null) return null;
		ZonedDateTime localZdt = startDateTime.atZone(ZoneId.systemDefault());
        return localZdt.withZoneSameInstant(ZoneOffset.UTC);
	}

	public ZonedDateTime getEndDateTimeUtc() {
		LocalDateTime endDateTime = getEndDateTime();
		if (endDateTime == null) return null;
		ZonedDateTime localZdt = endDateTime.atZone(ZoneId.systemDefault());
		return localZdt.withZoneSameInstant(ZoneOffset.UTC);
	}
	public long getDurationInMinutes() {
		LocalDateTime startDateTime = getStartDateTime();
		LocalDateTime endDateTime = getEndDateTime();

		if (Objects.isNull(startDateTime) || Objects.isNull(endDateTime)) {
			return 0;
		}

		Duration duration = Duration.between(startDateTime, endDateTime);

		return duration.toMinutes();
	}
}
