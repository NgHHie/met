package ptit.dblab.app.repository;

import ptit.dblab.shared.common.repository.BaseRepository;
import ptit.dblab.app.entity.QuestionContest;
import ptit.dblab.app.interfaceProjection.QuestionContestProjection;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface QuestionContestRepository extends BaseRepository<QuestionContest> {

    @Query("SELECT qc FROM QuestionContest qc WHERE qc.contest.id = :contestId")
    List<QuestionContest> findByContestId(String contestId);

    @Query("SELECT qc.id as Id from QuestionContest qc where qc.contest.id = :contestId")
    List<QuestionContestProjection> getListByContestId(String contestId);

    @Modifying
    @Transactional
    @Query("DELETE FROM QuestionContest qc WHERE qc.id IN :ids")
    void deleteAllByIds(@Param("ids") List<String> ids);

    @Query("select count(q.id) from QuestionContest q where q.contest.id = :contestId")
    int getNumberQuestionOfContest(String contestId);


}
