package ftn.socialnetwork.repository;

import ftn.socialnetwork.model.entity.GroupAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupAdminRepository extends JpaRepository<GroupAdmin, Long> {
}
