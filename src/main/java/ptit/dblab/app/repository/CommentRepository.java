package ptit.dblab.app.repository;

import ptit.dblab.shared.common.repository.BaseRepository;
import ptit.dblab.app.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CommentRepository extends BaseRepository<Comment> {

    @Query("SELECT c FROM Comment c WHERE c.parentId = :parentId " +
            "ORDER BY (CASE WHEN c.countLike > 100 THEN 0 ELSE 1 END), c.createdAt ASC")
    Page<Comment> findCommentsByParentId(String parentId, Pageable pageable);


    @Modifying
    @Transactional
    @Query("UPDATE Comment c SET c.countLike = c.countLike + 1 WHERE c.id = :commentId")
    void incrementLikeCount(@Param("commentId") String commentId);

    @Modifying
    @Transactional
    @Query("UPDATE Comment c SET c.countLike = c.countLike - 1 WHERE c.id = :commentId AND c.countLike > 0")
    void decrementLikeCount(@Param("commentId") String commentId);
}
