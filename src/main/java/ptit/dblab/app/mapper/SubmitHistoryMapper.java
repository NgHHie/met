package ptit.dblab.app.mapper;

import ptit.dblab.shared.common.BaseMapper;
import ptit.dblab.app.dto.response.SubmitHistoryDetailResponse;
import ptit.dblab.app.dto.response.SubmitHistoryResponse;
import ptit.dblab.app.entity.SubmitContestExam;
import ptit.dblab.app.entity.SubmitHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Objects;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SubmitHistoryMapper extends BaseMapper<SubmitHistory,SubmitHistory, SubmitHistoryResponse> {
    @Override
    @Mapping(target = "user.fullName",  expression = "java(concatFullName(user.getLastName(),user.getFirstName()))")
    SubmitHistoryResponse toResponse(SubmitHistory entity);

    default String concatFullName(String lastName, String firstName) {
        return (Objects.requireNonNullElse(lastName,"") + " " + Objects.requireNonNullElse(firstName, "")).trim();
    }

    @Mapping(target = "user.fullName",  expression = "java(concatFullName(user.getFirstName(), user.getLastName()))")
    SubmitHistoryDetailResponse toDetaiResponse(SubmitHistory entity);

    @Mapping(target = "question",ignore = true)
    SubmitHistoryResponse fromSubmitContestToSubmitHisResponse(SubmitContestExam entity);
}
