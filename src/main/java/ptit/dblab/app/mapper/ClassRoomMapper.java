package ptit.dblab.app.mapper;

import ptit.dblab.shared.common.BaseMapper;
import ptit.dblab.app.dto.request.ClassRoomRequest;
import ptit.dblab.app.dto.response.ClassRoomBaseResponse;
import ptit.dblab.app.dto.response.ClassRoomDetailResponse;
import ptit.dblab.app.entity.ClassRoom;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClassRoomMapper extends BaseMapper<ClassRoom, ClassRoomRequest, ClassRoomBaseResponse> {

    ClassRoomDetailResponse toResponseDetail(ClassRoom entity);

}
