package ptit.dblab.app.mapper;

import ptit.dblab.shared.common.BaseMapper;
import ptit.dblab.app.dto.response.TypeDatabaseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import ptit.dblab.app.entity.TypeDatabase;


@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TypeDatabaseMapper extends BaseMapper<TypeDatabase, TypeDatabase, TypeDatabaseResponse> {
	
	@Named("mapTypeDatabase")
	default TypeDatabase map(String typeDatabaseId) {
		if (typeDatabaseId == null) {
			return null;
		}
		TypeDatabase typeDatabase = new TypeDatabase();
		typeDatabase.setId(typeDatabaseId);
		return typeDatabase;
	}
}
