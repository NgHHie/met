package ptit.dblab.app.mapper;

import java.util.List;

import ptit.dblab.app.dto.request.QuestionDetailRequest;
import ptit.dblab.app.dto.response.*;
import ptit.dblab.app.entity.Question;
import ptit.dblab.app.entity.QuestionDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import ptit.dblab.shared.common.BaseMapper;
import ptit.dblab.app.dto.request.QuestionRequest;
import ptit.dblab.app.dto.response.QuestionBasicResponse;
import ptit.dblab.app.dto.response.QuestionDetailResponseAdmin;
import ptit.dblab.app.dto.response.QuestionResponseAdmin;
import ptit.dblab.app.dto.response.QuestionResponseDetail;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface QuestionMapper extends BaseMapper<Question, QuestionRequest, QuestionResponseAdmin> {

	QuestionBasicResponse toBasicResponse(Question entity);

	QuestionResponseDetail toDetailResponse(Question entity);

	List<QuestionBasicResponse> toBasicResponseList(List<Question> entityList);

	@Override
	@Mapping(target = "constraints", ignore = true)
	QuestionResponseAdmin toResponse(Question entity);

	@Mapping(target = "tableUses", ignore = true)
    QuestionDetailResponseAdmin toDetailResponse(QuestionDetail source);

	@Mapping(target = "tableUses", ignore = true)
	QuestionDetail toDetailEntity(QuestionDetailRequest source);
}
