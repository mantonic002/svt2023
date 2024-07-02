package ftn.socialnetwork.controller;


import ftn.socialnetwork.indexmodel.GroupIndex;
import ftn.socialnetwork.indexmodel.PostIndex;
import ftn.socialnetwork.model.dto.SearchQueryDTO;
import ftn.socialnetwork.service.GroupSearchService;
import ftn.socialnetwork.service.PostSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/group/range/{min}:{max}")
    public Page<GroupIndex> rangeSearch(@PathVariable Integer min, @PathVariable Integer max,
                                         Pageable pageable) {
        return searchService.rangeSearch(min, max, pageable);
    }

    @PostMapping("/post/simple")
    public Page<PostIndex> simplePostSearch(@RequestBody SearchQueryDTO simpleSearchQuery,
                                            Pageable pageable) {
        return postSearchService.simpleSearch(simpleSearchQuery.keywords(), pageable);
    }

    @PostMapping("/post/range/{min}:{max}")
    public Page<PostIndex> rangePostSearch(@PathVariable Integer min, @PathVariable Integer max,
                                        Pageable pageable) {
        return postSearchService.rangeSearch(min, max, pageable);
    }



    @PostMapping("/advanced")
    public Page<GroupIndex> advancedSearch(@RequestBody SearchQueryDTO advancedSearchQuery,
                                           Pageable pageable) {
        return searchService.advancedSearch(advancedSearchQuery.keywords(), pageable);
    }
}
