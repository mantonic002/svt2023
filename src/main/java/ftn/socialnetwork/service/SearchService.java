package ftn.socialnetwork.service;

import ftn.socialnetwork.indexmodel.GroupIndex;
import ftn.socialnetwork.model.entity.Group;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface SearchService {

    @Transactional
    String indexDocument(Group group);

    Page<GroupIndex> simpleSearch(List<String> keywords, Pageable pageable);

    Page<GroupIndex> advancedSearch(List<String> expression, Pageable pageable);
}
