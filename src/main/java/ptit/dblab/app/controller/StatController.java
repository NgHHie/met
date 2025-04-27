package ptit.dblab.app.controller;

import ptit.dblab.app.dto.request.ContestInfRequest;
import ptit.dblab.app.dto.request.TopUserRequest;
import ptit.dblab.app.dto.response.*;
import ptit.dblab.app.dto.response.*;
import ptit.dblab.app.enumerate.ContestType;
import ptit.dblab.app.enumerate.IntervalTypeEnum;
import ptit.dblab.app.interfaceProjection.*;
import ptit.dblab.app.interfaceProjection.*;
import ptit.dblab.app.service.StatService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/stats")
public class StatController {

    private final StatService statService;

    @GetMapping("/user-submit/practice")
    public ResponseEntity<Page<UserSubmitStat>> getUserSubmitStat(Pageable pageable,
                                                                  @RequestParam(required = false) String classRoomId,
                                                                  @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(statService.getUserSubmitStat(pageable,classRoomId,keyword));
    }


    @GetMapping("/export/user-submit")
    public ResponseEntity<Resource> exportUserStatsiticSubmit(Pageable pageable,
                                                              @RequestParam(required = true) String contestId) throws IOException {
        return statService.exportUserStatisticSubmit(pageable,contestId);
    }

    @GetMapping("/user-detail/{id}")
    public ResponseEntity<UserDetailStat> getUserDetailStat(@PathVariable String id,
                                                            @RequestParam(required = false) String contestId,
                                                            @RequestParam ContestType contestType) throws Exception {
        return ResponseEntity.ok(statService.getUserDetailStat(id,contestId,contestType));
    }

    @PostMapping("/contest/sumary")
    public ResponseEntity<ContestInfoResponse> getContestInfo(@RequestBody ContestInfRequest request) {
        return ResponseEntity.ok(statService.getContestInf(request));
    }

    @GetMapping("/question-submit/user/{userId}")
    public ResponseEntity<Page<QuestionUserSubmit>> getQuestionUserSubmit(@PathVariable String userId,
                                                                          @RequestParam(required = false) String contestId,
                                                                          @RequestParam ContestType contestType,
                                                                          Pageable pageable) {
        return ResponseEntity.ok(statService.getListQuestionUserSubmit(userId,contestId,contestType,pageable));
    }

    @PostMapping("/top-user")
    public ResponseEntity<Page<TopUserResponse>> getTopUsers(@RequestBody TopUserRequest request, Pageable pageable) {
        return ResponseEntity.ok(statService.getTopUser(request,pageable));
    }

    @GetMapping("/question-submit/level/user/{userId}")
    public ResponseEntity<List<QuestionLevel>> getNumberQuestionLevel(@PathVariable String userId,
                                                                      @RequestParam(required = false) String contestId,
                                                                      @RequestParam ContestType contestType) {
        return ResponseEntity.ok(statService.getQuestionLevels(userId,contestId,contestType));
    }

    @GetMapping("/question-stat")
    public ResponseEntity<Page<QuestionStat>> getQuestionStat(Pageable pageable, @RequestParam(required = false) String contestId,
                                                              @RequestParam ContestType contestType,
                                                              @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(statService.getQuestionStats(pageable,contestId,contestType,keyword));
    }

    @GetMapping("/user-submit/count")
    public ResponseEntity<Page<UserSubCountResponse>> getUserSubCountStat(Pageable pageable, @RequestParam IntervalTypeEnum intervalType,
                                                                          @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(statService.getUserSubCount(pageable,intervalType,keyword));
    }

    @GetMapping("/submit/count")
    public ResponseEntity<List<TotalSubmitForDay>> getTotalSubmitCount(@RequestParam IntervalTypeEnum intervalType) {
        return ResponseEntity.ok(statService.getTotalSubmitByInterval(intervalType));
    }

    @GetMapping("/user-submit/contest/{contestId}")
    public ResponseEntity<Page<UserSubmitContestStatResponse>> getSubmitContestStat(Pageable pageable, @PathVariable String contestId,
                                                                                    @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(statService.getUserSubmitStatContest(pageable,contestId,keyword));
    }

    @GetMapping("/common-info")
    public ResponseEntity<CommonInfoStatResponse> getCommonInfoStat() {
        return ResponseEntity.ok(statService.getCommonInfoStat());
    }

    @GetMapping("/question/level")
    public ResponseEntity<List<QuestionLevel>> getAllQuestionStatByLevel() {
        return ResponseEntity.ok(statService.getAllQuestionStatByLevel());
    }

    @GetMapping("/contest/{contestId}/range-question")
    public ResponseEntity<List<RangeQuestionResponse>> getRangeQuestionContest(@PathVariable String contestId) {
        return ResponseEntity.ok(statService.getRangeQuestionContest(contestId));
    }
}
