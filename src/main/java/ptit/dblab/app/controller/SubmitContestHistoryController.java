package ptit.dblab.app.controller;

import ptit.dblab.app.dto.request.QuestionCompleteRequest;
import ptit.dblab.app.dto.response.SubmitContestHistoryResponse;
import ptit.dblab.app.dto.response.SubmitHistoryResponse;
import ptit.dblab.app.interfaceProjection.QuestionCompleteProjection;
import ptit.dblab.app.service.SubmitContestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/submit-contest")
@RequiredArgsConstructor
public class SubmitContestHistoryController {
    private final SubmitContestService submitContestService;

    @GetMapping("/user")
    public ResponseEntity<Page<SubmitHistoryResponse>> getSubmitHistoryByUserIdAndQuestion(@RequestParam String questionContestId,
                                                                                           @RequestParam(required = false) String userId,
                                                                                           Pageable pageable) {
        return ResponseEntity.ok(submitContestService.getSubmitContestByUserAndQuestionCode(questionContestId,userId,pageable));
    }

    @GetMapping("/filter/user")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<Page<SubmitHistoryResponse>> getSubmitHistoryByUserId(@RequestParam String contestId,
                                                                                           @RequestParam String userId,
                                                                                           Pageable pageable) {
        return ResponseEntity.ok(submitContestService.getSubmitContestByUser(contestId,userId,pageable));
    }

    @PostMapping("/check/complete")
    public ResponseEntity<List<QuestionCompleteProjection>> getQuestionStatus(
            @RequestBody QuestionCompleteRequest request) {
        List<QuestionCompleteProjection> questionStatus = submitContestService.getQuestionStatus(request.getUserId(), request.getQuestionIds());
        return ResponseEntity.ok(questionStatus);
    }

    @GetMapping("/history/{contestId}")
    public ResponseEntity<Page<SubmitContestHistoryResponse>> getSubmitContestHistoryByContestId(Pageable pageable,@PathVariable String contestId) {
        return ResponseEntity.ok(submitContestService.getSubmitContestHistoryByContestId(contestId,pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubmitContestHistoryResponse> getSubmitHistoryDetail(@PathVariable String id) {
        return ResponseEntity.ok(submitContestService.getSubmitContestDetail(id));
    }


}
