package ptit.dblab.app.service;

import ptit.dblab.shared.utils.ContextUtil;
import ptit.dblab.shared.common.BaseService;
import ptit.dblab.app.dto.response.CpSubmitResponse;
import ptit.dblab.app.dto.response.SubmitContestHistoryResponse;
import ptit.dblab.app.dto.response.SubmitHistoryResponse;
import ptit.dblab.app.entity.*;
import ptit.dblab.app.entity.QuestionContest;
import ptit.dblab.app.entity.SubmitContestExam;
import ptit.dblab.app.entity.User;
import ptit.dblab.app.enumerate.Role;
import ptit.dblab.app.interfaceProjection.QuestionCompleteProjection;
import ptit.dblab.app.mapper.SubmitContestMapper;
import ptit.dblab.app.repository.SubmitContestExamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class SubmitContestService extends BaseService<SubmitContestExam, SubmitContestExamRepository> {

    private static final Logger log = LoggerFactory.getLogger(SubmitContestService.class);
    private final ContextUtil contextUtil;
    private final SubmitContestMapper submitContestMapper;
    private final QuestionContestService questionContestService;

    public SubmitContestService(SubmitContestExamRepository repository, ContextUtil contextUtil, SubmitContestMapper submitContestMapper, QuestionContestService questionContestService) {
        super(repository);
        this.contextUtil = contextUtil;
        this.submitContestMapper = submitContestMapper;
        this.questionContestService = questionContestService;
    }

    public SubmitContestHistoryResponse getSubmitContestDetail(String id) {
        return submitContestMapper.toResponseHistory(findById(id));
    }
    public Page<SubmitHistoryResponse> getSubmitContestByUserAndQuestionCode(String questionContestId, String userId, Pageable pageable) {
        String userIdUse = contextUtil.getUser().getRole().equals(Role.ADMIN.name()) ? userId : contextUtil.getUserId();
        Page<SubmitContestExam> submitContestExamPage = this.repository.findSubmitContestByUserIdAndQuestionContestId(userIdUse,questionContestId,pageable);
        log.info("userID: {} size data: {}",userIdUse,submitContestExamPage.getTotalElements());
        return submitContestExamPage.map(submitContestMapper::toResponse);
    }

    public Page<SubmitHistoryResponse> getSubmitContestByUser(String contestId, String userId,Pageable pageable) {
        Page<SubmitContestExam> submitContestExamPage = this.repository.findSubmitContestByUserId(userId,contestId,pageable);
        return submitContestExamPage.map(submitContestMapper::toResponse);
    }

    public List<QuestionCompleteProjection> getQuestionStatus(String userId, String[] questionIds) {
        return this.repository.findQuestionContestStatusByUser(userId, questionIds);
    }

    public void updateSubmitContestResult(CpSubmitResponse response) {
        SubmitContestExam submitHistory = this.findById(response.getRequest().getSubmitId());
        QuestionContest currentQuestion = questionContestService.findById(response.getRequest().getDevelopPayload());
        int point = Math.round((currentQuestion.getPoint()/response.getTotalTest())* response.getTestPass());
        submitHistory.setStatus(response.getStatusSubmit());
        if(Objects.isNull(submitHistory.getTimeSubmit())) {
            submitHistory.setTimeSubmit(LocalDateTime.now());
        }
        submitHistory.setUser(User.builder()
                .id(response.getRequest().getUserSubId())
                .build());
        submitHistory.setQuerySub(submitHistory.getQuerySub());
        submitHistory.setQuestionContest(currentQuestion);
        submitHistory.setTimeExec((float) response.getTimeExec());
        submitHistory.setTestPass(response.getTestPass());
        submitHistory.setTotalTest(response.getTotalTest());
        submitHistory.setPoint(point);
        submitHistory.setIsRetry(false);
        this.save(submitHistory);
    }

    public Page<SubmitContestHistoryResponse> getSubmitContestHistoryByContestId(String contestId, Pageable pageable) {
        Page<SubmitContestExam> submitContestExamPage = this.repository.getSubmitContestHistoryByContest(pageable,contestId);
        return submitContestExamPage.map(submitContestMapper::toResponseHistory);
    }

    public List<SubmitContestHistoryResponse> getAllSubmitContestHistoryByContestId(String contestId) {
        return submitContestMapper.toResponseHistoryList(this.repository.getAllSubmitContestHistoryByContestId(contestId));
    };
}
