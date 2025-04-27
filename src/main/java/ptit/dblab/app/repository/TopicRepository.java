package ptit.dblab.app.repository;

import ptit.dblab.shared.common.repository.BaseRepository;
import ptit.dblab.app.entity.Topic;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TopicRepository extends BaseRepository<Topic> {

    @Modifying
    @Transactional
    @Query("UPDATE Topic t SET t.views = t.views + 1 WHERE t.id = :topicId")
    void incrementViews(String topicId);

    @Query("select count(t) from Topic t where t.views < 10")
    long countTopicNew();
}
