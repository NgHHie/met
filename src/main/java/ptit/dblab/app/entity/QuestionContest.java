package ptit.dblab.app.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import ptit.dblab.shared.common.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class QuestionContest extends BaseEntity{
	private float point;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "question_id",referencedColumnName = "id")
	@JsonBackReference
	private Question question;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contest_id",referencedColumnName = "id")
	@JsonManagedReference
	private Contest contest;
}
