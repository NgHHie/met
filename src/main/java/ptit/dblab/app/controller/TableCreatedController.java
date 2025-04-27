package ptit.dblab.app.controller;

import java.util.List;

import ptit.dblab.app.dto.request.TableCreatedRequest;
import ptit.dblab.app.dto.response.TableCreatedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ptit.dblab.app.service.TableCreateServcie;

@RestController
@CrossOrigin
@RequestMapping("/table")
public class TableCreatedController {
	private static final Logger log = LoggerFactory.getLogger(TableCreatedController.class);
	private final TableCreateServcie tableCreateServcie;
	
	public TableCreatedController(TableCreateServcie tableCreateServcie) {
		this.tableCreateServcie = tableCreateServcie;
	}
	
	@GetMapping("/created")
	public List<TableCreatedResponse> getListTableCreatedByUser(@RequestParam String typeDatabase) {
		return tableCreateServcie.getTableCreatedsByUserId(typeDatabase);
	}

	@GetMapping("/page")
	@PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
	public ResponseEntity<Page<TableCreatedResponse>> getTableCreatedFilter(Pageable pageable,
																			@RequestParam(required = false,defaultValue = "desc") String direction,
																			@RequestParam(required = false) String keyword,
																			@RequestParam(required = false) String typeDatabaseId) {
		return ResponseEntity.ok(tableCreateServcie.getTableCreatedFilter(pageable,direction,keyword,typeDatabaseId));
	}

	@PutMapping("/update/{id}")
	@PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
	public ResponseEntity<Void> update(@PathVariable String id, @RequestBody TableCreatedRequest request) {
		tableCreateServcie.update(id, request);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
	public ResponseEntity<Void> delete(@PathVariable String id) {
		tableCreateServcie.delete(id);
		return ResponseEntity.ok().build();
	}

}
