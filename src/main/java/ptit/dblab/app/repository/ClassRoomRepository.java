package ptit.dblab.app.repository;

import ptit.dblab.shared.common.repository.BaseRepository;
import ptit.dblab.app.entity.ClassRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRoomRepository extends BaseRepository<ClassRoom> {

    @Query("""
            SELECT c
            FROM ClassRoom c
            WHERE (:userId IS NULL OR c.createdBy = :userId)
              AND (:keyword IS NULL OR :keyword = '' OR
                   c.name ILIKE CONCAT('%', :keyword, '%'))
        """)
    Page<ClassRoom> getClassRoomByUserId(String userId, Pageable pageable,String keyword);

    @Query("Select c from ClassRoom c where c.createdBy = :userId")
    List<ClassRoom> getAllClassRoomsByUserId(String userId);

    @Query("Select count(c) from ClassRoom c where (:userId IS NULL OR c.createdBy = :userId)")
    int findTotalClassRoom(String userId);
}
