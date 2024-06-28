package ftn.socialnetwork.service;

import ftn.socialnetwork.indexmodel.GroupIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SearchService {

    Page<GroupIndex> simpleSearch(List<String> keywords, Pageable pageable);

    Page<GroupIndex> advancedSearch(List<String> expression, Pageable pageable);
}
