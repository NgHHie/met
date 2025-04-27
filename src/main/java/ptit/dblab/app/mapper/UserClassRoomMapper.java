package ptit.dblab.app.mapper;

import ptit.dblab.shared.common.BaseMapper;
import ptit.dblab.app.dto.request.UserClassRoomRequest;
import ptit.dblab.app.dto.response.UserClassResponse;
import ptit.dblab.app.entity.UserClassRoom;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserClassRoomMapper extends BaseMapper<UserClassRoom, UserClassRoomRequest, UserClassResponse> {
}
