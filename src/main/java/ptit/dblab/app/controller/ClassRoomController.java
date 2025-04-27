package ptit.dblab.app.controller;

import ptit.dblab.app.dto.request.ClassRoomRequest;
import ptit.dblab.app.dto.response.ClassRoomBaseResponse;
import ptit.dblab.app.dto.response.ClassRoomDetailResponse;
import ptit.dblab.app.dto.response.ServerResponse;
import ptit.dblab.app.dto.response.UserClassResponse;
import ptit.dblab.app.service.ClassRoomService;
import ptit.dblab.app.service.UserClassRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/classroom")
public class ClassRoomController {

    private final ClassRoomService classRoomService;
    private final UserClassRoomService userClassRoomService;

    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ClassRoomBaseResponse> create(@RequestBody ClassRoomRequest request) {
        return ResponseEntity.ok(classRoomService.create(request));
    }

    @PutMapping("/{classRoomId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<Void> update(@RequestBody ClassRoomRequest request,@PathVariable String classRoomId) {
        classRoomService.update(classRoomId,request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{classRoomId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<Void> delete(@PathVariable String classRoomId) {
        classRoomService.hardDelete(classRoomId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sysadmin/{classRoomId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ClassRoomDetailResponse> getClassRoomDetail(@PathVariable String classRoomId) {
        return ResponseEntity.ok(classRoomService.getClassRoomDetail(classRoomId));
    }

    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<Page<ClassRoomBaseResponse>> getClassRooms(Pageable pageable,
                                                                     @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(classRoomService.getClassRoomByUserId(pageable,keyword));
    }

    @PostMapping("/import/add-user")
    public ResponseEntity<ServerResponse> importFileUserClassRoom(@RequestParam("file") MultipartFile file,
                                                                  @RequestParam String classRoomId) {
        return ResponseEntity.ok(userClassRoomService.addUserClassRoom(file,classRoomId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ClassRoomBaseResponse>> getAllClassRoomsByUserId() {
        return ResponseEntity.ok(classRoomService.getAllClassRooms());
    }

    @GetMapping("/user/{classRoomId}")
    public ResponseEntity<List<UserClassResponse>> getListUserByClassRoomId(@PathVariable String classRoomId) {
        return ResponseEntity.ok(userClassRoomService.getUserClassRooms(classRoomId));
    }
}
