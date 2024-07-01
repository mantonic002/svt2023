package ftn.socialnetwork.indexrepository;

import ftn.socialnetwork.indexmodel.PostIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostIndexRepository
        extends ElasticsearchRepository<PostIndex, Long> {
}
