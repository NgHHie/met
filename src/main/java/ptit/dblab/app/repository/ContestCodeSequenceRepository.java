package ptit.dblab.app.repository;

import ptit.dblab.app.sequenceTable.ContestCodeSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContestCodeSequenceRepository extends JpaRepository<ContestCodeSequence,Long> {
}
