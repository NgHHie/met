package ptit.dblab.app.mapper;

import ptit.dblab.app.dto.response.QuestionHisResponse;
import ptit.dblab.app.dto.response.SubmitContestHistoryResponse;
import ptit.dblab.app.dto.response.SubmitHistoryResponse;
import ptit.dblab.app.dto.response.UserSubmitContestStatResponse;
import ptit.dblab.app.entity.SubmitContestExam;
import ptit.dblab.app.interfaceProjection.UserQuestionSubmitDetail;
import ptit.dblab.app.interfaceProjection.UserSubmitStat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubmitContestMapper {
    private final SubmitHistoryMapper submitHistoryMapper;
    private final SubmitContestHistoryMapper submitContestHistoryMapper;

    public SubmitHistoryResponse toResponse(SubmitContestExam entity) {
        SubmitHistoryResponse resp = submitHistoryMapper.fromSubmitContestToSubmitHisResponse(entity);
        QuestionHisResponse questionHisResponse = new QuestionHisResponse();
        questionHisResponse.setQuestionCode(entity.getQuestionContest().getQuestion().getQuestionCode());
        questionHisResponse.setTitle(entity.getQuestionContest().getQuestion().getTitle());
        resp.setQuestion(questionHisResponse);
        return resp;
    }

    public UserSubmitContestStatResponse toUserSubmitContestStatResponse(UserSubmitStat entity, List<UserQuestionSubmitDetail> details) {
        UserSubmitContestStatResponse response = new UserSubmitContestStatResponse();
        response.setId(entity.getId());
        response.setNumQuestionDone(entity.getNumQuestionDone());
        response.setTotalSubmit(entity.getTotalSubmit());
        response.setTotalSubmitAc(entity.getTotalSubmitAc());
        response.setUserCode(entity.getUserCode());
        response.setFullName(entity.getFullName());
        response.setTotalPoints(entity.getTotalPoints());
        response.setDetails(details);
        return response;
    }

    public SubmitContestHistoryResponse toResponseHistory(SubmitContestExam exam) {
        return submitContestHistoryMapper.toResponse(exam);
    }

    public List<SubmitContestHistoryResponse> toResponseHistoryList(List<SubmitContestExam> exams) {
        return submitContestHistoryMapper.toResponseList(exams);
    }
}
