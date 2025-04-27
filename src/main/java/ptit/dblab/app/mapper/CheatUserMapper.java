package ptit.dblab.app.mapper;

import ptit.dblab.shared.common.BaseMapper;
import ptit.dblab.app.dto.response.CheatUserResponse;
import ptit.dblab.app.entity.CheatUser;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CheatUserMapper extends BaseMapper<CheatUser,CheatUser, CheatUserResponse> {
}
