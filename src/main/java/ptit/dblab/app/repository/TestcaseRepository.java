package ptit.dblab.app.repository;

import ptit.dblab.shared.common.repository.BaseRepository;
import ptit.dblab.app.entity.Testcase;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TestcaseRepository extends BaseRepository<Testcase> {

    @Query(value = "Select * from testcase where question_detail_id = :questionDetailId limit 1", nativeQuery = true)
    Testcase findTestcaseSample(String questionDetailId);
}
