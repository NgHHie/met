package ptit.dblab.app.repository;

import ptit.dblab.app.interfaceProjection.UserBaseInfoProjection;
import ptit.dblab.app.interfaceProjection.UserDetailCustomInf;
import ptit.dblab.shared.common.repository.BaseRepository;
import ptit.dblab.app.entity.User;
import ptit.dblab.app.repository.Sql.SqlUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends BaseRepository<User> {

    @Query(value = "SELECT u.id, u.username as username, u.password, u.role, u.user_code, u.user_prefix,u.is_premium FROM user_entity u WHERE u.username = :username",nativeQuery = true)
    UserDetailCustomInf findByUsername(String username);

    @Query(value = "Select u from User u where u.username = :username")
    User findUserByUsername(String username);

    boolean existsByUserPrefix(String prefixCode);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUserCode(String userCode);

    @Query(value = SqlUser.QUERY_GET_LIST_USER,nativeQuery = true)
    Page<User> getListUser(Pageable pageable, String keyword, String role);

    @Query("""
            SELECT u
            FROM User u
            WHERE u.role != 'ADMIN'
              AND u.createdBy = :userId
              AND (:keyword IS NULL OR
                   LOWER(u.firstName) LIKE CONCAT('%', LOWER(:keyword), '%') OR
                   LOWER(u.lastName) LIKE CONCAT('%', LOWER(:keyword), '%') OR
                   LOWER(u.username) LIKE CONCAT('%', LOWER(:keyword), '%'))
            """)
    Page<User> getListUserByCreatedBy(Pageable pageable, String userId, String keyword);


    @Query(value = SqlUser.QUERY_SEARCH_USER,nativeQuery = true)
    List<User> searchUser(String keyword);


    @Query(value = "Select u.id as userId from user_entity as u where u.username IN (:usernames)",nativeQuery = true)
    List<UserBaseInfoProjection> getListUserBaseInfo(List<String> usernames);

    @Query("Select count(u) from User u where (:userId IS NULL OR u.createdBy = :userId)")
    int findTotalUser(String userId);
}
