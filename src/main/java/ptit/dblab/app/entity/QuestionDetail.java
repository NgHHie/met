package ptit.dblab.app.entity;



import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import ptit.dblab.shared.enumerate.TypeQuestion;
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
public class QuestionDetail extends BaseEntity{

	@Column(columnDefinition = "TEXT")
	private String sqlQuery;
	
	@OneToMany(mappedBy = "questionDetail", cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
	@JsonManagedReference
	private List<TableDetail> tableUses;

	@ManyToOne
	@JoinColumn(name = "type_database_id",referencedColumnName = "id")
	private TypeDatabase typeDatabase;

	@Column(columnDefinition = "TEXT")
	private String queryAnswer;
	
	@OneToMany(mappedBy = "questionDetail",cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
	@JsonManagedReference
	private List<Testcase> testcases;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference
	private Question question;

	private int maxTimeExec;

	public List<TableCreated> getListTableUseCreated() {
		List<TableCreated> tableUseDetail = new ArrayList<TableCreated>();
		if (Objects.isNull(tableUses)) {
			return tableUseDetail;
		}
		tableUses.sort(Comparator.comparingInt(TableDetail::getSequenceNumber));
		for(TableDetail tableDetail : tableUses) {
			tableUseDetail.add(tableDetail.getTableCreated());
		}
		return tableUseDetail;
	}

	public TypeQuestion getTypeQuestion() {
		if(Objects.isNull(question)) {
			return null;
		}
		return this.question.getType();
	}
}
