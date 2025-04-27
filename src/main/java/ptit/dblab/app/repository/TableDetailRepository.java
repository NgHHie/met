package ptit.dblab.app.repository;

import ptit.dblab.shared.common.repository.BaseRepository;
import ptit.dblab.app.entity.TableDetail;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TableDetailRepository extends BaseRepository<TableDetail> {
    @Modifying
    @Transactional
    @Query("UPDATE TableDetail td SET td.sequenceNumber = :sequenceNumber WHERE td.questionDetail.id = :questionDetailId AND td.tableCreated.id = :tableCreatedId")
    void updateSequenceNumber(String questionDetailId, String tableCreatedId,int sequenceNumber);
}
