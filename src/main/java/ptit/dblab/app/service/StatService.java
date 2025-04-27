package ptit.dblab.app.service;

import ptit.dblab.shared.utils.ContextUtil;
import ptit.dblab.app.dto.request.ContestInfRequest;
import ptit.dblab.app.dto.request.TopUserRequest;
import ptit.dblab.app.dto.response.*;
import ptit.dblab.app.dto.response.*;
import ptit.dblab.app.entity.User;
import ptit.dblab.app.enumerate.ContestType;
import ptit.dblab.app.enumerate.IntervalTypeEnum;
import ptit.dblab.app.enumerate.Role;
import ptit.dblab.app.interfaceProjection.*;
import ptit.dblab.app.mapper.SubmitContestMapper;
import ptit.dblab.app.repository.*;
import ptit.dblab.app.utils.AppUtils;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ptit.dblab.app.interfaceProjection.*;
import ptit.dblab.app.repository.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StatService {
    private final SubmitHistoryRepository submitHistoryRepository;
    private final QuestionRepository questionRepository;
    private final SubmitContestExamRepository submitContestExamRepository;
    private final SubmitContestMapper submitContestMapper;
    private final SubmitContestService submitContestService;
    private final UserRepository userRepository;
    private final ContestRepository contestRepository;
    private final QuestionContestRepository questionContestRepository;
    private final ClassRoomRepository classRoomRepository;
    private final ContextUtil contextUtil;
    private final UserService userService;

    public Page<UserSubmitStat> getUserSubmitStat(Pageable pageable, String classRoomId, String keyword) {
        return submitHistoryRepository.getUserStatistics(pageable,classRoomId,keyword);
    }

    public UserDetailStat getUserDetailStat(String userId, String contestId, ContestType contestType) throws Exception {
        UserDetailStatProjection userDetailStatProjection;
        float totalPoint = 0;
        if(contestType == ContestType.CONTEST) {
            userDetailStatProjection = submitContestExamRepository.getUserDetailContestStat(userId,contestId);
            totalPoint = submitContestExamRepository.getTotalPointOfUserInContest(userId,contestId);
        } else {
            userDetailStatProjection = submitHistoryRepository.getUserDetailStat(userId);
            totalPoint = submitHistoryRepository.getTotalPointOfUser(userId);
        }
        UserDetailStat userDetailStat = new UserDetailStat();
        if(userDetailStatProjection != null) {
            userDetailStat.setUserCode(userDetailStatProjection.getUserCode());
            userDetailStat.setFullName(userDetailStatProjection.getFullName());
            userDetailStat.setNumQuestionDone(userDetailStatProjection.getNumQuestionDone());
            userDetailStat.setTotalSubmit(userDetailStatProjection.getTotalSubmit());
            userDetailStat.setTotalPoints(totalPoint);
            userDetailStat.setTotalCorrectQuestions(userDetailStatProjection.getTotalCorrectQuestions());
            userDetailStat.setTotalSubmitAc(userDetailStatProjection.getTotalSubmitAc());
            userDetailStat.setTotalSubmitWa(userDetailStatProjection.getTotalSubmitWa());
            userDetailStat.setTotalSubmitTle(userDetailStatProjection.getTotalSubmitTle());
            userDetailStat.setTotalSubmitCe(userDetailStatProjection.getTotalSubmitCe());
        } else {
            User currentUser = userService.findById(userId);
            userDetailStat.setUserCode(currentUser.getUserCode());
            userDetailStat.setFullName(AppUtils.getFullName(currentUser.getFirstName(),currentUser.getLastName()));
        }

        return userDetailStat;
    }

    public ContestInfoResponse getContestInf(ContestInfRequest request) {
        int numberUser = 0, numberQuestion = 0;
        if(request.getContestType() == ContestType.PRACTICE) {
            numberUser = submitHistoryRepository.getNumberUserJoinSubmitPractice();
            numberQuestion = questionRepository.getNumberOfQuestionPractice();
        }
        return ContestInfoResponse.builder()
                .numberUser(numberUser)
                .numberQuestion(numberQuestion)
                .build();
    }

    public Page<QuestionUserSubmit> getListQuestionUserSubmit(String userId, String contestId, ContestType contestType, Pageable pageable) {
        switch (contestType) {
            case CONTEST -> {
                return submitContestExamRepository.findListQuestionContestUserSub(userId,contestId,pageable);
            }
            case PRACTICE -> {
                return submitHistoryRepository.findListQuestionUserSub(userId,pageable);
            }
        }
        return Page.empty();
    }

    public Page<TopUserResponse> getTopUser(TopUserRequest request, Pageable pageable) {
        if(request.getContestType() == ContestType.PRACTICE) {
            Page<TopUserProjection>  topUserProjections = submitHistoryRepository.getTopUserPractice(pageable);
            List<TopUserProjection> topUserProjectionList = topUserProjections.getContent();
            List<TopUserResponse> topUserResponseList = new ArrayList<>();
            int startRank = pageable.getPageNumber() * pageable.getPageSize();
            int index = 0;
            for(TopUserProjection topUserProjection : topUserProjectionList) {
                TopUserResponse topUserResponse = new TopUserResponse();
                topUserResponse.setId(topUserProjection.getId());
                topUserResponse.setUserCode(topUserProjection.getUserCode());
                topUserResponse.setFullName(topUserProjection.getFullName());
                topUserResponse.setNumQuestionDone(topUserProjection.getNumQuestionDone());
                topUserResponse.setTotalPoints(topUserProjection.getTotalPoints());
                topUserResponse.setRank(startRank + index + 1);
                topUserResponse.setAvatar(topUserProjection.getAvatar());
                index++;
                topUserResponseList.add(topUserResponse);
            }
            return new PageImpl<>(topUserResponseList, pageable, topUserProjections.getTotalElements());
        }
        return Page.empty(pageable);
    }

    public List<QuestionLevel> getQuestionLevels(String userId, String contestId, ContestType contestType) {
        return switch (contestType) {
            case CONTEST -> submitContestExamRepository.getQuestionLevels(userId, contestId);
            case PRACTICE -> submitHistoryRepository.getQuestionLevels(userId);
        };
    }

    public Page<QuestionStat> getQuestionStats(Pageable pageable,String contestId,ContestType contestType,String keyword) {
        if(contestType == ContestType.PRACTICE) {
            return submitHistoryRepository.getQuestionStats(pageable,keyword);
        } else {
            return submitContestExamRepository.getQuestionContestStats(pageable,contestId,keyword);
        }
    }

    public QuestionStat getQuestionSubInfo(String questionId) {
        return submitHistoryRepository.getQuestionSubInfo(questionId);
    }

    public Page<UserSubCountResponse> getUserSubCount(Pageable pageable, IntervalTypeEnum intervalType, String keyword) {
        Page<UserJoined> userJoinedPage = submitHistoryRepository.getUserJoined(pageable,keyword);
        List<UserJoined> userJoinedList = userJoinedPage.getContent();
        List<UserSubCountResponse> userSubCountResponseList = new ArrayList<>();
        for(UserJoined userJoined : userJoinedList) {
            UserSubCountResponse userSubCountResponse = new UserSubCountResponse();
            userSubCountResponse.setUserId(userJoined.getUserId());
            userSubCountResponse.setUserCode(userJoined.getUserCode());
            String fullName = (userJoined.getLastName() != null ? userJoined.getLastName() + " " : "") +
                    (userJoined.getFirstName() != null ? userJoined.getFirstName() : "");
            userSubCountResponse.setFullName(fullName.trim());
            List<UserSubCount> userSubCount = submitHistoryRepository.getUserSubCount(userJoined.getUserId(),intervalType.getValue());
            userSubCountResponse.setSubmits(mapSubOfDay(userSubCount));
            userSubCountResponseList.add(userSubCountResponse);
        }
        userSubCountResponseList.sort(
                Comparator.comparingInt(UserSubCountResponse::getTodaySubmissionCount).reversed()
        );
        return new PageImpl<>(userSubCountResponseList, pageable, userJoinedPage.getTotalElements());
    }

    public List<TotalSubmitForDay> getTotalSubmitByInterval(IntervalTypeEnum intervalType) {
        return submitHistoryRepository.getTotalSubmitByInterval(intervalType.getValue());
    }
    private List<SubmitCountResponse> mapSubOfDay(List<UserSubCount> userSubCountList) {
        List<SubmitCountResponse> userSubCountResponseList = new ArrayList<>();
        for(UserSubCount userSubCount : userSubCountList) {
            SubmitCountResponse submitOfDayResponse = new SubmitCountResponse();
            submitOfDayResponse.setSubmitDate(userSubCount.getSubmitDate());
            submitOfDayResponse.setTotalSub(userSubCount.getTotalSubmit());
            userSubCountResponseList.add(submitOfDayResponse);
        }
        return userSubCountResponseList;
    }

    public ResponseEntity<Resource> exportUserStatisticSubmit(Pageable pageable,String contestId) throws IOException {
        Page<UserSubmitContestStatResponse> submitContestStatResponses = getUserSubmitStatContest(pageable,contestId,null);
        List<UserSubmitContestStatResponse> stats = submitContestStatResponses.getContent();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Kết quả contest");

        // Create header row
        Row headerRow = sheet.createRow(0);
        List<String> headers = new ArrayList<>(List.of(
               "STT", "Mã SV", "Họ và tên", "Số câu hỏi đã làm", "Số câu làm đúng", "Số lần submit"
        ));


        if(!stats.isEmpty() && Objects.nonNull(stats.get(0).getDetails())) {
            for(UserQuestionSubmitDetail detail : stats.get(0).getDetails()) {
                headers.add(detail.getQuestionCode());
            }
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(createHeaderCellStyle(workbook));
            }
            // Populate data rows
            int rowNum = 1;
            int stt = 1;
            for (UserSubmitContestStatResponse stat : stats) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(stt++);
                row.createCell(1).setCellValue(stat.getUserCode());
                row.createCell(2).setCellValue(stat.getFullName());
                row.createCell(3).setCellValue(stat.getNumQuestionDone());
                row.createCell(4).setCellValue(stat.getTotalSubmitAc());
                row.createCell(5).setCellValue(stat.getTotalSubmit());
                int questionColumn = 6;
                for (UserQuestionSubmitDetail detail : stat.getDetails()) {
                    // For each question detail, create a new row and populate
                    String description = detail.getTotalTest() == 0 ? "-" : detail.getMaxTestPass()+"/"+detail.getTotalTest();
                    row.createCell(questionColumn++).setCellValue(description);
                }
            }
        }

        // Auto-size columns
        for (int i = 0; i < headers.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        Sheet historySheet = workbook.createSheet("Lịch sử submit chi tiết");
        Row historyHeaderRow = historySheet.createRow(0);
        List<String> historyHeaders = List.of("Mã câu hỏi", "Tiêu đề", "Mã sinh viên", "Họ và tên",
                "Thời gian submit", "Trạng thái", "Kết quả","Database");

        for (int i = 0; i < historyHeaders.size(); i++) {
            Cell cell = historyHeaderRow.createCell(i);
            cell.setCellValue(historyHeaders.get(i));
            cell.setCellStyle(createHeaderCellStyle(workbook));
        }
        int historyRowNum = 1;
        List<SubmitContestHistoryResponse> histories = submitContestService.getAllSubmitContestHistoryByContestId(contestId);
        for(SubmitContestHistoryResponse his : histories) {
            Row row = historySheet.createRow(historyRowNum++);
            row.createCell(0).setCellValue(his.getQuestionContest().getQuestion().getQuestionCode());
            row.createCell(1).setCellValue(his.getQuestionContest().getQuestion().getTitle());
            row.createCell(2).setCellValue(his.getUser().getUserCode());
            String fullName = Objects.requireNonNullElse(his.getUser().getLastName(),"") + " " + Objects.requireNonNullElse(his.getUser().getFirstName(), "");
            row.createCell(3).setCellValue(fullName);
            row.createCell(4).setCellValue(AppUtils.formatLocalDateTime(his.getTimeSubmit()));
            row.createCell(5).setCellValue(his.getStatus().name());
            row.createCell(6).setCellValue(his.getTestPass()+"/"+his.getTotalTest());
            row.createCell(7).setCellValue(his.getDatabase().getName());
        }
        // Write workbook to a byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        workbook.close();

        // Create a ByteArrayResource from the byte array
        ByteArrayResource resource = new ByteArrayResource(byteArrayOutputStream.toByteArray());

        // Return the file as a downloadable response
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=learnsql_contest_result.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    public Page<UserSubmitContestStatResponse> getUserSubmitStatContest(Pageable pageable,String contestId, String keyword) {
        Page<UserSubmitStat> userSubmitStatPage = submitContestExamRepository.getUserStatisticsSubmitContest(pageable,contestId,keyword);
        List<UserSubmitContestStatResponse> userSubmitContestStatResponseList = new ArrayList<>();
        List<UserSubmitStat> stats = userSubmitStatPage.getContent();
        int rank = pageable.getPageNumber()* pageable.getPageSize() + 1;
        for(UserSubmitStat stat : stats) {
            List<UserQuestionSubmitDetail> details = submitContestExamRepository.getStatQuestionSubmitContestByUserId(stat.getId(),contestId);
            UserSubmitContestStatResponse submitContestStatResponse = submitContestMapper.toUserSubmitContestStatResponse(stat,details);
            submitContestStatResponse.setRank(rank);
            userSubmitContestStatResponseList.add(submitContestStatResponse);
            rank++;
        }
        return new PageImpl<>(userSubmitContestStatResponseList, pageable, userSubmitStatPage.getTotalElements());
    }
    private CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    public CommonInfoStatResponse getCommonInfoStat() {
        String userId = contextUtil.getUser().getRole().equals(Role.ADMIN.name()) ? null : contextUtil.getUserId();
        return CommonInfoStatResponse.builder()
                .numberUser(userRepository.findTotalUser(userId))
                .numberQuestion(questionRepository.findTotalQuestions(userId))
                .numberContest(contestRepository.findTotalContest(userId))
                .numberClassRoom(classRoomRepository.findTotalClassRoom(userId))
                .build();
    }

    public List<QuestionLevel> getAllQuestionStatByLevel() {
        return questionRepository.getAllQuestionLevelStats();
    }

    public List<RangeQuestionResponse> getRangeQuestionContest(String contestId) {
        int numQuestion = questionContestRepository.getNumberQuestionOfContest(contestId);
        List<RangeQuestionContestProjection> rangeQuestions = submitContestExamRepository.getRangeQuestionContest(contestId);
        List<RangeQuestionResponse> rangeQuestionResponseList = rangeQuestions.stream()
                .filter(range -> isRangeLessThanOrEqualTo(range.getRange(),numQuestion))
                .map(range -> RangeQuestionResponse.builder()
                        .range(range.getRange())
                        .numUser(range.getNumUser())
                        .build())
                .toList();
        if(!rangeQuestionResponseList.isEmpty()) {
            RangeQuestionResponse lastRange = rangeQuestionResponseList.get(rangeQuestionResponseList.size()-1);
            String rangeFormat = lastRange.getRange();
            if(rangeFormat.contains("-")) {
                String[] numbers = rangeFormat.split("-");
                int secondNumber = Integer.parseInt(numbers[1]);
                if(secondNumber < numQuestion) {
                    rangeFormat = numbers[0]+"-"+ numQuestion;
                }
            }
            lastRange.setRange(rangeFormat+"/"+numQuestion);
        }
        return rangeQuestionResponseList;
    }

    private boolean isRangeLessThanOrEqualTo(String range, int numberQuestion) {
        if (range.contains("+")) {
            return numberQuestion >= Integer.parseInt(range.replace("+","").trim());
        }
        if (range.contains("-")) {
            String[] parts = range.split("-");
            int upperBound = Integer.parseInt(parts[1]);
            return upperBound <= numberQuestion;
        }
        int value = Integer.parseInt(range);
        return value <= numberQuestion;
    }
}
