package ptit.dblab.app.repository;

import ptit.dblab.app.sequenceTable.UserCodeSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCodeSequenceRepository extends JpaRepository<UserCodeSequence, Long> {
}
