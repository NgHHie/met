package ptit.dblab.app.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import ptit.dblab.shared.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class Testcase extends BaseEntity{
	@Column(columnDefinition = "TEXT")
	private String query_input;
	
	@Column(columnDefinition = "TEXT")
	private String expect_result;
	
	private int maxTimeExec;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "question_detail_id",referencedColumnName = "id")
	@JsonBackReference
	private QuestionDetail questionDetail;
}
