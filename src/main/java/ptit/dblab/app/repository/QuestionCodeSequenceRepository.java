package ptit.dblab.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ptit.dblab.app.sequenceTable.QuestionCodeSequence;

@Repository
public interface QuestionCodeSequenceRepository extends JpaRepository<QuestionCodeSequence, Long>{

}
