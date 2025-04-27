package ptit.dblab.app.service;

import ptit.dblab.shared.exception.core.ValidateException;
import ptit.dblab.app.dto.response.ServerResponse;
import ptit.dblab.app.dto.response.UserClassResponse;
import ptit.dblab.app.entity.ClassRoom;
import ptit.dblab.app.entity.User;
import ptit.dblab.app.entity.UserClassRoom;
import ptit.dblab.app.enumerate.ErrorCode;
import ptit.dblab.app.interfaceProjection.UserBaseInfoProjection;
import ptit.dblab.app.mapper.UserClassRoomMapper;
import ptit.dblab.app.repository.UserClassRoomRepository;
import ptit.dblab.app.repository.UserRepository;
import ptit.dblab.app.utils.AppUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserClassRoomService {

    private final UserClassRoomRepository userClassRoomRepository;
    private final UserRepository userRepository;
    private final UserClassRoomMapper userClassRoomMapper;

    public ServerResponse addUserClassRoom(MultipartFile file, String classRoomId) {
        List<String> usernames = new ArrayList<>();
        int resultHandleCount = 0;
        int rowEmpty = 0;
        int userNotExistCount = 0;
        String description = "";
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Start from row 1 to skip header row
                Row row = sheet.getRow(i);
                if (row != null) {
                    String studentId = AppUtils.getCellValue(row.getCell(1));
                    if(Objects.nonNull(studentId) && !studentId.isEmpty()) {
                        resultHandleCount++;
                        usernames.add(studentId);
                    } else {
                        rowEmpty++;
                    }
                }
            };
            List<UserBaseInfoProjection> userBaseInfoProjections = userRepository.getListUserBaseInfo(usernames);
            List<UserClassRoom> userClassRooms = new ArrayList<>();
            ClassRoom classRoom = new ClassRoom();
            classRoom.setId(classRoomId);
            log.info("classRoomId: {}", classRoomId);
            log.info("******** userBaseInfoProjections: {}", userBaseInfoProjections.size());
            for(UserBaseInfoProjection userBaseInfoProjection : userBaseInfoProjections) {
                UserClassRoom userClassRoom = new UserClassRoom();
                userClassRoom.setClassRoom(classRoom);
                userClassRoom.setUser(User.builder().id(userBaseInfoProjection.getUserId()).build());
                log.info("UserClassRoom id:{}", userClassRoom.getId());
                userClassRooms.add(userClassRoom);
            }
            userClassRoomRepository.saveAll(userClassRooms);
            description = "Đọc "+resultHandleCount+ " user từ file. Đã thêm " + userBaseInfoProjections.size()+" user vào lớp học thành công!";
            if(rowEmpty > 0) {
                description += " Có "+rowEmpty +" ô không có thông tin";
            }
            userNotExistCount = resultHandleCount - userBaseInfoProjections.size();
            if(userNotExistCount > 0) {
                description = "Có "+ userNotExistCount +" user chưa có tài khoản trong hệ thống";
            }
            log.info(description);
            return ServerResponse.builder()
                    .status(userNotExistCount > 0 ? ErrorCode.USER_NOT_EXIST.getCode() : ErrorCode.SUCCESS.getCode())
                    .description(description)
                    .build();
        } catch (Exception e) {
            throw new ValidateException("Không thể đọc file xin vui lòng kiểm tra lại!");
        }
    }

    public List<UserClassResponse> getUserClassRooms(String classRoomId) {
        return userClassRoomMapper.toResponseList(userClassRoomRepository.findByClassRoomId(classRoomId));
    }
}
