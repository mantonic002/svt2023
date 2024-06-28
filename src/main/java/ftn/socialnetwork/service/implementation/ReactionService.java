package ftn.socialnetwork.service.implementation;

import ftn.socialnetwork.model.entity.Group;
import ftn.socialnetwork.model.entity.Post;
import ftn.socialnetwork.model.entity.Reaction;
import ftn.socialnetwork.repository.GroupRepository;
import ftn.socialnetwork.repository.PostRepository;
import ftn.socialnetwork.repository.ReactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ReactionService {

    public final PostRepository postRepository;

    public final ReactionRepository reactionRepository;

    @Transactional
    public Reaction save(Reaction reaction) {
        return reactionRepository.save(reaction);
    }

    @Transactional
    public void deleteReaction(Reaction reaction) {
        reactionRepository.delete(reaction);
    }

    public Reaction getReaction(Long id) {
        return reactionRepository.findById(id).get();
    }

    public List<Reaction> getAllReactions() {
        return reactionRepository.findAll();
    }

    public List<Reaction> getReactionsByPost(Long id) {
        Post post = postRepository.findById(id).get();
        return post.getReactions();
    }

}

