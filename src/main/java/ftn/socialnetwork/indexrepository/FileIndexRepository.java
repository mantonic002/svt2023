package ftn.socialnetwork.indexrepository;

import ftn.socialnetwork.indexmodel.FileIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface FileIndexRepository  extends ElasticsearchRepository<FileIndex, String> {
}