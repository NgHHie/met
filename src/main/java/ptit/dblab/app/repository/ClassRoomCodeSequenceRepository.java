package ptit.dblab.app.repository;

import ptit.dblab.app.sequenceTable.ClassRoomSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRoomCodeSequenceRepository extends JpaRepository<ClassRoomSequence,Long> {
}
