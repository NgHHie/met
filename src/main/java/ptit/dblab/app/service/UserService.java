package ptit.dblab.app.service;

import ptit.dblab.shared.enumerate.TypeToken;
import ptit.dblab.shared.exception.core.ValidateException;
import ptit.dblab.shared.securityConfig.UserDetailCustom;
import ptit.dblab.shared.utils.ContextUtil;
import ptit.dblab.shared.utils.JwtUtil;
import ptit.dblab.shared.common.BaseService;
import ptit.dblab.app.dto.request.*;
import ptit.dblab.app.dto.response.*;
import ptit.dblab.app.dto.request.UserUpdateRequest;
import ptit.dblab.app.entity.User;
import ptit.dblab.app.enumerate.ErrorCode;
import ptit.dblab.app.enumerate.Role;
import ptit.dblab.app.mapper.UserMapper;
import ptit.dblab.app.repository.UserRepository;
import ptit.dblab.app.securityConfig.UserDetailServiceCustom;
import ptit.dblab.app.utils.AppUtils;
import ptit.dblab.app.utils.SequenceUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ptit.dblab.app.dto.request.AuthRequest;
import ptit.dblab.app.dto.request.RefreshTokenRequest;
import ptit.dblab.app.dto.request.UserRequest;
import ptit.dblab.app.dto.response.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserService extends BaseService<User, UserRepository> {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserMapper userMapper;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    private final UserDetailServiceCustom customUserDetailsService;

    private final SequenceUtil sequenceUtil;

    private final PasswordEncoder passwordEncoder;

    private final ContextUtil contextUtil;

    @Value("${account.path-save}")
    private String excelSavePath;

    public UserService(UserRepository repository
    , UserMapper userMapper, AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserDetailServiceCustom customUserDetailsService, SequenceUtil sequenceUtil, PasswordEncoder passwordEncoder, ContextUtil contextUtil) {
        super(repository);
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
        this.sequenceUtil = sequenceUtil;
        this.passwordEncoder = passwordEncoder;
        this.contextUtil = contextUtil;
    }

    public ServerResponse create(UserRequest request, String roleName) {
        if(request.getUsername().matches(".*\\s+.*")) {
            throw new ValidateException("Username không được chứa dấu cách");
        }
        if(Objects.isNull(roleName)) {
            if(Objects.isNull(request.getPassword())) {
                throw new ValidateException(ErrorCode.PARAM_MUST_BE_REQUIRED.getDescription(),"password");
            }
        }
        User user = userMapper.toEntity(request);
        if (this.repository.existsByUsername(user.getUsername()) || Objects.nonNull(request.getUserCode()) && this.repository.existsByUserCode(request.getUserCode())) {
            return new ServerResponse(ErrorCode.USER_ALREADY_EXISTS.getCode(), ErrorCode.USER_ALREADY_EXISTS.getDescription());
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty() && this.repository.existsByEmail(user.getEmail())) {
            return new ServerResponse(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if(Objects.isNull(request.getUserCode())) {
            user.setUserCode(sequenceUtil.generateUserCode());
        }

        user.setUserPrefix(sequenceUtil.generateUserPrefix());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsPremium(false);
        if(Objects.isNull(roleName) || !roleName.equals(Role.ADMIN.name())) {
            user.setRole(Role.STUDENT);
        }
        if(Objects.isNull(request.getRole())) {
            user.setRole(Role.STUDENT);
        }
        this.save(user);
        log.info("**** create user {} success", user.getUsername());
        return new ServerResponse(ErrorCode.SUCCESS.getCode(), user.getId());
    }

    public ServerResponse createOrUpdateUserSystem(UserRequest request) {
        ServerResponse response = create(request,contextUtil.getUser().getRole());
        if(response.getStatus() == ErrorCode.USER_ALREADY_EXISTS.getCode()) {
            User user = this.repository.findUserByUsername(request.getUsername());
            log.info("=== birthDay: {} \nAvatar: {}",request.getBirthDay(),request.getAvatar());
            userMapper.updateFromRequest(user,request);
            if(Objects.nonNull(request.getPassword())) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            this.save(user);
        }
        return new ServerResponse(ErrorCode.SUCCESS.getCode(), "");
    }

    public ErrorCode createByFile(UserRequest request) {
        ServerResponse response = create(request,null);
        if(response.getStatus() == ErrorCode.USER_ALREADY_EXISTS.getCode()) {
            User user = this.repository.findUserByUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(user.getUsername()));
            this.save(user);
            log.info("====== user exist: {}",request.getUsername());
            return ErrorCode.USER_ALREADY_EXISTS;
        }
        log.info("create user {} success", request.getUsername());
        return ErrorCode.SUCCESS;
    }

    public AuthResponse login(AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailCustom userDetails = (UserDetailCustom) customUserDetailsService.loadUserByUsername(authRequest.getUsername());
            final String accessToken = jwtUtil.generateAccessToken(userDetails);
            final String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            return new AuthResponse(ErrorCode.SUCCESS.getCode(),accessToken, refreshToken);
        } catch (BadCredentialsException e) {
          throw new BadCredentialsException("Invalid username or password");
        } catch (Exception ex) {
            ex.printStackTrace();
           throw ex;
        }
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        if(jwtUtil.validateToken(request.getRefreshToken(), TypeToken.REFRESH_TOKEN)) {
            String username = jwtUtil.extractUsername(request.getRefreshToken());
            UserDetailCustom userDetails = (UserDetailCustom) customUserDetailsService.loadUserByUsername(username);
            final String accessToken = jwtUtil.generateAccessToken(userDetails);
            final String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            return new AuthResponse(ErrorCode.SUCCESS.getCode(),accessToken, refreshToken);
        }
        throw new ValidateException("Invalid refresh token");
    }

    public UserDetailResponse getUserDetail(String userId) throws Exception {
        return userMapper.toResponse(this.findById(Objects.isNull(userId) ? contextUtil.getUser().getId() : userId));
    }

    public ResponseEntity<ByteArrayResource> createMulAccount(MultipartFile file) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        // Create a new Excel workbook and sheet
        Workbook newWorkbook = new XSSFWorkbook();
        String originalFileName = file.getOriginalFilename();
        String sheetName = originalFileName != null ? originalFileName.replaceAll("[\\\\/*\\[\\]:?]", "").substring(0, Math.min(31, originalFileName.length())) : "Accounts";
        sheetName = sheetName.contains(".")
                ? sheetName.substring(0, originalFileName.lastIndexOf('.'))
                : sheetName;
        Sheet newSheet = newWorkbook.createSheet(sheetName);
        log.info("======== file name: {}",sheetName);
        // Create header row in new file (with only 4 columns: Mã SV, Họ lót, Tên, Password)
        Row headerRow = newSheet.createRow(0);
        headerRow.createCell(0).setCellValue("STT");
        headerRow.createCell(1).setCellValue("Mã SV");
        headerRow.createCell(2).setCellValue("Họ lót");
        headerRow.createCell(3).setCellValue("Tên");
        headerRow.createCell(4).setCellValue("Password");
        headerRow.createCell(5).setCellValue("Trạng thái");

        // Iterate through each row
        int newRowNum = 1;
        int index = 1;
        for (Row row : sheet) {
            try {
                if (row.getRowNum() == 0) {
                    continue; // Skip header row
                }
                String username = AppUtils.getCellValue(row.getCell(1)); // Username from column B
                String lastName = AppUtils.getCellValue(row.getCell(2)); // Last Name from column C
                String firstName = AppUtils.getCellValue(row.getCell(3)); // First Name from column D
                String email = AppUtils.getCellValue(row.getCell(7));

                if (username.isEmpty() || lastName.isEmpty() || firstName.isEmpty() || username.matches(".*\\s+.*")) {
                    continue; // Skip rows with missing data
                }

                UserRequest userRequest = new UserRequest();
                userRequest.setUsername(username);
                userRequest.setPassword(username);//set password username
                userRequest.setFirstName(firstName);
                userRequest.setLastName(lastName);
                userRequest.setUserCode(username);
                if(Objects.nonNull(email)) {
                    userRequest.setEmail(email);
                }
                ErrorCode response = createByFile(userRequest); // Create account logic

                if(response == ErrorCode.SUCCESS) {
                    Row newRow = newSheet.createRow(newRowNum++);
                    newRow.createCell(0).setCellValue(index);
                    newRow.createCell(1).setCellValue(username);     // Mã SV
                    newRow.createCell(2).setCellValue(lastName);    // Họ lót
                    newRow.createCell(3).setCellValue(firstName);   // Tên
                    newRow.createCell(4).setCellValue(username);
                    newRow.createCell(5).setCellValue("Thành công");// Password
                } else {
                    Row newRow = newSheet.createRow(newRowNum++);
                    newRow.createCell(0).setCellValue(index);
                    newRow.createCell(1).setCellValue(username);     // Mã SV
                    newRow.createCell(2).setCellValue(lastName);    // Họ lót
                    newRow.createCell(3).setCellValue(firstName);   // Tên
                    newRow.createCell(4).setCellValue(username);
                    newRow.createCell(5).setCellValue("User is exist!");// Password
                }
                index++;
            } catch(Exception e) {
                log.error("cannot create account {}", e.getMessage());
            }
        }

        // Write the Excel file to a ByteArrayOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        newWorkbook.write(baos);
        newWorkbook.close();
        workbook.close();

        // Convert ByteArrayOutputStream to ByteArrayResource
        ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());

        // Set response headers for download
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + sheetName+".xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

    public Page<UserResponse> getListUsers(Pageable pageable, String keyword, String role) {
        Page<User> userPage;
        if(contextUtil.getUser().getRole().equals(Role.ADMIN.name())) {
            userPage = this.repository.getListUser(pageable,keyword,role);
        } else {
            userPage = this.repository.getListUserByCreatedBy(pageable,contextUtil.getUserId(),keyword);
        }
        return userPage.map(userMapper::toBaseResponse);
    }

    public List<UserResponse> searchUsers(String keyword) {
        if(keyword.isEmpty()) return new ArrayList<>();
        return userMapper.toBaseListResponse(this.repository.searchUser(keyword));
    }

    public void update(UserUpdateRequest userRequest) {
        User user = findById(contextUtil.getUserId());

        userMapper.update(user,userRequest);
        if(userRequest.getPassword() != null) {
            if(userRequest.getRepassword() == null || userRequest.getRepassword().isEmpty()) {
                throw new ValidateException("Vui lòng nhập lại mật khẩu");
            }
            if(!userRequest.getPassword().equals(userRequest.getRepassword())) {
                throw new ValidateException("Mật khẩu nhập lại không khớp");
            }
            user.setPassword(passwordEncoder.encode(userRequest.getRepassword()));

        }
        this.save(user);
    }

    public UserBaseResponse toUserBaseResponse(String id) {
        try {
            return userMapper.toUserBaseResponse(findById(id));
        } catch (Exception e) {
            return null;
        }
    }
}
