package ptit.dblab.app.controller;

import ptit.dblab.app.dto.request.ContestRequest;
import ptit.dblab.app.dto.response.*;
import ptit.dblab.app.dto.response.*;
import ptit.dblab.app.enumerate.ContestStatus;
import ptit.dblab.app.interfaceProjection.UserDoneQuestionProjection;
import ptit.dblab.app.service.ContestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contest")
@CrossOrigin
public class ContestController {
    private final ContestService contestService;

    @GetMapping("/create")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ContestBaseResponse> createContest() {
        return ResponseEntity.ok(contestService.create());
    }

    @PutMapping("{contestId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<Void> updateContest(@PathVariable String contestId,@RequestBody ContestRequest request) {
        contestService.update(contestId,request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sysadmin")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<Page<ContestResponse>> findContests(Pageable pageable,
                                                              @RequestParam(required = false) ContestStatus status,
                                                              @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(contestService.getAllContest(pageable,status,keyword));
    }

    @GetMapping("")
    public ResponseEntity<Page<ContestResponse>> findContestPublics(Pageable pageable) {
        return ResponseEntity.ok(contestService.getAllContestPublic(pageable));
    }

    @GetMapping("/sysadmin/{contestId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ContestDetailResponseAdmin> findById(@PathVariable String contestId) {
        return ResponseEntity.ok(contestService.getContestDetailAdmin(contestId));
    }

    @GetMapping("/waiting/{contestId}")
    public ResponseEntity<ContestResponse> getContestWaitingById(@PathVariable String contestId) {
        return ResponseEntity.ok(contestService.getContestWaiting(contestId));
    }

    @GetMapping("/{contestId}")
    public ResponseEntity<ContestDetailResponse> getContestDetail(@PathVariable String contestId) {
        return ResponseEntity.ok(contestService.getContestDetail(contestId));
    }

    @GetMapping("/user/joined")
    public ResponseEntity<List<ContestResponse>> getListUserContestByUser() {
        return ResponseEntity.ok(contestService.getContestJoinByUserId());
    }

    @GetMapping("/user/current-contest/exam")
    public ResponseEntity<ContestResponse> getCurrentContestExamByUser() {
        return ResponseEntity.ok(contestService.getCurrentContestExamRunning());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        contestService.deleteContest(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count/opening")
    public ResponseEntity<NumberContestOpenResponse> getNumberContestOpen() {
        return ResponseEntity.ok(contestService.getNumberContestOpening());
    }

    @GetMapping("/question-contest/{questionId}/user-done")
    public ResponseEntity<Page<UserDoneQuestionProjection>> getListUserDoneQuestion(Pageable pageable, @PathVariable String questionId) {
        return ResponseEntity.ok(contestService.getListUserDoneQuestionContest(pageable,questionId));
    }
}
