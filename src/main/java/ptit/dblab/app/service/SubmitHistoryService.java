package ptit.dblab.app.service;

import ptit.dblab.shared.common.BaseService;
import ptit.dblab.app.dto.request.EvaluateSubmitRequest;
import ptit.dblab.app.dto.response.CpSubmitResponse;
import ptit.dblab.app.dto.response.SubmitHistoryDetailResponse;
import ptit.dblab.app.dto.response.SubmitHistoryResponse;
import ptit.dblab.app.entity.Question;
import ptit.dblab.app.entity.SubmitHistory;
import ptit.dblab.app.entity.User;
import ptit.dblab.app.mapper.SubmitHistoryMapper;
import ptit.dblab.app.interfaceProjection.QuestionCompleteProjection;
import ptit.dblab.app.repository.SubmitHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class SubmitHistoryService extends BaseService<SubmitHistory, SubmitHistoryRepository> {

    private final SubmitHistoryMapper submitHistoryMapper;
    private final QuestionService questionService;
    public SubmitHistoryService(SubmitHistoryRepository repository, SubmitHistoryMapper submitHistoryMapper, QuestionService questionService) {
        super(repository);
        this.submitHistoryMapper = submitHistoryMapper;
        this.questionService = questionService;
    }

    public Page<SubmitHistoryResponse> getSubmitHistory(Pageable pageable) {
        Page<SubmitHistory> submitHistoryPage = this.repository.findAllByOrderByCreatedAtDesc(pageable);
        return submitHistoryPage.map(submitHistoryMapper::toResponse);
    }

    public Page<SubmitHistoryResponse> getSubmitHistoryByUserIdAndQuestionId(String userId,String questionId,Pageable pageable) {
        Page<SubmitHistory> submitHistoryPage = this.repository.findByUserIdAndQuestionId(userId,questionId,pageable);
        return submitHistoryPage.map(submitHistoryMapper::toResponse);
    }

    public Page<SubmitHistoryResponse> getSubmitHisByUser(String userId,Pageable pageable) {
        Page<SubmitHistory> submitHistoryPage = this.repository.findByUserId(userId,pageable);
        return submitHistoryPage.map(submitHistoryMapper::toResponse);
    }

    public List<QuestionCompleteProjection> getQuestionStatus(String userId, String[] questionIds) {
        return this.repository.findQuestionStatusByUser(userId, questionIds);
    }

    public SubmitHistoryDetailResponse getSubmitHistoryDetail(String id) {
        return submitHistoryMapper.toDetaiResponse(this.findById(id));
    }

    public SubmitHistory getSubmitHistoryById(String id) {
        return this.findById(id);
    }

    public void updateSubmitResult(String id, CpSubmitResponse response) {
        SubmitHistory submitHistory = this.findById(id);
        Question currentQuestion = questionService.findById(response.getRequest().getQuestionId());
        int point = Math.round((currentQuestion.getPoint()/response.getTotalTest())* response.getTestPass());
        submitHistory.setStatus(response.getStatusSubmit());
        if(Objects.isNull(submitHistory.getTimeSubmit())) {
            submitHistory.setTimeSubmit(LocalDateTime.now());
        }
        submitHistory.setUser(User.builder().id(response.getRequest().getUserSubId()).build());
        submitHistory.setQuerySub(submitHistory.getQuerySub());
        submitHistory.setQuestion(currentQuestion);
        submitHistory.setTimeout(response.getTimeExec());
        submitHistory.setTestPass(response.getTestPass());
        submitHistory.setTotalTest(response.getTotalTest());
        submitHistory.setPoint(point);
        submitHistory.setIsRetry(false);
        this.save(submitHistory);
    }

    public void updateEvaluate(EvaluateSubmitRequest request) {
        SubmitHistory submitHistory = findById(request.getSubmitId());
        submitHistory.setEvaluate(request.getEvaluate());
        this.save(submitHistory);
    }
}
