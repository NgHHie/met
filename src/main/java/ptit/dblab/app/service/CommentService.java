package ptit.dblab.app.service;

import ptit.dblab.shared.common.BaseService;
import ptit.dblab.shared.exception.core.ValidateException;
import ptit.dblab.shared.utils.ContextUtil;
import ptit.dblab.app.dto.request.CommentLikeRequest;
import ptit.dblab.app.dto.request.CommentRequest;
import ptit.dblab.app.dto.response.CommentResponse;
import ptit.dblab.app.entity.Comment;
import ptit.dblab.app.entity.CommentLike;
import ptit.dblab.app.entity.User;
import ptit.dblab.app.enumerate.ErrorCode;
import ptit.dblab.app.mapper.CommentMapper;
import ptit.dblab.app.repository.CommentLikeRepository;
import ptit.dblab.app.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CommentService extends BaseService<Comment, CommentRepository> {

    private final CommentMapper commentMapper;
    private final ContextUtil contextUtil;
    private final CommentLikeRepository commentLikeRepository;

    public CommentService(CommentRepository repository, CommentMapper commentMapper, ContextUtil contextUtil, CommentLikeRepository commentLikeRepository) {
        super(repository);
        this.commentMapper = commentMapper;
        this.contextUtil = contextUtil;
        this.commentLikeRepository = commentLikeRepository;
    }

    public CommentResponse create(CommentRequest request) {
        if(Objects.isNull(request.getUser().getId())) {
            throw new ValidateException(ErrorCode.USER_NOT_LOGIN.getDescription());
        }
        Comment comment = commentMapper.toEntity(request);
        repository.save(comment);
        return commentMapper.toResponse(comment);
    }

    public Page<CommentResponse> findCommentsByParent(String parentId,Pageable pageable) {
        Page<Comment> comments = repository.findCommentsByParentId(parentId,pageable);

        return comments.map(this::convertToResponse);
    }

    public void updateCountLike(CommentLikeRequest request) {
        if(commentLikeRepository.existsByCommentIdAndUserId(request.getCommentId(),contextUtil.getUserId())) {
            commentLikeRepository.deleteByCommentIdAndUserId(request.getCommentId(),contextUtil.getUserId());
            this.repository.decrementLikeCount(request.getCommentId());
        } else {
            CommentLike commentLike = new CommentLike();
            commentLike.setUser(User.builder().id(contextUtil.getUserId()).build());
            commentLike.setComment(Comment.builder().id(request.getCommentId()).build());
            commentLikeRepository.save(commentLike);
            this.repository.incrementLikeCount(request.getCommentId());
        }
    }

    private CommentResponse convertToResponse(Comment comment) {
        CommentResponse commentResponse = commentMapper.toResponse(comment);
        commentResponse.setUserLike(commentLikeRepository.existsByCommentIdAndUserId(comment.getId(),contextUtil.getUserId()));
        return commentResponse;
    }
}
