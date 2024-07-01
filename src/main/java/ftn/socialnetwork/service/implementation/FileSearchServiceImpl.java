package ftn.socialnetwork.service.implementation;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import ftn.socialnetwork.exceptionhandling.exception.MalformedQueryException;
import ftn.socialnetwork.indexmodel.FileIndex;
import ftn.socialnetwork.service.FileSearchService;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.common.unit.Fuzziness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FileSearchServiceImpl implements FileSearchService {

    private final ElasticsearchOperations elasticsearchTemplate;

    @Override
    public Page<FileIndex> simpleSearch(List<String> keywords, Pageable pageable, String type) {
        var searchQueryBuilder =
            new NativeQueryBuilder().withQuery(buildSimpleSearchQuery(keywords, type))
                .withPageable(pageable);

        return runQuery(searchQueryBuilder.build());
    }


    private Query buildSimpleSearchQuery(List<String> tokens, String type) {
        return BoolQuery.of(q -> q.must(mb -> mb.bool(b -> {
            tokens.forEach(token -> {
                // Match Query - full-text search with fuzziness
                // Matches documents with fuzzy matching in "title" field
                b.should(sb -> sb.match(
                    m -> m.field("title").fuzziness(Fuzziness.ONE.asString()).query(token)));

                // Match Query - full-text search in other fields
                // Matches documents with full-text search in other fields
                b.should(sb -> sb.match(m -> m.field("content_sr").query(token)));
                b.should(sb -> sb.match(m -> m.field("content_en").query(token)));
            });
//            if (Objects.equals(type, "group")) {
//                // Ensure documents have a group_id
//                b.filter(fb -> fb.exists(e -> e.field("group_id")));
//            } else if (Objects.equals(type, "post")) {
//                // Ensure documents have a post_id
//                b.filter(fb -> fb.exists(e -> e.field("post_id")));
//            }
            return b;
        })))._toQuery();
    }


    private Page<FileIndex> runQuery(NativeQuery searchQuery) {

        var searchHits = elasticsearchTemplate.search(searchQuery, FileIndex.class,
            IndexCoordinates.of("file_index"));

        var searchHitsPaged = SearchHitSupport.searchPageFor(searchHits, searchQuery.getPageable());

        return (Page<FileIndex>) SearchHitSupport.unwrapSearchHits(searchHitsPaged);
    }
}
