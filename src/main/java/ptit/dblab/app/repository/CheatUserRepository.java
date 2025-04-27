package ptit.dblab.app.repository;

import ptit.dblab.shared.common.repository.BaseRepository;
import ptit.dblab.app.entity.CheatUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CheatUserRepository extends BaseRepository<CheatUser> {

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM CheatUser c " +
            "WHERE c.user.id = :userId AND c.contestId = :contestId " +
            "")
    boolean existsByUserIdAndContestId(String userId, String contestId);

    Page<CheatUser> findByContestIdOrderByCreatedAtDesc(Pageable pageable,String contestId);
}
