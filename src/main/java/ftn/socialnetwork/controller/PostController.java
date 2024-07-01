package ftn.socialnetwork.controller;

import ftn.socialnetwork.model.dto.DocumentFileDTO;
import ftn.socialnetwork.model.entity.*;
import ftn.socialnetwork.service.IndexingService;
import ftn.socialnetwork.service.implementation.CommentService;
import ftn.socialnetwork.service.implementation.PostService;
import ftn.socialnetwork.service.implementation.ReactionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/post")
@AllArgsConstructor
public class PostController {

    private final PostService postService;
    private final ReactionService reactionService;
    private final CommentService commentService;
    private final IndexingService indexingService;


    @PostMapping("/add")
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        post.setCreationDate(LocalDateTime.now());
        Post addedPost = postService.save(post);
        return new ResponseEntity<>(addedPost, HttpStatus.CREATED);
    }

    @PostMapping("/add_file/{id}")
    public ResponseEntity<Post> addFile(@PathVariable Long id, @ModelAttribute DocumentFileDTO documentFile) {
        Post post = postService.getPost(id);
        File file = indexingService.indexDocument(documentFile.file(), "post", id);
        post.setFile(file);
        file.setPost(post);
        postService.save(post);
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Post> updatePost(@RequestBody Post post) {
        Post updatedPost = postService.save(post);
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePost (@PathVariable("id") Long id) {
        postService.deletePost(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        Post post = postService.getPost(id);
        List<Comment> comments = commentService.getCommentByPost(id);
        List<Comment> commentsWithChildren = populateChildComments(comments);
        post.setComments(commentsWithChildren);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    // Helper method to populate child comments recursively
    private List<Comment> populateChildComments(List<Comment> comments) {
        List<Comment> commentsWithChildren = new ArrayList<>();
        for (Comment comment : comments) {
            List<Comment> childComments = commentService.getChildComments(comment.getId());
            comment.setChildComments(populateChildComments(childComments));
            commentsWithChildren.add(comment);
        }
        return commentsWithChildren;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Post>> getAllPosts(){
        List<Post> posts = postService.getAllPosts();
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/by-group/{id}")
    public ResponseEntity<List<Post>> getPostsByGroup(@PathVariable("id") Long id) {
        List<Post> posts = postService.getPostsByGroup(id);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @PostMapping("/add_reaction/{postId}")
    public ResponseEntity<Reaction> addReactionToPost(@PathVariable("postId") Long postId, @RequestBody Reaction reaction) {
        Post post = postService.getPost(postId);

//        boolean userHasReacted = post.getReactions().stream()
//                .anyMatch(r -> r.getUserId().equals(reaction.getUserId()));

        Long reactedId = null;

        for (Reaction r :  post.getReactions()) {
            if (r.getUserId().equals(reaction.getUserId()) &&
                    r.getReactionType().equals(reaction.getReactionType())){
                reactedId = r.getId();
            }
        }

        if (reactedId != null) {
            System.out.println(reactedId);

            reactionService.deleteReaction(reaction);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            reaction.setPost(post);
            Reaction addedReaction = reactionService.save(reaction);
            return new ResponseEntity<>(addedReaction, HttpStatus.CREATED);
        }

    }

    @GetMapping("/{postId}/reactions")
    public ResponseEntity<List<Reaction>> getPostReactions(@PathVariable("postId") Long postId) {
        List<Reaction> reactions = reactionService.getReactionsByPost(postId);
        return new ResponseEntity<>(reactions, HttpStatus.OK);
    }

    @PostMapping("/add_comment/{postId}")
    public ResponseEntity<Comment> addCommentToPost(@PathVariable("postId") Long postId, @RequestBody Comment comment) {
        Post post = postService.getPost(postId);
        comment.setPost(post);
        comment.setTimestamp(LocalDateTime.now());
        Comment addedComment = commentService.save(comment);
        return new ResponseEntity<>(addedComment, HttpStatus.CREATED);
    }

    @PostMapping("/add_child_comment/{parentCommentId}")
    public ResponseEntity<Comment> addChildComment(@PathVariable("parentCommentId") Long parentCommentId, @RequestBody Comment childComment) {
        Comment parentComment = commentService.get(parentCommentId);
        childComment.setParentComment(parentComment);
        childComment.setTimestamp(LocalDateTime.now());
        Comment addedChildComment = commentService.save(childComment);
        return new ResponseEntity<>(addedChildComment, HttpStatus.CREATED);
    }


    @PostMapping("/add_comment_reaction/{commentId}")
    public ResponseEntity<Reaction> addReactionToComment(@PathVariable("commentId") Long commentId, @RequestBody Reaction reaction) {
        Comment comment = commentService.get(commentId);

        Long reactedId = null;

        for (Reaction r :  comment.getReactions()) {
            if (r.getUserId().equals(reaction.getUserId()) &&
                    r.getReactionType().equals(reaction.getReactionType())){
                reactedId = r.getId();
            }
        }

        if (reactedId != null) {
            System.out.println(reactedId);

            reactionService.deleteReaction(reaction);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            reaction.setComment(comment);
            Reaction addedReaction = reactionService.save(reaction);
            return new ResponseEntity<>(addedReaction, HttpStatus.CREATED);
        }

    }


}
