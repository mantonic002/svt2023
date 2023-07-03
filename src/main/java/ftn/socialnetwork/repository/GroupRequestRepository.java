package ftn.socialnetwork.repository;

import ftn.socialnetwork.model.entity.GroupRequest;
import ftn.socialnetwork.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRequestRepository extends JpaRepository<GroupRequest, Long> {
    List<GroupRequest> findByUserAndApproved(User user, boolean approved);
}

