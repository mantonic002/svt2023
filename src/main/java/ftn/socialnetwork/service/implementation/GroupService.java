package ftn.socialnetwork.service.implementation;

import ftn.socialnetwork.model.entity.Group;
import ftn.socialnetwork.repository.GroupRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class GroupService {

    public final GroupRepository repository;

    @Transactional
    public Group save(Group group){
        repository.save(group);
        return group;
    }

    @Transactional
    public List<Group> getAll() {
        return repository.findAll();
    }

    @Transactional
    public Group getGroup(Long id) {
        return repository.findById(id).get();
    }

    public void deleteGroup(Long id) {
        repository.deleteById(id);
    }
}
