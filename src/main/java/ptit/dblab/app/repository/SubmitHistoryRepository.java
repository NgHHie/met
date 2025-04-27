package ptit.dblab.app.repository;

import ptit.dblab.shared.common.repository.BaseRepository;
import ptit.dblab.app.entity.SubmitHistory;
import ptit.dblab.app.interfaceProjection.*;
import ptit.dblab.app.repository.Sql.SqlQuestion;
import ptit.dblab.app.repository.Sql.SqlSubmitHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ptit.dblab.app.interfaceProjection.*;

import java.util.List;

@Repository
public interface SubmitHistoryRepository extends BaseRepository<SubmitHistory> {

    @Query(value = SqlSubmitHistory.GET_SUBMIT_HIS_BY_USER_AND_QUESTION, nativeQuery = true)
    Page<SubmitHistory> findByUserIdAndQuestionId(@Param("userId") String userId, @Param("questionId") String questionId, Pageable pageable);

    @Query(value = SqlSubmitHistory.GET_SUBMIT_HIS_BY_USER, nativeQuery = true)
    Page<SubmitHistory> findByUserId(@Param("userId") String userId, Pageable pageable);

    Page<SubmitHistory> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query(value = SqlSubmitHistory.COMPETE_QUESTION_BY_USER, nativeQuery = true)
    List<QuestionCompleteProjection> findQuestionStatusByUser(
            @Param("userId") String userId,
            @Param("questionIds") String[] questionIds
    );

    @Query(value = SqlSubmitHistory.STAT_USER_SUBMIT, nativeQuery = true)
    Page<UserSubmitStat> getUserStatistics(Pageable pageable, String classRoomId, String keyword);

    @Query(value = SqlSubmitHistory.USER_DETAIL_STAT_SUBMIT, nativeQuery = true)
    UserDetailStatProjection getUserDetailStat(@Param("userId") String userId);

    @Query(value = SqlSubmitHistory.GET_TOTAL_POINT_USER,nativeQuery = true)
    float getTotalPointOfUser(@Param("userId") String userId);

    @Query(value = SqlSubmitHistory.GET_NUMBER_USER_JOIN_PRACTICE,nativeQuery = true)
    int getNumberUserJoinSubmitPractice();

    @Query(value = SqlSubmitHistory.GET_QUESTION_USER_SUB,nativeQuery = true)
    Page<QuestionUserSubmit> findListQuestionUserSub(String userId, Pageable pageable);

    @Query(value = SqlSubmitHistory.TOP_USER_SUBMIT_PRACTICE, nativeQuery = true)
    Page<TopUserProjection> getTopUserPractice(Pageable pageable);

    @Query(value = SqlQuestion.NUMBER_QUESTION_BY_LEVEL, nativeQuery = true)
    List<QuestionLevel> getQuestionLevels(String userId);

    @Query(value = SqlQuestion.QUESTION_STAT,nativeQuery = true)
    Page<QuestionStat> getQuestionStats(Pageable pageable,String keyword);

    @Query(value = SqlQuestion.QUESTION_SUB_INFO,nativeQuery = true)
    QuestionStat getQuestionSubInfo(String questionId);

    @Query(value = SqlSubmitHistory.GET_USER_JOINED,nativeQuery = true)
    Page<UserJoined> getUserJoined(Pageable pageable,String keyword);

    @Query(value = SqlSubmitHistory.GET_USER_SUB_COUNT,nativeQuery = true)
    List<UserSubCount> getUserSubCount(String userId,String intervalType );

    @Query(value = SqlSubmitHistory.GET_TOTAL_SUBMIT_BY_INTERVAL,nativeQuery = true)
    List<TotalSubmitForDay> getTotalSubmitByInterval(String intervalType);

    @Query(value = SqlSubmitHistory.GET_SUBMIT_PRACTICE_RETRY,nativeQuery = true)
    List<SubmitPracticeProjection> getSubmitErrorOrRetry(int batchSize);
}
