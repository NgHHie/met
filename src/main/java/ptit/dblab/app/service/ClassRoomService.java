package ptit.dblab.app.service;

import ptit.dblab.shared.common.BaseService;
import ptit.dblab.shared.exception.core.ValidateException;
import ptit.dblab.shared.utils.ContextUtil;
import ptit.dblab.app.dto.request.ClassRoomRequest;
import ptit.dblab.app.dto.request.UserClassRoomRequest;
import ptit.dblab.app.dto.response.ClassRoomBaseResponse;
import ptit.dblab.app.dto.response.ClassRoomDetailResponse;
import ptit.dblab.app.entity.ClassRoom;
import ptit.dblab.app.entity.UserClassRoom;
import ptit.dblab.app.enumerate.ErrorCode;
import ptit.dblab.app.enumerate.Role;
import ptit.dblab.app.mapper.ClassRoomMapper;
import ptit.dblab.app.mapper.UserClassRoomMapper;
import ptit.dblab.app.mapper.UserMapper;
import ptit.dblab.app.repository.ClassRoomRepository;
import ptit.dblab.app.utils.SequenceUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ClassRoomService extends BaseService<ClassRoom, ClassRoomRepository> {

    private final ClassRoomMapper classRoomMapper;
    private final SequenceUtil sequenceUtil;
    private final UserClassRoomMapper userClassRoomMapper;
    private final ContextUtil contextUtil;
    private final UserService userService;

    public ClassRoomService(ClassRoomRepository repository, ClassRoomMapper classRoomMapper, SequenceUtil sequenceUtil, UserMapper userMapper, UserClassRoomMapper userClassRoomMapper, ContextUtil contextUtil, UserService userService) {
        super(repository);
        this.classRoomMapper = classRoomMapper;
        this.sequenceUtil = sequenceUtil;
        this.userClassRoomMapper = userClassRoomMapper;
        this.contextUtil = contextUtil;
        this.userService = userService;
    }

    @Transactional
    public ClassRoomBaseResponse create(ClassRoomRequest request) {
        ClassRoom classRoom = classRoomMapper.toEntity(request);
        classRoom.setClassCode(sequenceUtil.generateClassRoomCode());
        this.save(classRoom);
        return classRoomMapper.toResponse(classRoom);
    }

    @Transactional
    public void update(String classRoomId, ClassRoomRequest request) {
        ClassRoom classRoom = findById(classRoomId);
        if(Objects.isNull(classRoom)) {
            throw new ValidateException(ErrorCode.RESOURCE_NOT_FOUND.getDescription());
        }
        classRoomMapper.updateFromRequest(classRoom,request);
        if(Objects.nonNull(request.getUsers())) {
            List<UserClassRoom> users = classRoom.getUsers();
            List<UserClassRoom> updateUsers = new ArrayList<>();
            for (UserClassRoomRequest userRequest : request.getUsers()) {
                if (Objects.isNull(userRequest.getId())) {
                    UserClassRoom userClassRoom = userClassRoomMapper.toEntity(userRequest);
                    userClassRoom.setClassRoom(classRoom);
                    updateUsers.add(userClassRoom);
                } else {
                    UserClassRoom userClassRoom = getUserClassRoomInList(users, userRequest.getId());
                    if (Objects.nonNull(userClassRoom)) {
                        userClassRoomMapper.updateFromRequest(userClassRoom, userRequest);
                        updateUsers.add(userClassRoom);
                    }
                }
            }
            users.clear();
            users.addAll(updateUsers);
        }

        this.save(classRoom);
    }

    public ClassRoomDetailResponse getClassRoomDetail(String id) {
        return classRoomMapper.toResponseDetail(findById(id));
    }

    private UserClassRoom getUserClassRoomInList(List<UserClassRoom> userClassRooms, String id) {
        return userClassRooms.stream()
                .filter(q -> q.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Page<ClassRoomBaseResponse> getClassRoomByUserId(Pageable pageable,String keyword) {
        Pageable sortedByCreatedAt = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "createdAt")
        );
        String userId = contextUtil.getUser().getRole().equals(Role.ADMIN.name()) ? null : contextUtil.getUserId();
        Page<ClassRoom> classRoomPage = this.repository.getClassRoomByUserId(userId, sortedByCreatedAt,keyword);
        return classRoomPage.map(this::toBaseResponseWithUserCreated);
    }

    private ClassRoomBaseResponse toBaseResponseWithUserCreated(ClassRoom classRoom) {
        ClassRoomBaseResponse response = classRoomMapper.toResponse(classRoom);
        response.setUserCreated(userService.toUserBaseResponse(classRoom.getCreatedBy()));
        return response;
    }

    public List<ClassRoomBaseResponse> getAllClassRooms() {
        return classRoomMapper.toResponseList(this.repository.getAllClassRoomsByUserId(contextUtil.getUserId()));
    }
}
