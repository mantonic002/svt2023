package ftn.socialnetwork.controller;

import ftn.socialnetwork.model.dto.DocumentFileDTO;
import ftn.socialnetwork.model.entity.*;
import ftn.socialnetwork.service.IndexingService;
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

    private final IndexingService indexingService;


    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody Group group, Principal principal) {
        if (principal != null){
            String currentUsername = principal.getName();
            User currentUser = userService.findByUsername(currentUsername);

            GroupAdmin admin = new GroupAdmin();
            admin.setUser(currentUser);
            admin.setGroup(group);
            group.getAdmins().add(admin);
        }


        group.setCreationDate(LocalDate.now());
        group.setSuspended(false);

        groupService.save(group);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(group);
    }

    @PostMapping("/add_file/{id}")
    public ResponseEntity<Group> addFile(@PathVariable Long id, @ModelAttribute DocumentFileDTO documentFile) {
        Group group = groupService.getGroup(id);
        File file = indexingService.indexDocument(documentFile.file(), "group", id);
        group.setFile(file);
        file.setGroup(group);
        groupService.save(group);
        return new ResponseEntity<>(group, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Group> updateGroup(@RequestBody Group group) {
        Group oldGroup = groupService.getGroup(group.getId());
        group.setAdmins(oldGroup.getAdmins());
        Group updatedGroup = groupService.save(group);
        return new ResponseEntity<>(updatedGroup, HttpStatus.OK);
    }


    @PutMapping ("/delete/{id}")
    public ResponseEntity<?> deleteGroup (@PathVariable("id") Long id, @RequestParam("reason") String reason) {
        groupService.deleteGroup(id, reason);
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
    public ResponseEntity<List<Group>> getAllGroups() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(groupService.getAllActiveGroups());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Group> getGroup(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(groupService.getGroup(id));
    }

    @PostMapping("/{groupId}/request")
    public ResponseEntity<GroupRequest> createGroupRequest(@PathVariable("groupId") Long groupId, Principal principal) {
        String currentUsername = principal.getName();
        User currentUser = userService.findByUsername(currentUsername);

        Group group = groupService.getGroup(groupId);

        // Check if the user is already a member or if a request already exists
        if (groupService.isGroupMember(group, currentUser) || groupService.isGroupRequestExists(group, currentUser)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Create the group request
        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setGroup(group);
        groupRequest.setUser(currentUser);
        groupRequest.setApproved(false);
        groupRequest.setAt(LocalDateTime.now());

        GroupRequest createdRequest = groupService.createGroupRequest(groupRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdRequest);
    }

    @PutMapping("/{groupId}/request/{requestId}/approve")
    public ResponseEntity<GroupRequest> approveGroupRequest(@PathVariable("groupId") Long groupId, @PathVariable("requestId") Long requestId, Principal principal) {
        String currentUsername = principal.getName();
        User currentUser = userService.findByUsername(currentUsername);

        Group group = groupService.getGroup(groupId);
        GroupRequest groupRequest = groupService.getGroupRequest(requestId);

        // Check if the group request belongs to the specified group
        if (!groupRequest.getGroup().equals(group)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Check if the current user is a GroupAdmin
        if (!groupService.isGroupAdmin(group, currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Approve the group request
        groupRequest.setApproved(true);
        GroupRequest approvedRequest = groupService.saveGroupRequest(groupRequest);

        // Add the user as a member of the group
        User user = groupRequest.getUser();
        groupService.addGroupMember(group, user);

        return ResponseEntity.status(HttpStatus.OK)
                .body(approvedRequest);
    }

    @PutMapping("/{groupId}/request/{requestId}/reject")
    public ResponseEntity<?> rejectGroupRequest(@PathVariable("groupId") Long groupId, @PathVariable("requestId") Long requestId) {
        Group group = groupService.getGroup(groupId);
        GroupRequest groupRequest = groupService.getGroupRequest(requestId);

        // Check if the group request belongs to the specified group
        if (!groupRequest.getGroup().equals(group)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Delete the group request
        groupService.deleteGroupRequest(groupRequest);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Group>> getGroupsByUser(@PathVariable("userId") Long userId) {
        User user = userService.findById(userId);
        List<Group> groups = groupService.getGroupsByUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(groups);
    }
}
