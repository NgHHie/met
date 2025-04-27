package ptit.dblab.app.service;

import ptit.dblab.shared.exception.core.ValidateException;
import ptit.dblab.shared.utils.ContextUtil;
import ptit.dblab.shared.common.BaseService;
import ptit.dblab.app.dto.request.ContestRequest;
import ptit.dblab.app.dto.response.*;
import ptit.dblab.app.dto.response.*;
import ptit.dblab.app.entity.Contest;
import ptit.dblab.app.enumerate.ContestStatus;
import ptit.dblab.app.enumerate.ErrorCode;
import ptit.dblab.app.enumerate.ModeContest;
import ptit.dblab.app.enumerate.Role;
import ptit.dblab.app.interfaceProjection.UserDoneQuestionProjection;
import ptit.dblab.app.mapper.ContestMapper;
import ptit.dblab.app.mapper.QuestionContestMapper;
import ptit.dblab.app.mapper.UserContestMapper;
import ptit.dblab.app.repository.ContestIdProjection;
import ptit.dblab.app.repository.ContestRepository;
import ptit.dblab.app.repository.QuestionContestRepository;
import ptit.dblab.app.repository.UserContestRepository;
import ptit.dblab.app.schedule.ContestSchedule;
import ptit.dblab.app.utils.AppUtils;
import ptit.dblab.app.utils.SequenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class ContestService extends BaseService<Contest, ContestRepository> {

    private static final Logger log = LoggerFactory.getLogger(ContestService.class);
    private final SequenceUtil sequenceUtil;
    private final ContestMapper contestMapper;
    private final UserContestService userContestService;
    private final QuestionContestService questionContestService;
    private final ContextUtil contextUtil;
    private final ContestSchedule contestSchedule;
    private final UserService userService;

    public ContestService(ContestRepository repository, SequenceUtil sequenceUtil, QuestionContestRepository questionContestRepository, QuestionContestService questionContestService, UserContestRepository userContestRepository, UserContestService userContestService, ContestMapper contestMapper, QuestionContestMapper questionContestMapper, UserContestMapper userContestMapper, UserContestService userContestService1, QuestionContestService questionContestService1, ContextUtil contextUtil, ContestSchedule contestSchedule, UserService userService) {
        super(repository);
        this.sequenceUtil = sequenceUtil;
        this.contestMapper = contestMapper;
        this.userContestService = userContestService1;
        this.questionContestService = questionContestService1;
        this.contextUtil = contextUtil;
        this.contestSchedule = contestSchedule;
        this.userService = userService;
    }

    public ContestBaseResponse create() {
        Contest contest = new Contest();
        contest.setContestCode(sequenceUtil.generateContestCode());
        this.save(contest);
        return ContestBaseResponse.builder()
                .id(contest.getId())
                .contestCode(contest.getContestCode())
                .status(contest.getStatus())
                .build();
    }

    @Transactional
    public void update(String contestId, ContestRequest request) {
        Contest contest = this.findById(contestId);
        //mapping
        contestMapper.updateFromRequest(contest,request);
        if(Objects.isNull(request.getQuestions())) {
            throw new ValidateException(ErrorCode.QUESTIONS_MUST_BE_REQUIRED.getDescription());
        }
        LocalDateTime startDateTime = AppUtils.combineDateAndTime(request.getStartDay(), request.getStartTime());
        LocalDateTime endDateTime = AppUtils.combineDateAndTime(request.getEndDay(), request.getEndTime());

        if (endDateTime.isBefore(startDateTime)) {
            throw new ValidateException(ErrorCode.TIME_SET_UP_NOT_VALID.getDescription());
        }

        questionContestService.updateQuestionContests(contest,request.getQuestions());
        if(Objects.nonNull(request.getUsers())) {
            userContestService.updateUserContests(contest,request.getUsers());
        }
        if(contest.getMode() == ModeContest.EXAM) {
            contest.setIsTracker(true);
        }
        this.save(contest);
        //refresh list schedule
        contestSchedule.refreshContest();
    }

    public Page<ContestResponse> getAllContest(Pageable pageable, ContestStatus status, String keyword) {
        String userId = contextUtil.getUser().getRole().equals(Role.ADMIN.name()) ? null : contextUtil.getUser().getId();
        Page<Contest> contests = repository.findAllContestByUserId(pageable,userId,Objects.nonNull(status) ? status.name() : null,keyword);
        return contests.map(this::mapToContestResponse);
    }

    public List<ContestResponse> getContestJoinByUserId() {
        return mapToListResponse(userContestService.getListContestByUser(contextUtil.getUserId()));
    }

    public Page<ContestResponse> getAllContestPublic(Pageable pageable) {
        Page<Contest> contests = repository.findAllContestPublished(pageable);
        return contests.map(this::mapToContestResponse);
    }

    public ContestDetailResponseAdmin getContestDetailAdmin(String contestId) {
        return contestMapper.toDetailResponseAdmin(findById(contestId));
    }

    public ContestResponse getContestWaiting(String contestId) {
        return mapToContestResponse(this.findById(contestId));
    }

    public ContestResponse getCurrentContestExamRunning() {
        ContestResponse contestResponse = contestMapper.toResponse(userContestService.getContestExamRunning());
        if(Objects.isNull(contestResponse)) {
            throw new ValidateException(ErrorCode.RESOURCE_NOT_FOUND.getDescription());
        }
        return contestResponse;
    }

    public void deleteContest(String contestId) {
        this.hardDelete(contestId);
        contestSchedule.refreshContest();
    }

    public ContestIdProjection getContestIdByQuestionContestId(String questionContestId) {
        return this.repository.getContestIdByQuestionContest(questionContestId);
    }

    private ContestResponse mapToContestResponse(Contest contest) {
        ContestResponse contestResponse = contestMapper.toResponse(contest);
        contestResponse.setNumberQuestion(questionContestService.getNumberQuestionOfContest(contest.getId()));
        contestResponse.setNumberUser(userContestService.getNumberUserOfContest(contest.getId()));
        contestResponse.setUserCreated(userService.toUserBaseResponse(contest.getCreatedBy()));
        return contestResponse;
    }

    private List<ContestResponse> mapToListResponse(List<Contest> contests) {
        return contests.stream()
                .map(this::mapToContestResponse)
                .collect(Collectors.toList());
    }

    public ContestDetailResponse getContestDetail(String contestId) {
        return contestMapper.toDetailResponse(findById(contestId));
    }

    public NumberContestOpenResponse getNumberContestOpening() {
        int contentOpen = this.repository.findTotalContestOpen();
        int contentUserJoined = this.repository.findTotalContestUserJoinedAndNotClose(contextUtil.getUserId());
        return NumberContestOpenResponse.builder().number(contentOpen+ contentUserJoined).build();
    }

    public boolean isContestOpen(String contestId) {
        return this.repository.isContestOpen(contestId);
    }

    public Page<UserDoneQuestionProjection> getListUserDoneQuestionContest(Pageable pageable, String questionId) {
        return this.repository.findListUserDoneQuestionContest(pageable,questionId);
    }
}
