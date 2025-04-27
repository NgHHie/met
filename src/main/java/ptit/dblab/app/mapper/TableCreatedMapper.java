package ptit.dblab.app.mapper;

import ptit.dblab.shared.common.BaseMapper;
import ptit.dblab.app.dto.request.TableCreatedRequest;
import ptit.dblab.app.dto.response.TableCreatedResponse;
import ptit.dblab.app.entity.TableCreated;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TableCreatedMapper extends BaseMapper<TableCreated, TableCreatedRequest,TableCreatedResponse> {

}
