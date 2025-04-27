package ptit.dblab.app.controller;

import ptit.dblab.app.dto.request.CheckJoinedRequest;
import ptit.dblab.app.dto.request.UserContestRequest;
import ptit.dblab.app.dto.response.ServerResponse;
import ptit.dblab.app.interfaceProjection.UserContestJoinStatus;
import ptit.dblab.app.service.UserContestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-contest")
@CrossOrigin
public class UserContestController {
    private final UserContestService userContestService;

    @PostMapping("/join")
    public ResponseEntity<Void> joinContest(@RequestBody UserContestRequest request) {
        userContestService.joinContest(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/check-join")
    public ResponseEntity<List<UserContestJoinStatus>> checkJoinContest(@RequestBody CheckJoinedRequest request) {
        return ResponseEntity.ok(userContestService.checkJoin(request));
    }

    @PostMapping("/import-file/add-user")
    public ResponseEntity<ServerResponse> uploadFileAddUserContest(@RequestParam("file") MultipartFile file,
                                                                   @RequestParam String contestId) {
        return ResponseEntity.ok(userContestService.addUserContest(file,contestId));
    }
}
