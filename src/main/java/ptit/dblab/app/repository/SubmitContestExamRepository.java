package ptit.dblab.app.repository;

import ptit.dblab.shared.common.repository.BaseRepository;
import ptit.dblab.app.entity.SubmitContestExam;
import ptit.dblab.app.interfaceProjection.*;
import ptit.dblab.app.repository.Sql.SqlSubmitContest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ptit.dblab.app.interfaceProjection.*;

import java.util.List;

@Repository
public interface SubmitContestExamRepository extends BaseRepository<SubmitContestExam> {
    @Query("select sc from SubmitContestExam sc where sc.user.id = :userId and sc.questionContest.id = :questionContestId order by sc.timeSubmit desc")
    Page<SubmitContestExam> findSubmitContestByUserIdAndQuestionContestId(String userId, String questionContestId, Pageable pageable);

    @Query("select sc from SubmitContestExam sc where sc.user.id = :userId and sc.questionContest.contest.id= :contestId order by sc.timeSubmit")
    Page<SubmitContestExam> findSubmitContestByUserId(String userId, String contestId, Pageable pageable);

    @Query(value = SqlSubmitContest.COMPETE_QUESTION_CONTEST_BY_USER, nativeQuery = true)
    List<QuestionCompleteProjection> findQuestionContestStatusByUser(
            @Param("userId") String userId,
            @Param("questionIds") String[] questionIds
    );

    @Query(value = SqlSubmitContest.USER_STATSICT_SUBMIT_BY_CONTEST_ID, nativeQuery = true)
    Page<UserSubmitStat> getUserStatisticsSubmitContest(Pageable pageable, String contestId, String keyword);

    @Query(value = SqlSubmitContest.GET_STAT_QUESTION_SUBMIT_CONTEST_BY_USER_ID,nativeQuery = true)
    List<UserQuestionSubmitDetail> getStatQuestionSubmitContestByUserId(String userId,String contestId);

    @Query("Select s from SubmitContestExam s where s.questionContest.contest.id = :contestId order by s.createdAt DESC")
    Page<SubmitContestExam> getSubmitContestHistoryByContest(Pageable pageable, String contestId);

    @Query("Select s from SubmitContestExam s where s.questionContest.contest.id = :contestId order by s.createdAt")
    List<SubmitContestExam> getAllSubmitContestHistoryByContestId(String contestId);

    @Query(value = SqlSubmitContest.GET_STAT_USER_DETAIL_IN_CONTEST, nativeQuery = true)
    UserDetailStatProjection getUserDetailContestStat(@Param("userId") String userId, String contestId);

    @Query(value = SqlSubmitContest.GET_TOTAL_POINT_USER_CONTEST,nativeQuery = true)
    float getTotalPointOfUserInContest(@Param("userId") String userId,String contestId);

    @Query(value = SqlSubmitContest.GET_NUMBER_QUESTION_CONTEST_BY_LEVEL, nativeQuery = true)
    List<QuestionLevel> getQuestionLevels(String userId, String contestId);

    @Query(value = SqlSubmitContest.GET_LIST_QUESTION_CONTEST_USER_SUB,nativeQuery = true)
    Page<QuestionUserSubmit> findListQuestionContestUserSub(String userId, String contestId, Pageable pageable);

    @Query(value = SqlSubmitContest.GET_SUBMIT_CONTEST_RETRY,nativeQuery = true)
    List<SubmitContestProjection> getSubmitErrorOrRetry(int batchSize);

    @Query(value = SqlSubmitContest.QUESTION_CONTEST_STAT,nativeQuery = true)
    Page<QuestionStat> getQuestionContestStats(Pageable pageable,String contestId,String keyword);

    @Query(value = SqlSubmitContest.COUNT_IP_USER,nativeQuery = true)
    UserIpCountProjection getUserIpCount(String contestId, String userId);

    @Query(value = SqlSubmitContest.GET_STAT_RANGE_NUM_QUESTION_AC_CONTEST,nativeQuery = true)
    List<RangeQuestionContestProjection> getRangeQuestionContest(String contestId);

}
