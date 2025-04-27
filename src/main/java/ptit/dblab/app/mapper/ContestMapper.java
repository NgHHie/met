package ptit.dblab.app.mapper;

import ptit.dblab.shared.common.BaseMapper;
import ptit.dblab.app.dto.request.ContestRequest;
import ptit.dblab.app.dto.response.ContestDetailResponse;
import ptit.dblab.app.dto.response.ContestDetailResponseAdmin;
import ptit.dblab.app.dto.response.ContestResponse;
import ptit.dblab.app.entity.Contest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ContestMapper extends BaseMapper<Contest, ContestRequest, ContestResponse> {

    ContestDetailResponseAdmin toDetailResponseAdmin(Contest contest);

    @Override
    @Mapping(target = "startDatetime", expression = "java(entity.getStartDateTimeUtc())")
    @Mapping(target = "endDatetime", expression = "java(entity.getEndDateTimeUtc())")
    @Mapping(target = "duration", expression = "java(entity.getDurationInMinutes())")
    ContestResponse toResponse(Contest entity);

    @Override
    @Mapping(target = "questions",ignore = true)
    @Mapping(target = "users",ignore = true)
    void updateFromRequest(@MappingTarget Contest entity, ContestRequest update);

    @Mapping(target = "startDatetime", expression = "java(contest.getStartDateTime())")
    @Mapping(target = "endDatetime", expression = "java(contest.getEndDateTime())")
    ContestDetailResponse toDetailResponse(Contest contest);
}
