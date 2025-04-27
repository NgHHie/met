package ptit.dblab.app.controller;

import ptit.dblab.app.dto.request.TopicRequest;
import ptit.dblab.app.dto.response.TopicCountResponse;
import ptit.dblab.app.dto.response.TopicDetailResponse;
import ptit.dblab.app.dto.response.TopicResponse;
import ptit.dblab.app.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/topics")
public class TopicController {
    private final TopicService topicService;

    @PostMapping("")
    public ResponseEntity<Void> createTopic(@RequestBody TopicRequest topicRequest) {
        topicService.createTopic(topicRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public ResponseEntity<Page<TopicResponse>> getTopics(Pageable pageable,@RequestParam(required = false) String keyword,
                                                         @RequestParam(required = false,defaultValue = "desc") String sort) {
        return ResponseEntity.ok(topicService.getTopics(pageable, sort, keyword));
    }

    @GetMapping("/{topicId}")
    public ResponseEntity<TopicDetailResponse> getTopicDetail(@PathVariable String topicId) {
        return ResponseEntity.ok(topicService.getTopicDetail(topicId));
    }

    @GetMapping("/count-new")
    public ResponseEntity<TopicCountResponse> getNumberTopicNew() {
        return ResponseEntity.ok(topicService.getNumberTopicNew());
    }
}
