package ftn.socialnetwork.service.implementation;

import ftn.socialnetwork.model.entity.Comment;
import ftn.socialnetwork.model.entity.Post;
import ftn.socialnetwork.model.entity.Reaction;
import ftn.socialnetwork.repository.CommentRepository;
import ftn.socialnetwork.repository.PostRepository;
import ftn.socialnetwork.repository.ReactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }

    public Comment get(Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public List<Comment> getCommentByPost(Long id) {
        Post post = postRepository.findById(id).get();
        return post.getComments();
    }

    public List<Comment> getChildComments(Long id) {
        Comment comment = commentRepository.findById(id).get();
        return comment.getChildComments();
    }
}
