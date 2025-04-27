package ptit.dblab.app.entity;

import ptit.dblab.shared.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class TableCreated extends BaseEntity{
	private String name;
	private String prefix;

	@Column(columnDefinition = "TEXT")
	private String query;

	@Column(name = "is_public", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
	private Boolean isPublic = false;

	private String typeDatabaseId;

	private String displayName;
	
	
	public void setTableNameWithPrefix(String tableName) {
		String temps[] = tableName.split("_",2);
		if(temps.length > 1) {
			this.prefix = temps[0];
			this.name = temps[1];
		}
	}

	public String getTableNameWithPrefix() {
		return this.prefix + "_" + this.name;
	}
}
