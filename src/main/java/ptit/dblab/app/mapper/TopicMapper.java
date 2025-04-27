package ptit.dblab.app.mapper;

import ptit.dblab.shared.common.BaseMapper;
import ptit.dblab.app.dto.request.TopicRequest;
import ptit.dblab.app.dto.response.TopicDetailResponse;
import ptit.dblab.app.dto.response.TopicResponse;
import ptit.dblab.app.entity.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TopicMapper extends BaseMapper<Topic, TopicRequest, TopicResponse> {
    TopicDetailResponse toDetailResponse(Topic topic);
}
