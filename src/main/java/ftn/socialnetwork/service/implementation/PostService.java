package ftn.socialnetwork.service.implementation;

import ftn.socialnetwork.model.entity.Post;
import ftn.socialnetwork.repository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class PostService {


    public final PostRepository postRepository;

    @Transactional
    public void save(Post post) {
        postRepository.save(post);
    }


    public Post getPost(Long id) {
        return postRepository.findById(id).get();
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }


//    public List<Post> getPostsByGroup(Long id) {
//        //TODO
//    }
}
