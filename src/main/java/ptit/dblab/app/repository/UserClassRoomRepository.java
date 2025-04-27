package ptit.dblab.app.repository;

import ptit.dblab.shared.common.repository.BaseRepository;
import ptit.dblab.app.entity.UserClassRoom;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserClassRoomRepository extends BaseRepository<UserClassRoom> {

    @Query("Select u from UserClassRoom u where u.classRoom.id = :classRoomId")
    List<UserClassRoom> findByClassRoomId(String classRoomId);
}
