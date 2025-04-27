package ptit.dblab.app.repository;

import ptit.dblab.shared.common.repository.BaseRepository;
import ptit.dblab.app.entity.UserTracker;
import ptit.dblab.app.interfaceProjection.UserTrackerProjection;
import ptit.dblab.app.repository.Sql.SqlUserTracker;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTrackerRepository extends BaseRepository<UserTracker> {

    @Query(value = SqlUserTracker.GET_LOG_CONTEST_DETAIL_BY_USER,nativeQuery = true)
    List<UserTrackerProjection> getUserTrackerLogDetail(String userId,String contestId);
}
