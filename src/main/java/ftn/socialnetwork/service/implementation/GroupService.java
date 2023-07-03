package ftn.socialnetwork.service.implementation;

import ftn.socialnetwork.model.entity.Group;
import ftn.socialnetwork.model.entity.GroupRequest;
import ftn.socialnetwork.model.entity.User;
import ftn.socialnetwork.repository.GroupRepository;
import ftn.socialnetwork.repository.GroupRequestRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class GroupService {

    public final GroupRepository repository;

    public final GroupRequestRepository groupRequestRepository;

    @Transactional
    public Group save(Group group){
        repository.save(group);
        return group;
    }

    @Transactional
    public List<Group> getAll() {
        return repository.findAll();
    }

    public List<Group> getAllActiveGroups() {
        return repository.findByIsSuspended(false);
    }

    @Transactional
    public Group getGroup(Long id) {
        return repository.findById(id).get();
    }

    public void deleteGroup(Long id, String reason) {
        Group group = repository.findById(id).get();
        group.setSuspended(true);
        group.setSuspendedReason(reason);
        group.getAdmins().clear();
        repository.save(group);
    }

    public GroupRequest createGroupRequest(GroupRequest groupRequest) {
        return groupRequestRepository.save(groupRequest);
    }

    public GroupRequest getGroupRequest(Long requestId) {
        return groupRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Group request not found"));
    }

    public GroupRequest saveGroupRequest(GroupRequest groupRequest) {
        return groupRequestRepository.save(groupRequest);
    }

    public boolean isGroupAdmin(Group group, User user) {
        return group.getAdmins().stream().anyMatch(admin -> admin.getUser().equals(user));
    }

    public boolean isGroupMember(Group group, User user) {
        return group.getGroupRequests().stream()
                .filter(request -> request.getApproved())
                .anyMatch(request -> request.getUser().equals(user));
    }

    public boolean isGroupRequestExists(Group group, User user) {
        return group.getGroupRequests().stream()
                .anyMatch(request -> request.getUser().equals(user));
    }

    public void addGroupMember(Group group, User user) {
        GroupRequest request = group.getGroupRequests().stream()
                .filter(req -> req.getUser().equals(user))
                .findFirst()
                .orElse(null);

        if (request != null && !request.getApproved()) {
            request.setApproved(true);
            repository.save(group);
        }
    }

    public void deleteGroupRequest(GroupRequest groupRequest) {
        groupRequestRepository.delete(groupRequest);
    }

    public List<Group> getGroupsByUser(User user) {
        List<GroupRequest> approvedRequests = groupRequestRepository.findByUserAndApproved(user, true);
        List<Group> groups = new ArrayList<>();
        for (GroupRequest request : approvedRequests) {
            Group group = request.getGroup();
            groups.add(group);
        }
        return groups;
    }

}
