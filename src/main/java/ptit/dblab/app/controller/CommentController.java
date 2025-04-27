package ptit.dblab.app.controller;

import ptit.dblab.app.dto.request.CommentLikeRequest;
import ptit.dblab.app.dto.request.CommentRequest;
import ptit.dblab.app.dto.response.CommentResponse;
import ptit.dblab.app.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("")
    public ResponseEntity<CommentResponse> create(@RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.create(request));
    }

    @GetMapping("/question/{questionId}")
    public ResponseEntity<Page<CommentResponse>> getComments(@PathVariable String questionId, Pageable pageable) {
        return ResponseEntity.ok(commentService.findCommentsByParent(questionId,pageable));
    }

    @PostMapping("/like")
    public ResponseEntity<Void> updateCountLike(@RequestBody CommentLikeRequest request) {
        commentService.updateCountLike(request);
        return ResponseEntity.noContent().build();
    }
}
