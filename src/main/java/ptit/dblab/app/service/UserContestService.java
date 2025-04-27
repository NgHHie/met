package ptit.dblab.app.service;

import ptit.dblab.shared.exception.core.ValidateException;
import ptit.dblab.shared.utils.ContextUtil;
import ptit.dblab.app.dto.request.CheckJoinedRequest;
import ptit.dblab.app.dto.request.UserContestRequest;
import ptit.dblab.app.dto.response.ServerResponse;
import ptit.dblab.app.entity.*;
import ptit.dblab.app.entity.Contest;
import ptit.dblab.app.entity.User;
import ptit.dblab.app.entity.UserContest;
import ptit.dblab.app.enumerate.ContestStatus;
import ptit.dblab.app.enumerate.ErrorCode;
import ptit.dblab.app.enumerate.ModeContest;
import ptit.dblab.app.interfaceProjection.UserBaseInfoProjection;
import ptit.dblab.app.interfaceProjection.UserContestJoinStatus;
import ptit.dblab.app.mapper.UserContestMapper;
import ptit.dblab.app.repository.ContestRepository;
import ptit.dblab.app.repository.UserContestRepository;
import ptit.dblab.app.repository.UserRepository;
import ptit.dblab.app.utils.AppUtils;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserContestService {
    private static final Logger log = LoggerFactory.getLogger(UserContestService.class);
    private final UserContestRepository userContestRepository;
    private final UserContestMapper userContestMapper;
    private final ContestRepository contestRepository;
    private final UserRepository userRepository;
    private final ContextUtil contextUtil;

    public void updateUserContests(Contest contest, List<UserContestRequest> requests) {
        List<UserContest> updatedUserContests = new ArrayList<>();
        List<UserContest> userContests = contest.getUsers();
        for (UserContestRequest userContestRequest : requests) {
            if(Objects.isNull(userContestRequest.getId())) {
                UserContest userContest = userContestMapper.toEntity(userContestRequest);
                userContest.setContest(contest);
                userContest.setTimeJoined(LocalDateTime.now());
                updatedUserContests.add(userContest);
            } else {
                UserContest userContest = getUserContest(userContests,userContestRequest.getId());
                if(Objects.nonNull(userContest)) {
                    userContestMapper.updateFromRequest(userContest,userContestRequest);
                    updatedUserContests.add(userContest);
                }
            }
        }
        userContests.clear();
        userContests.addAll(updatedUserContests);
    }

    @Transactional
    public void joinContest(UserContestRequest request) {
        //check overlap time
        Contest contestJoin = contestRepository.findById(request.getContest().getId()).orElseThrow(()->new ResourceNotFoundException("Contest not found"));
        if(contestJoin.getStatus() == ContestStatus.CLOSE) {
            throw new ValidateException(ErrorCode.CONTEST_CLOSE.getDescription());
        }
        if(!contestJoin.getIsPublic()) {
            throw new ValidateException(ErrorCode.CONTEST_NOT_PUBLIC.getDescription());
        }
        List<UserContest> contestOpen = this.userContestRepository.findOpenContestsByUser(request.getUser().getId());

        for(UserContest userContest : contestOpen) {
            Contest contest = userContest.getContest();
            if(contest.getMode() == ModeContest.EXAM && contestsOverlap(contestJoin,contest)) {
                throw new ValidateException(ErrorCode.OVERLAP_CONTEST.getDescription());
            }
        }
        if(userContestRepository.existsByUserId(request.getUser().getId(),contestJoin.getId())) {
            System.out.println(request.getUser().getId());
            throw new ValidateException(ErrorCode.USER_EXISTED_IN_CONTEST.getDescription());
        }
        UserContest userContest = userContestMapper.toEntity(request);
        userContest.setTimeJoined(LocalDateTime.now());
        userContestRepository.save(userContest);
    }

    public List<UserContestJoinStatus> checkJoin(CheckJoinedRequest request) {
        return userContestRepository.getUserContestJoinStatus(request.getUserId(), request.getContestIds());
    }

    public int getNumberUserOfContest(String contestId) {
        return userContestRepository.getNumberUserOfContest(contestId);
    }

    public List<Contest> getListContestByUser(String userId) {
        return userContestRepository.getListContestByUser(userId);
    }

    public Contest getContestExamRunning() {
        return userContestRepository.getContestExamRunningByUser(contextUtil.getUserId());
    }
    private boolean contestsOverlap(Contest contestJoin, Contest contestOpen) {
        LocalDateTime contestJoinStart = AppUtils.combineDateAndTime(contestJoin.getStartDay(), contestJoin.getStartTime());
        LocalDateTime contestJoinEnd = AppUtils.combineDateAndTime(contestJoin.getEndDay(), contestJoin.getEndTime());

        LocalDateTime contestOpenStart = AppUtils.combineDateAndTime(contestOpen.getStartDay(), contestOpen.getStartTime());
        LocalDateTime contestOpenEnd = AppUtils.combineDateAndTime(contestOpen.getEndDay(), contestOpen.getEndTime());

        return contestJoinStart.isBefore(contestOpenEnd) && contestOpenStart.isBefore(contestJoinEnd);
    }
    private UserContest getUserContest(List<UserContest> userContestList,String id) {
        return userContestList.stream()
                .filter(q -> q.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public ServerResponse addUserContest(MultipartFile file, String contestId) {
        List<String> usernames = new ArrayList<>();
        int resultHandleCount = 0;
        int userNotExistCount = 0;
        String description = "";
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Start from row 1 to skip header row
                Row row = sheet.getRow(i);
                if (row != null) {
                    String studentId = AppUtils.getCellValue(row.getCell(1));
                    if(Objects.nonNull(studentId) && !studentId.isEmpty()) {
                        resultHandleCount++;
                        usernames.add(studentId);
                    }
                }
            };
            List<UserBaseInfoProjection> userBaseInfoProjections = userRepository.getListUserBaseInfo(usernames);
            List<UserContest> userContests = new ArrayList<>();
            Contest contest = new Contest();
            contest.setId(contestId);
            log.info("******** userBaseInfoProjections: {}", userBaseInfoProjections.size());
            for(UserBaseInfoProjection userBaseInfoProjection : userBaseInfoProjections) {
                UserContest userContest = new UserContest();
                userContest.setContest(contest);
                userContest.setUser(User.builder().id(userBaseInfoProjection.getUserId()).build());
                userContest.setTimeJoined(LocalDateTime.now());
                userContests.add(userContest);
            }
            this.userContestRepository.saveAll(userContests);
            description = "Đọc "+resultHandleCount+ " user từ file. ";
            userNotExistCount = resultHandleCount - userBaseInfoProjections.size();
            if(userNotExistCount > 0) {
                description += userNotExistCount +" user không tồn tại trong hệ thống vui lòng tạo tài khoản cho các user và import lại";
            } else {
                description += "Đã thêm thành công "+ userBaseInfoProjections.size() +" vào contest thành công!";
            }
            log.info(description);
            return ServerResponse.builder()
                    .status(ErrorCode.SUCCESS.getCode())
                    .description(description)
                    .build();
        } catch (Exception e) {
            throw new ValidateException("Không thể đọc file xin vui lòng kiểm tra lại!");
        }
    }
}
