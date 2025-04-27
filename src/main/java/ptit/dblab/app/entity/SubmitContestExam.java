package ptit.dblab.app.entity;

import java.time.LocalDateTime;

import ptit.dblab.shared.common.entity.BaseEntity;
import ptit.dblab.app.enumerate.AnswerStatus;

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
public class SubmitContestExam extends BaseEntity{
	@Enumerated(EnumType.STRING)
	private AnswerStatus status;

	@Column(name = "time_submit")
	private LocalDateTime timeSubmit;
	
	private float timeExec;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id",referencedColumnName = "id")
	private User user;

	@Column(columnDefinition = "TEXT")
	private String querySub;

	private int testPass;

	private int totalTest;

	@Column(columnDefinition = "float default 0")
	private float point;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "question_contest_id",referencedColumnName = "id")
	private QuestionContest questionContest;

	@ManyToOne(fetch = FetchType.LAZY)
	private TypeDatabase database;

	private String ip;

	@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
	private Boolean isRetry;
}
