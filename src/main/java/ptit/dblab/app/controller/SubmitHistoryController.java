package ptit.dblab.app.controller;

import ptit.dblab.app.dto.request.EvaluateSubmitRequest;
import ptit.dblab.app.dto.request.QuestionCompleteRequest;
import ptit.dblab.app.dto.response.SubmitHistoryDetailResponse;
import ptit.dblab.app.dto.response.SubmitHistoryResponse;
import ptit.dblab.app.interfaceProjection.QuestionCompleteProjection;
import ptit.dblab.app.service.SubmitHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/submit-history")
@RequiredArgsConstructor
public class SubmitHistoryController {

    private final SubmitHistoryService submitHistoryService;

    @GetMapping()
    public ResponseEntity<Page<SubmitHistoryResponse>> getAllSubmitHistory(Pageable pageable) {
        return ResponseEntity.ok(submitHistoryService.getSubmitHistory(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubmitHistoryDetailResponse> getSubmitHistoryDetail(@PathVariable String id) {
        return ResponseEntity.ok(submitHistoryService.getSubmitHistoryDetail(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<SubmitHistoryResponse>> getSubmitHistoryByUserIdAndQuestion(@PathVariable String userId,@RequestParam String questionId,Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "time_submit"));
        }
        return ResponseEntity.ok(submitHistoryService.getSubmitHistoryByUserIdAndQuestionId(userId,questionId,pageable));
    }

    @GetMapping("/user/filter/{userId}")
    public ResponseEntity<Page<SubmitHistoryResponse>> getSubmitHistoryByUserId(@PathVariable String userId,Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "time_submit"));
        }
        return ResponseEntity.ok(submitHistoryService.getSubmitHisByUser(userId,pageable));
    }

    @PostMapping("/check/complete")
    public ResponseEntity<List<QuestionCompleteProjection>> getQuestionStatus(
            @RequestBody QuestionCompleteRequest request) {
        List<QuestionCompleteProjection> questionStatus = submitHistoryService.getQuestionStatus(request.getUserId(), request.getQuestionIds());
        return ResponseEntity.ok(questionStatus);
    }

    @PutMapping("/update-evaluate")
    public ResponseEntity<Void> updateEvaluate(@RequestBody EvaluateSubmitRequest request) {
        submitHistoryService.updateEvaluate(request);
        return ResponseEntity.noContent().build();
    }
}
