package ftn.socialnetwork.repository;

import ftn.socialnetwork.model.entity.File;
import ftn.socialnetwork.model.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Integer> {
}
