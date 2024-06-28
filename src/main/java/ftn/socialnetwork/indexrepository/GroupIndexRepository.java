package ftn.socialnetwork.indexrepository;

import ftn.socialnetwork.indexmodel.GroupIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupIndexRepository
        extends ElasticsearchRepository<GroupIndex, Long> {
}
