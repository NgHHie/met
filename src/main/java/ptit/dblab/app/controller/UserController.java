package ptit.dblab.app.controller;

import ptit.dblab.app.dto.request.AuthRequest;
import ptit.dblab.app.dto.request.RefreshTokenRequest;
import ptit.dblab.app.dto.request.UserRequest;
import ptit.dblab.app.dto.request.UserUpdateRequest;
import ptit.dblab.app.dto.response.AuthResponse;
import ptit.dblab.app.dto.response.ServerResponse;
import ptit.dblab.app.dto.response.UserDetailResponse;
import ptit.dblab.app.dto.response.UserResponse;
import ptit.dblab.app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<Page<UserResponse>> getListUser(Pageable pageable,
                                                          @RequestParam(required = false) String keyword,
                                                          @RequestParam(required = false) String role) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.desc("created_at")));

        return ResponseEntity.ok(userService.getListUsers(pageable,keyword,role));
    };

    @PostMapping("/auth/register")
    public ResponseEntity<ServerResponse> register(@RequestBody @Valid UserRequest request) {
        return ResponseEntity.ok(userService.create(request,null));
    }

    @PostMapping("/create-account")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ServerResponse> createAccount(@RequestBody @Valid UserRequest request) {
        return ResponseEntity.ok(userService.createOrUpdateUserSystem(request));
    }


    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/auth/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        return ResponseEntity.ok(userService.refreshToken(request));
    }

    @GetMapping("/info")
    public ResponseEntity<UserDetailResponse> getUserInfo() throws Exception {
        return ResponseEntity.ok(userService.getUserDetail(null));
    }

    @GetMapping("/info-by-id/{userId}")
    public ResponseEntity<UserDetailResponse> getUserInfoByUd(@PathVariable String userId) throws Exception {
        return ResponseEntity.ok(userService.getUserDetail(userId));
    }

    @PostMapping("/create/multi-account")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    @CrossOrigin(
            exposedHeaders = "Content-Disposition"
    )
    public ResponseEntity<ByteArrayResource> createMultiAccount(@RequestParam("file") MultipartFile file) throws IOException {
        return userService.createMulAccount(file);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUser(@RequestParam(value = "keyword") String keyword) {
        return ResponseEntity.ok(userService.searchUsers(keyword));
    }

    @PostMapping("/update")
    public ResponseEntity<Void> update( @RequestBody @Valid UserUpdateRequest request) {
        userService.update( request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable String userId) {
        userService.hardDelete(userId);
        return ResponseEntity.noContent().build();
    }
}
