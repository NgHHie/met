package ptit.dblab.app.mapper;


import ptit.dblab.shared.common.BaseMapper;
import ptit.dblab.app.dto.response.SubmitContestHistoryResponse;
import ptit.dblab.app.entity.SubmitContestExam;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SubmitContestHistoryMapper extends BaseMapper<SubmitContestExam,SubmitContestExam, SubmitContestHistoryResponse> {

}
