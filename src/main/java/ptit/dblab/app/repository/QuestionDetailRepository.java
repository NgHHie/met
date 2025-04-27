package ptit.dblab.app.repository;

import ptit.dblab.shared.common.repository.BaseRepository;
import ptit.dblab.app.entity.QuestionDetail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionDetailRepository extends BaseRepository<QuestionDetail> {

    @Query(value = "SELECT qd.*, q.type FROM question_detail qd JOIN question q ON qd.question_id = q.id WHERE qd.question_id = :questionId AND qd.type_database_id = :typeDatabaseId",nativeQuery = true)
    QuestionDetail findByQuestionIdAndTypeDatabaseId(String questionId, String typeDatabaseId);

    @Query("SELECT q FROM QuestionDetail q " +
            "LEFT JOIN FETCH q.tableUses t " +
            "LEFT JOIN FETCH t.tableCreated " +
            "WHERE q.question.id = :questionId and q.typeDatabase.id =:typeDatabaseId")
    QuestionDetail fetchQuestionDetailByQuestionIdAndDatabaseId(String questionId, String typeDatabaseId);
}