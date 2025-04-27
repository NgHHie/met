package ptit.dblab.app.mapper;

import ptit.dblab.shared.common.BaseMapper;
import ptit.dblab.app.dto.request.UserContestRequest;
import ptit.dblab.app.dto.response.UserContestResponse;
import ptit.dblab.app.entity.UserContest;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserContestMapper extends BaseMapper<UserContest, UserContestRequest, UserContestResponse> {

}
