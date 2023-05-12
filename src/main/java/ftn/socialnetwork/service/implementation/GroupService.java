package ftn.socialnetwork.service.implementation;

import ftn.socialnetwork.model.entity.Group;
import ftn.socialnetwork.repository.GroupRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class GroupService {

    public final GroupRepository repository;

    @Transactional
    public void save(Group group){
        repository.save(group);
    }

    @Transactional
    public List<Group> getAll() {
        return repository.findAll();
    }

    @Transactional
    public Group getGroup(Long id) {
        return repository.findById(id).get();
    }
}
