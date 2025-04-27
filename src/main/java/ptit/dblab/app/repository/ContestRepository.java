package ptit.dblab.app.repository;

import ptit.dblab.shared.common.repository.BaseRepository;
import ptit.dblab.app.entity.Contest;
import ptit.dblab.app.interfaceProjection.UserDoneQuestionProjection;
import ptit.dblab.app.repository.Sql.SqlContest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContestRepository extends BaseRepository<Contest> {
    @Query(value = SqlContest.FIND_LIST_CONTEST_BY_USER_ID,nativeQuery = true)
    Page<Contest> findAllContestByUserId(Pageable pageable,String userId,String status, String keyword);

    @Query("""
    SELECT c
    FROM Contest c
    WHERE c.isPublic = true
    ORDER BY
        CASE
            WHEN c.status = 'OPEN' AND c.mode = ptit.dblab.app.enumerate.ModeContest.EXAM THEN 1
            WHEN c.status = 'OPEN' THEN 2
            WHEN c.status = 'SCHEDULED' THEN 3
            WHEN c.status = 'CLOSE' THEN 4
            ELSE 5
        END,
        c.createdAt DESC
    """)
    Page<Contest> findAllContestPublished(Pageable pageable);

    @Query("SELECT c FROM Contest c WHERE  c.startDay <= :today AND c.endDay >= :today")
    List<Contest> findListContestToday(LocalDate today);

    @Query(value = SqlContest.GET_CONTEST_ID_BY_QUESTION_CONTEST,nativeQuery = true)
    ContestIdProjection getContestIdByQuestionContest(String questionContestId);

    @Query("Select count(c) from Contest c where (:userId IS NULL OR c.createdBy = :userId)")
    int findTotalContest(String userId);

    @Query("Select count(c) from Contest c where c.status = 'OPEN' and c.isPublic = true")
    int findTotalContestOpen();

    @Query("SELECT COUNT(DISTINCT c.id) " +
            "FROM Contest c " +
            "JOIN UserContest uc ON c.id = uc.contest.id " +
            "WHERE uc.user.id = :userId " +
            "  AND c.status <> 'CLOSE'")
    int findTotalContestUserJoinedAndNotClose(String userId);

    @Query("SELECT COUNT(c) > 0 FROM Contest c WHERE c.id = :contestId AND c.status = 'OPEN'")
    boolean isContestOpen(String contestId);

    @Query(value = SqlContest.GET_LIST_USER_DONE_QUESTION_CONTEST,
            countQuery = SqlContest.GET_COUNT_USER_SUBMIT_QUESTION_CONTEST,
            nativeQuery = true)
    Page<UserDoneQuestionProjection> findListUserDoneQuestionContest(Pageable pageable, @Param("questionId") String questionId);

}
