package ftn.socialnetwork.service.implementation;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import ftn.socialnetwork.exceptionhandling.exception.MalformedQueryException;
import ftn.socialnetwork.indexmodel.FileIndex;
import ftn.socialnetwork.indexmodel.GroupIndex;
import ftn.socialnetwork.indexrepository.GroupIndexRepository;
import ftn.socialnetwork.model.entity.Group;
import ftn.socialnetwork.service.FileSearchService;
import ftn.socialnetwork.service.GroupSearchService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.tika.language.detect.LanguageDetector;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.elasticsearch.common.unit.Fuzziness;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupSearchServiceImpl implements GroupSearchService {

    private final ElasticsearchOperations elasticsearchTemplate;

    private final GroupIndexRepository indexRepository;

    private final LanguageDetector languageDetector;

    private final FileSearchService fileSearchService;

    @Override
    @Transactional
    public String indexDocument(Group group) {
        GroupIndex newEntity = new GroupIndex(group.getId(), group.getName(), group.getCreationDate(), group.isSuspended(), group.getSuspendedReason());

        if (detectLanguage(group.getDescription()).equals("SR")) {
            newEntity.setDescriptionSr(group.getDescription());
        } else {
            newEntity.setDescriptionEn(group.getDescription());
        }

        indexRepository.save(newEntity);

        return group.getName();
    }

    private String detectLanguage(String text) {
        var detectedLanguage = languageDetector.detect(text).getLanguage().toUpperCase();
        if (detectedLanguage.equals("HR")) {
            detectedLanguage = "SR";
        }

        return detectedLanguage;
    }

    @Override
    public Page<GroupIndex> simpleSearch(List<String> keywords, Pageable pageable) {

        // Search for files related to groups
        Page<FileIndex> fileResults = fileSearchService.simpleSearch(keywords, pageable, "group");

        // Extract group ids from fileResults
        List<FieldValue> groupIdsFromFiles = fileResults.getContent().stream()
                .map(FileIndex::getGroupId)
                .distinct()
                .map(FieldValue::of)
                .toList();


        var searchQueryBuilder =
            new NativeQueryBuilder().withQuery(buildSimpleSearchQuery(keywords, groupIdsFromFiles))
                .withPageable(pageable);

        return runQuery(searchQueryBuilder.build());
    }


    @Override
    public Page<GroupIndex> advancedSearch(List<String> expression, Pageable pageable) {
        if (expression.size() != 3) {
            throw new MalformedQueryException("Search query malformed.");
        }

        String operation = expression.get(1);
        expression.remove(1);
        var searchQueryBuilder =
            new NativeQueryBuilder().withQuery(buildAdvancedSearchQuery(expression, operation))
                .withPageable(pageable);

        return runQuery(searchQueryBuilder.build());
    }

    private Query buildSimpleSearchQuery(List<String> tokens, List<FieldValue> groupIdsFromFiles) {
        return BoolQuery.of(q -> q.must(mb -> mb.bool(b -> {
            tokens.forEach(token -> {
                // Match Query - full-text search with fuzziness
                // Matches documents with fuzzy matching in "name" field
                b.should(sb -> sb.match(
                    m -> m.field("name").fuzziness(Fuzziness.ONE.asString()).query(token)));

                // Match Query - full-text search in other fields
                // Matches documents with full-text search in other fields
                b.should(sb -> sb.match(m -> m.field("description_sr").query(token)));
                b.should(sb -> sb.match(m -> m.field("description_en").query(token)));

            });
            // Ensure documents have a group ID from the file search results
            if (!groupIdsFromFiles.isEmpty()) {
                b.should(sb -> sb.terms(t -> t.field("id").terms(tq -> tq.value(groupIdsFromFiles))));
            }

            return b;
        })))._toQuery();
    }

    private Query buildAdvancedSearchQuery(List<String> operands, String operation) {
        return BoolQuery.of(q -> q.must(mb -> mb.bool(b -> {
            var field1 = operands.get(0).split(":")[0];
            var value1 = operands.get(0).split(":")[1];
            var field2 = operands.get(1).split(":")[0];
            var value2 = operands.get(1).split(":")[1];

            switch (operation) {
                case "AND":
                    b.must(sb -> sb.match(
                        m -> m.field(field1).fuzziness(Fuzziness.ONE.asString()).query(value1)));
                    b.must(sb -> sb.match(m -> m.field(field2).query(value2)));
                    break;
                case "OR":
                    b.should(sb -> sb.match(
                        m -> m.field(field1).fuzziness(Fuzziness.ONE.asString()).query(value1)));
                    b.should(sb -> sb.match(m -> m.field(field2).query(value2)));
                    break;
                case "NOT":
                    b.must(sb -> sb.match(
                        m -> m.field(field1).fuzziness(Fuzziness.ONE.asString()).query(value1)));
                    b.mustNot(sb -> sb.match(m -> m.field(field2).query(value2)));
                    break;
            }

            return b;
        })))._toQuery();
    }

    private Page<GroupIndex> runQuery(NativeQuery searchQuery) {

        var searchHits = elasticsearchTemplate.search(searchQuery, GroupIndex.class,
            IndexCoordinates.of("group_index"));

        var searchHitsPaged = SearchHitSupport.searchPageFor(searchHits, searchQuery.getPageable());

        return (Page<GroupIndex>) SearchHitSupport.unwrapSearchHits(searchHitsPaged);
    }
}
