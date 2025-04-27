package ptit.dblab.app.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import ptit.dblab.shared.common.entity.BaseEntity;

import jakarta.persistence.Entity;
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
public class TableDetail extends BaseEntity{

	@ManyToOne
	@JoinColumn(name = "question_detail_id",referencedColumnName = "id")
	@JsonBackReference
	private QuestionDetail questionDetail;
	
	@ManyToOne
	@JoinColumn(name = "table_created_id",referencedColumnName = "id")
	private TableCreated tableCreated;

	private int sequenceNumber = 0;
}
