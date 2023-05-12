package ftn.socialnetwork.controller;

import ftn.socialnetwork.model.entity.Post;
import ftn.socialnetwork.service.implementation.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/post")
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity createPost(@RequestBody Post post) {
        postService.save(post);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public Post getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    @GetMapping
    public List<Post> getAllPosts(){
        return postService.getAllPosts();
    }

//    @GetMapping("/by-group/{id}")
//    public List<Post> getPostsByGroup(Long id) {
//    //TODO
//        return postService.getPostsByGroup(id);
//    }

}
