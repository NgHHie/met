package ptit.dblab.app.entity;

import java.time.LocalDateTime;

import ptit.dblab.shared.common.entity.BaseEntity;
import ptit.dblab.app.enumerate.AnswerStatus;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class SubmitHistory extends BaseEntity{
	private LocalDateTime timeSubmit;

	private double timeout;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 50)
	private AnswerStatus status;
	
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
	private TypeDatabase database;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "question_id",referencedColumnName = "id")
	private Question question;

	private String ip;

	@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
	private Boolean isRetry = false;

	@Column(columnDefinition = "TEXT")
	private String evaluate;
}
