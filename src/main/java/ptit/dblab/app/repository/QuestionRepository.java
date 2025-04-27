package ptit.dblab.app.repository;

import java.util.List;

import ptit.dblab.app.entity.Question;
import ptit.dblab.app.interfaceProjection.QuestionLevel;
import ptit.dblab.app.interfaceProjection.UserDoneQuestionProjection;
import ptit.dblab.app.repository.Sql.SqlQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ptit.dblab.shared.common.repository.BaseRepository;

@Repository
public interface QuestionRepository extends BaseRepository<Question> {

	boolean existsByPrefixCode(String prefixCode);

	@Query("""
		SELECT q
		FROM Question q
		WHERE q.enable = true
		  AND q.isSynchorus = true
		  AND (:keyword IS NULL OR :keyword = '' OR 
			   q.title ILIKE CONCAT('%', :keyword, '%') OR 
			   q.questionCode ILIKE CONCAT('%', :keyword, '%'))
		  AND q.isDeleted = false 
		ORDER BY q.createdAt
	""")
	Page<Question> findEnabledQuestions(Pageable pageable, String keyword);


	@Query(value = SqlQuestion.GET_NUMBER_QUESTION_PRACTICE,nativeQuery = true)
	int getNumberOfQuestionPractice();

	@Query(value = SqlQuestion.GET_LIST_QUESTION_BY_USER_ID,nativeQuery = true)
	Page<Question> findAllQuestionsByUserId(Pageable pageable,String userId,String keyword, String level, String typeQuestion, String typeDatabaseId);

	@Query("Select count(q) from Question q where (:userId IS NULL OR q.createdBy = :userId)")
	int findTotalQuestions(String userId);

	@Query(value = SqlQuestion.COUNT_ALL_QUESTION_BY_LEVEL_STAT,nativeQuery = true)
	List<QuestionLevel> getAllQuestionLevelStats();

	@Query(value = SqlQuestion.GET_LIST_USER_SUBMIT_IN_QUESTION,
			countQuery = SqlQuestion.GET_COUNT_USER_SUBMIT_IN_QUESTION,
			nativeQuery = true)
	Page<UserDoneQuestionProjection> findListUserDoneQuestion(Pageable pageable, @Param("questionId") String questionId);

}
