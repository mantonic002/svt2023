package ftn.socialnetwork.controller;

import ftn.socialnetwork.model.entity.Group;
import ftn.socialnetwork.service.implementation.GroupService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/group")
@AllArgsConstructor
@Slf4j
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody Group group) {
        groupService.save(group);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(group);
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
