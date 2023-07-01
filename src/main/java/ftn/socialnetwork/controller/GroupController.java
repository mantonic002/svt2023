package ftn.socialnetwork.controller;

import ftn.socialnetwork.model.entity.Group;
import ftn.socialnetwork.model.entity.GroupAdmin;
import ftn.socialnetwork.model.entity.Post;
import ftn.socialnetwork.model.entity.User;
import ftn.socialnetwork.service.UserService;
import ftn.socialnetwork.service.implementation.GroupService;
import ftn.socialnetwork.service.implementation.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/group")
@AllArgsConstructor
@Slf4j
public class GroupController {

    private final PostService postService;

    private final GroupService groupService;

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody Group group, Principal principal) {
        String currentUsername = principal.getName();
        User currentUser = userService.findByUsername(currentUsername);

        group.setCreationDate(LocalDate.now());
        group.setSuspended(false);

        GroupAdmin admin = new GroupAdmin();
        admin.setUser(currentUser);
        admin.setGroup(group);
        group.getAdmins().add(admin);

        groupService.save(group);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(group);
    }

    @PutMapping("/update")
    public ResponseEntity<Group> updateGroup(@RequestBody Group group) {
        Group oldGroup = groupService.getGroup(group.getId());
        group.setAdmins(oldGroup.getAdmins());
        Group updatedGroup = groupService.save(group);
        return new ResponseEntity<>(updatedGroup, HttpStatus.OK);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteGroup (@PathVariable("id") Long id) {
        groupService.deleteGroup(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping("/add_post/{id}")
    public ResponseEntity<Post> createPost(@PathVariable Long id, @RequestBody Post post) {
        Group group = groupService.getGroup(id);
        post.setGroup(group);
        post.setCreationDate(LocalDateTime.now());
        Post addedPost = postService.save(post);
        return new ResponseEntity<>(addedPost, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Group>> getAllGroups(){;
        return ResponseEntity.status(HttpStatus.OK)
                .body(groupService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Group> getGroup(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(groupService.getGroup(id));
    }
}
