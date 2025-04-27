package ptit.dblab.app.mapper;

import ptit.dblab.shared.common.BaseMapper;
import ptit.dblab.app.dto.request.UserTrackerRequest;
import ptit.dblab.app.dto.response.UserTrackerResponse;
import ptit.dblab.app.entity.UserTracker;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserTrackerMapper extends BaseMapper<UserTracker, UserTrackerRequest, UserTrackerResponse> {

}
