package ptit.dblab.app.mapper;

import ptit.dblab.shared.common.BaseMapper;
import ptit.dblab.app.dto.request.CommentRequest;
import ptit.dblab.app.dto.response.CommentResponse;
import ptit.dblab.app.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper extends BaseMapper<Comment, CommentRequest, CommentResponse> {

}
