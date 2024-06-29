package ftn.socialnetwork.service.implementation;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import ftn.socialnetwork.exceptionhandling.exception.MalformedQueryException;
import ftn.socialnetwork.indexmodel.GroupIndex;
import ftn.socialnetwork.indexrepository.GroupIndexRepository;
import ftn.socialnetwork.model.entity.Group;
import ftn.socialnetwork.repository.GroupRepository;
import ftn.socialnetwork.service.SearchService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.tika.language.detect.LanguageDetector;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.elasticsearch.common.unit.Fuzziness;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final ElasticsearchOperations elasticsearchTemplate;

    private final GroupIndexRepository indexRepository;

    private final GroupRepository dummyRepository;

    private final LanguageDetector languageDetector;


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
        var searchQueryBuilder =
            new NativeQueryBuilder().withQuery(buildSimpleSearchQuery(keywords))
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

    private Query buildSimpleSearchQuery(List<String> tokens) {
        return BoolQuery.of(q -> q.must(mb -> mb.bool(b -> {
            tokens.forEach(token -> {
                // Term Query - simplest
                // Matches documents with exact term in "title" field
//            b.should(sb -> sb.term(m -> m.field("title").value(token)));

                // Terms Query
                // Matches documents with any of the specified terms in "title" field
//            var terms = new ArrayList<>(List.of("dummy1", "dummy2"));
//            var titleTerms = new TermsQueryField.Builder()
//                .value(terms.stream().map(FieldValue::of).toList())
//                .build();
//            b.should(sb -> sb.terms(m -> m.field("title").terms(titleTerms)));

                // Match Query - full-text search with fuzziness
                // Matches documents with fuzzy matching in "title" field
                b.should(sb -> sb.match(
                    m -> m.field("name").fuzziness(Fuzziness.ONE.asString()).query(token)));

                // Match Query - full-text search in other fields
                // Matches documents with full-text search in other fields
                b.should(sb -> sb.match(m -> m.field("description_sr").query(token)));
//                b.should(sb -> sb.match(m -> m.field("content_sr").query(token)));
//                b.should(sb -> sb.match(m -> m.field("content_en").query(token)));

                // Wildcard Query - unsafe
                // Matches documents with wildcard matching in "title" field
//            b.should(sb -> sb.wildcard(m -> m.field("title").value("*" + token + "*")));

                // Regexp Query - unsafe
                // Matches documents with regular expression matching in "title" field
//            b.should(sb -> sb.regexp(m -> m.field("title").value(".*" + token + ".*")));

                // Boosting Query - positive gives better score, negative lowers score
                // Matches documents with boosted relevance in "title" field
//            b.should(sb -> sb.boosting(bq -> bq.positive(m -> m.match(ma -> ma.field("title").query(token)))
//                                              .negative(m -> m.match(ma -> ma.field("description").query(token)))
//                                              .negativeBoost(0.5f)));

                // Match Phrase Query - useful for exact-phrase search
                // Matches documents with exact phrase match in "title" field
//            b.should(sb -> sb.matchPhrase(m -> m.field("title").query(token)));

                // Fuzzy Query - similar to Match Query with fuzziness, useful for spelling errors
                // Matches documents with fuzzy matching in "title" field
//            b.should(sb -> sb.match(
//                m -> m.field("title").fuzziness(Fuzziness.ONE.asString()).query(token)));

                // Range query - not applicable for dummy index, searches in the range from-to
            });
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
