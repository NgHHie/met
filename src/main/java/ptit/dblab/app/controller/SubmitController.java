package ptit.dblab.app.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ptit.dblab.shared.enumerate.TypeQuestion;
import ptit.dblab.app.dto.response.CpSubmitResponse;
import ptit.dblab.app.dto.response.EvaluateResponse;
import ptit.dblab.app.utils.TableUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ptit.dblab.app.dto.request.SubmitRequest;
import ptit.dblab.app.dto.response.SubmitResponse;
import ptit.dblab.app.service.SubmitService;

import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/executor")
@CrossOrigin
@RequiredArgsConstructor
public class SubmitController {

	private static final Logger log = LoggerFactory.getLogger(SubmitController.class);
	private final SubmitService submitService;

	@PostMapping("")
	@PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
	public ResponseEntity<List<SubmitResponse>> executor(@RequestBody SubmitRequest submit) throws Exception {
		return ResponseEntity.ok(submitService.submitRoleAdmin(submit.getSql(),submit.getPrefixTable(),submit.getTableUses(),submit.getTypeDatabaseId()));
	}

	@PostMapping("/user")
	public ResponseEntity<SubmitResponse> executorUser(@RequestBody SubmitRequest submit) throws Exception {
		return ResponseEntity.ok(submitService.executeSqlUser(submit.getSql(),submit.getQuestionId(),submit.getTypeDatabaseId()));
	}

	@PostMapping("/generate/testcase")
	@PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
	public ResponseEntity<List<SubmitResponse>> createTestCases(@RequestBody List<SubmitRequest> executor) throws Exception {
		return ResponseEntity.ok(submitService.createTestCase(executor,false));
	}

	@PostMapping("/generate/testcase/file")
	@PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
	public ResponseEntity<List<SubmitResponse>> uploadExcelFile(@RequestParam("file") MultipartFile file,
																@RequestParam("answer") String answer,
																@RequestParam("typeQuestion")TypeQuestion typeQuestion,
																@RequestParam("prefixTable") String prefixTable,
																@RequestParam("typeDatabaseId") String typeDatabaseId) throws Exception {
		List<SubmitRequest> requests = submitService.readTestCaseFromFile(file,answer,typeQuestion,prefixTable,typeDatabaseId);
		return ResponseEntity.ok(submitService.createTestCase(requests,true));
	}

	@PostMapping("/submit")
	public ResponseEntity<SubmitResponse> submitQuestion(@RequestBody SubmitRequest submit, HttpServletRequest request) throws Exception {
		return ResponseEntity.ok(submitService.sendSubmit(submit,request));
	}

	@PostMapping("/submit-file")
	public ResponseEntity<SubmitResponse> submitQuestionWithFile(
			@RequestParam("file") MultipartFile file,
			@RequestParam("questionId") String questionId,
			@RequestParam("questionContestId") String questionContestId,
			@RequestParam("typeDatabaseId") String typeDatabaseId,
			@RequestParam("isSubmitContest") boolean isSubmitContest,HttpServletRequest clientRequest) throws Exception {

		if (file.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		String sqlContent;
		try {
			sqlContent = new String(file.getBytes());
		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		SubmitRequest request = new SubmitRequest();
		request.setQuestionId(questionId);
		request.setTypeDatabaseId(typeDatabaseId);
		request.setSql(sqlContent);
		request.setSubmitContest(isSubmitContest);
		request.setQuestionContestId(questionContestId);
		// Process the content with your service
		SubmitResponse response = submitService.sendSubmit(request,clientRequest);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/generate-script/{typeDatabase}")
	public ResponseEntity<String> generateScrip(@PathVariable String typeDatabase) {
		return ResponseEntity.ok(submitService.generateScriptCreateTable(typeDatabase));
	}

	@PostMapping("/webhook/result")
	public ResponseEntity<Void> submitResultWebHook(@RequestBody CpSubmitResponse submitResponse) throws Exception {
		submitService.handleSubmitResult(submitResponse);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/evaluate/{submitHisId}")
	public ResponseEntity<EvaluateResponse> getEvaluateResponse(@PathVariable String submitHisId) {
		return ResponseEntity.ok(submitService.getEvaluateFromAI(submitHisId));
	}
}
