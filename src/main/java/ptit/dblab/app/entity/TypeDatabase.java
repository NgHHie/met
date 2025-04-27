package ptit.dblab.app.entity;

import ptit.dblab.shared.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@Entity
@NoArgsConstructor
public class TypeDatabase extends BaseEntity{
	private String name;
	@Column(columnDefinition = "TEXT")
	private String queryGetDependency;
	@Column(columnDefinition = "TEXT")
	private String queryGetConstrant;
	@Column(columnDefinition = "TEXT")
	private String queryGetInfoTable;
}
