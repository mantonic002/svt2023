package ftn.socialnetwork.service;

import ftn.socialnetwork.model.entity.File;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface IndexingService {

    File indexDocument(MultipartFile documentFile, String type, Long id);
}
