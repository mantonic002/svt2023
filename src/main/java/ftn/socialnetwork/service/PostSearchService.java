package ftn.socialnetwork.service;

import ftn.socialnetwork.indexmodel.PostIndex;
import ftn.socialnetwork.model.entity.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostSearchService {

    @Transactional
    String indexDocument(Post post);

    PostIndex updatePostLikeNum(Long id);

    Page<PostIndex> simpleSearch(List<String> keywords, Pageable pageable);

    Page<PostIndex> rangeSearch(Integer min, Integer max, Pageable pageable);

    Page<PostIndex> advancedSearch(List<String> expression, Pageable pageable);
}
