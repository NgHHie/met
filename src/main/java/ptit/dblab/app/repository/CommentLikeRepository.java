package ptit.dblab.app.repository;

import ptit.dblab.shared.common.repository.BaseRepository;
import ptit.dblab.app.entity.CommentLike;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CommentLikeRepository extends BaseRepository<CommentLike> {

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CommentLike c WHERE c.comment.id = :commentId AND c.user.id = :userId")
    boolean existsByCommentIdAndUserId(String commentId, String userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CommentLike c WHERE c.comment.id = :commentId AND c.user.id = :userId")
    int deleteByCommentIdAndUserId(@Param("commentId") String commentId, @Param("userId") String userId);
}
