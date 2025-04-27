package ptit.dblab.app.controller;

import ptit.dblab.shared.utils.ContextUtil;
import ptit.dblab.app.dto.request.UserTrackerRequest;
import ptit.dblab.app.dto.response.CheatUserResponse;
import ptit.dblab.app.dto.response.TrackerDetailResponse;
import ptit.dblab.app.dto.response.UserTrackerResponse;
import ptit.dblab.app.event.producer.UserTrackerProducer;
import ptit.dblab.app.service.UserTrackerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/tracker")
public class UserTrackerController {

    private static final Logger log = LoggerFactory.getLogger(UserTrackerController.class);
    private final UserTrackerProducer producer;
    private final ContextUtil contextUtil;
    private final UserTrackerService userTrackerService;

    @PostMapping("/push")
    public ResponseEntity<Void> receiveTracker(@RequestBody UserTrackerRequest request) {
        log.info("receive tracker request: {}", contextUtil.getUserId());
        request.setUserId(contextUtil.getUserId());
        producer.send(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{contestId}/logs")
    public ResponseEntity<Page<UserTrackerResponse>> getUserTrackers(Pageable pageable, @PathVariable String contestId,
                                                                     @RequestParam(required = false) String keyword) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        return ResponseEntity.ok(userTrackerService.getUserTrackers(sortedPageable,contestId,keyword));
    }

    @GetMapping("/cheat/{contestId}")
    public ResponseEntity<Page<CheatUserResponse>> getCheatUsers(Pageable pageable,@PathVariable String contestId) {
        return ResponseEntity.ok(userTrackerService.getCheatUser(pageable,contestId));
    }

    @GetMapping("/cheat/detail/{contestId}/{userId}")
    public ResponseEntity<TrackerDetailResponse> getUserTrackerDetail(@PathVariable String userId,
                                                                      @PathVariable String contestId) {
        return ResponseEntity.ok(userTrackerService.getTrackerDetails(userId,contestId));
    }
}
