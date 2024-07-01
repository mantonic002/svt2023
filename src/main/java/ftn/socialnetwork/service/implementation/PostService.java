package ftn.socialnetwork.service.implementation;

import ftn.socialnetwork.model.entity.Group;
import ftn.socialnetwork.model.entity.Post;
import ftn.socialnetwork.repository.GroupRepository;
import ftn.socialnetwork.repository.PostRepository;
import ftn.socialnetwork.service.GroupSearchService;
import ftn.socialnetwork.service.PostSearchService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class PostService {


    public final PostRepository postRepository;

    public final GroupRepository groupRepository;

    public final PostSearchService searchService;


    @Transactional
    public Post save(Post post) {
        postRepository.save(post);
        searchService.indexDocument(post);
        return post;
    }

    @Transactional
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public Post getPost(Long id) {
        return postRepository.findById(id).get();
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public List<Post> getPostsByGroup(Long id) {
        Group group = groupRepository.findById(id).get();
        return group.getPosts();
    }

}
