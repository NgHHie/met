package ptit.dblab.app.service;

import ptit.dblab.app.dto.request.QuestionContestRequest;
import ptit.dblab.app.entity.Contest;
import ptit.dblab.app.entity.QuestionContest;
import ptit.dblab.app.mapper.QuestionContestMapper;
import ptit.dblab.app.repository.QuestionContestRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class QuestionContestService {
    private final QuestionContestRepository questionContestRepository;
    private final QuestionContestMapper questionContestMapper;

    public void updateQuestionContests(Contest contest,List<QuestionContestRequest> requests) {
        List<QuestionContest> updatedQuestionContests = new ArrayList<>();
        List<QuestionContest> questionContests = contest.getQuestions();
        for (QuestionContestRequest questionContestRequest : requests) {
            if(Objects.isNull(questionContestRequest.getId())) {
                QuestionContest questionContest = questionContestMapper.toEntity(questionContestRequest);
                questionContest.setContest(contest);
                updatedQuestionContests.add(questionContest);
            } else {
                QuestionContest questionContest = getQuestionContest(questionContests,questionContestRequest.getId());
                if(Objects.nonNull(questionContest)) {
                    questionContestMapper.updateFromRequest(questionContest,questionContestRequest);
                    updatedQuestionContests.add(questionContest);
                }
            }
        }
        questionContests.clear();
        questionContests.addAll(updatedQuestionContests);
    }

    private QuestionContest getQuestionContest(List<QuestionContest> questionContests,String id) {
        return questionContests.stream()
                .filter(q -> q.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public int getNumberQuestionOfContest(String contestId) {
        return questionContestRepository.getNumberQuestionOfContest(contestId);
    }

    public QuestionContest findById(String id) {
        return questionContestRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Question contest not found"));
    }
}
