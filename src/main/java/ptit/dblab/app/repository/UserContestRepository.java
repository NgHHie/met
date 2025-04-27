package ptit.dblab.app.repository;

import ptit.dblab.shared.common.repository.BaseRepository;
import ptit.dblab.app.entity.Contest;
import ptit.dblab.app.entity.UserContest;
import ptit.dblab.app.interfaceProjection.UserContestJoinStatus;
import ptit.dblab.app.interfaceProjection.UserContestProjection;
import ptit.dblab.app.repository.Sql.SqlUserContest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserContestRepository extends BaseRepository<UserContest> {

    @Query("select u from UserContest as u where u.contest.id = :contestId")
    List<UserContest> findByContestId(String contestId);

    @Query("SELECT u.id as Id from UserContest as u where u.contest.id = :contestId")
    List<UserContestProjection> getListByContestId(String contestId);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserContest as u WHERE u.id IN :ids")
    void deleteAllByIds(@Param("ids") List<String> ids);

    @Query(value = SqlUserContest.CHECK_JOIN_CONTEST,nativeQuery = true)
    List<UserContestJoinStatus> getUserContestJoinStatus(String userId, List<String> contestIds);

    @Query("SELECT uc FROM UserContest uc WHERE uc.user.id = :userId AND uc.contest.status = 'OPEN'")
    List<UserContest> findOpenContestsByUser(@Param("userId") String userId);

    @Query("select count(q.id) from UserContest q where q.contest.id = :contestId")
    int getNumberUserOfContest(String contestId);

    @Query("SELECT uc.contest FROM UserContest uc WHERE uc.user.id = :userId AND uc.contest.status <> 'CLOSE' order by uc.timeJoined DESC")
    List<Contest> getListContestByUser(String userId);

    @Query("select uc.contest from UserContest uc where uc.user.id = :userId and uc.contest.status = 'OPEN' and uc.contest.mode = 'EXAM' order by uc.timeJoined desc limit 1")
    Contest getContestExamRunningByUser(String userId);

    @Query("SELECT COUNT(uc) > 0 FROM UserContest uc WHERE uc.user.id = :userId and uc.contest.id = :contestId")
    boolean existsByUserId(@Param("userId") String userId, String contestId);


}
