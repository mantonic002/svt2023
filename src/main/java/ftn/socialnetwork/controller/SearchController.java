package ftn.socialnetwork.controller;


import ftn.socialnetwork.indexmodel.GroupIndex;
import ftn.socialnetwork.indexmodel.PostIndex;
import ftn.socialnetwork.model.dto.SearchQueryDTO;
import ftn.socialnetwork.service.GroupSearchService;
import ftn.socialnetwork.service.PostSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final GroupSearchService searchService;
    private final PostSearchService postSearchService;

    @PostMapping("/group/simple")
    public Page<GroupIndex> simpleSearch(@RequestBody SearchQueryDTO simpleSearchQuery,
                                         Pageable pageable) {
        return searchService.simpleSearch(simpleSearchQuery.keywords(), pageable);
    }

    @PostMapping("/post/simple")
    public Page<PostIndex> simplePostSearch(@RequestBody SearchQueryDTO simpleSearchQuery,
                                            Pageable pageable) {
        return postSearchService.simpleSearch(simpleSearchQuery.keywords(), pageable);
    }


    @PostMapping("/advanced")
    public Page<GroupIndex> advancedSearch(@RequestBody SearchQueryDTO advancedSearchQuery,
                                           Pageable pageable) {
        return searchService.advancedSearch(advancedSearchQuery.keywords(), pageable);
    }
}
