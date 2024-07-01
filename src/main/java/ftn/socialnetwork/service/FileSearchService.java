package ftn.socialnetwork.service;

import ftn.socialnetwork.indexmodel.FileIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FileSearchService {

    Page<FileIndex> simpleSearch(List<String> keywords, Pageable pageable, String type);

}
