package ptit.dblab.app.controller;

import ptit.dblab.app.dto.response.TypeDatabaseResponse;
import ptit.dblab.app.service.TypeDatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/database")
@CrossOrigin
public class TypeDatabaseController {
    private final TypeDatabaseService typeDatabaseService;

    @GetMapping("")
    public ResponseEntity<List<TypeDatabaseResponse>> getAllTypesDatabse() {
        return ResponseEntity.ok(typeDatabaseService.getAllTypeDatabase());
    }
}
