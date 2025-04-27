package ptit.dblab.app.mapper;

import ptit.dblab.shared.common.BaseMapper;
import ptit.dblab.app.dto.request.UserRequest;
import ptit.dblab.app.dto.request.UserUpdateRequest;
import ptit.dblab.app.dto.response.UserBaseResponse;
import ptit.dblab.app.dto.response.UserDetailResponse;
import ptit.dblab.app.dto.response.UserResponse;
import ptit.dblab.app.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper extends BaseMapper<User, UserRequest, UserDetailResponse> {

    @Override
    @Mapping(target = "fullName", expression = "java(concatFullName(entity.getFirstName(), entity.getLastName()))")
    UserDetailResponse toResponse(User entity);

    @Mapping(target = "fullName", expression = "java(concatFullName(entity.getFirstName(), entity.getLastName()))")
    UserResponse toBaseResponse(User entity);

    UserBaseResponse toUserBaseResponse(User entity);

    List<UserResponse> toBaseListResponse(List<User> entities);

    void update(@MappingTarget User user, UserUpdateRequest update);
    default String concatFullName(String firstName, String lastName) {
        return (Objects.requireNonNullElse(firstName, "") + " " + Objects.requireNonNullElse(lastName, "")).trim();
    }
}
