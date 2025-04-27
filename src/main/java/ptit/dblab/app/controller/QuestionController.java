package ptit.dblab.app.controller;


import org.springframework.web.multipart.MultipartFile;
import ptit.dblab.shared.enumerate.TypeQuestion;
import ptit.dblab.app.dto.response.*;
import ptit.dblab.app.enumerate.LevelQuestion;
import ptit.dblab.app.interfaceProjection.UserDoneQuestionProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ptit.dblab.app.dto.request.QuestionRequest;
import ptit.dblab.app.mapper.QuestionMapper;
import ptit.dblab.app.service.QuestionService;
import ptit.dblab.app.dto.response.QuestionBasicResponse;
import ptit.dblab.app.dto.response.QuestionResponseAdmin;
import ptit.dblab.app.dto.response.QuestionResponseDetail;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/question")
@CrossOrigin
@RequiredArgsConstructor
public class QuestionController {

	private final QuestionService questionService;

	private final QuestionMapper questionMapper;
	
	@GetMapping("/admin")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<Page<QuestionBasicResponse>> findQuestionListDetail(Pageable pageable,
                                                                              @RequestParam(required = false) String keyword,
                                                                              @RequestParam(required = false) LevelQuestion level,
                                                                              @RequestParam(required = false) String typeDatabaseId,
                                                                              @RequestParam(required = false) TypeQuestion typeQuestion,
                                                                              @RequestParam(required = false) String createdBy) {
        return ResponseEntity.ok(questionService.getQuestionListDetail(pageable,keyword,level,typeDatabaseId,typeQuestion,createdBy));
    }
	
	@GetMapping()
    public ResponseEntity<Page<QuestionBasicResponse>> findQuestionListBasic(Pageable pageable,
                                                                             @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(questionService.getBasicQuestionListEnable(pageable,keyword));
    }
	
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<QuestionResponseAdmin> findByIdDetail(@PathVariable String id) {
        return ResponseEntity.ok(questionService.getQuestionRoleAdminById(id));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponseDetail> findByIdBasic(@PathVariable String id) {
        return ResponseEntity.ok(questionMapper.toDetailResponse(questionService.findById(id)));
    }
    
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<QuestionBasicResponse> create(@RequestBody QuestionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<Void> update(@PathVariable String id, @RequestBody QuestionRequest request) {
        questionService.update(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        questionService.hardDelete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{questionId}/user-done")
    public ResponseEntity<Page<UserDoneQuestionProjection>> getListUserDoneQuestion(Pageable pageable,@PathVariable String questionId) {
        return ResponseEntity.ok(questionService.getListUserDoneQuestion(pageable,questionId));
    }

    @PostMapping("/import")
    public ResponseEntity<QuestionCreatedSummaryResponse> importQuestions(@RequestParam("file") MultipartFile file) throws IOException {
        List<QuestionRequest> requests = questionService.parseQuestionsFromExcel(file);
        return ResponseEntity.ok(questionService.createQuestionFromImport(requests));
    }
}
