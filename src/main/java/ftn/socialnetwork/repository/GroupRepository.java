package ftn.socialnetwork.repository;

import ftn.socialnetwork.model.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    @Override
    Group getOne(Long aLong);

    List<Group> findByIsSuspended(boolean isSuspended);

}
