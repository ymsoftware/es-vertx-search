package org.ap.search.es;

import org.ap.search.*;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.ap.search.Helpers.*;

/**
 * Created by ymetelkin on 7/16/15.
 */
public class ElasticSearchService implements SearchService {
    private Client client = null;
    private ElasticSearchServiceConfig config = null;

    public ElasticSearchService(Client client, ElasticSearchServiceConfig config) {
        this.client = client;
        this.config = config;
    }

    public String search(SearchRequest request) {
        SearchRequestBuilder builder = getBuilder(request);
        return builder.get().toString();
    }

    private SearchRequestBuilder getBuilder(SearchRequest request) {
        SearchRequestBuilder builder = this.client
                .prepareSearch(this.config.index)
                .setTypes(this.config.type);

        QueryBuilder qb = getQueryBuilder(request);
        builder.setQuery(qb);

        Integer from = request.getFrom();
        if (from != null && from > 0) {
            builder.setFrom(from);
        }

        Integer size = request.getSize();
        if (size != null && size > 0) {
            builder.setSize(size);
        }

        String[] fields = request.getFields();
        if (fields != null && fields.length > 0) {
            builder.setFetchSource(fields, null);
        }

        return builder;
    }

    private QueryBuilder getQueryBuilder(SearchRequest request) {
        QueryBuilder qb = null;

        String query = request.getQuery();

        if (isNullOrWhiteSpace(query)) {
            qb = QueryBuilders.matchAllQuery();
        } else {
            boolean quotes = inQuotes(query);
            boolean freeText = !quotes && !(
                    query.contains(":") ||
                            query.contains(" AND ") ||
                            query.contains(" OR ") ||
                            query.contains("NOT") ||
                            query.contains("\\+") ||
                            query.contains("-") ||
                            query.contains("\\*") ||
                            query.contains("\\?") ||
                            query.contains("~") ||
                            query.contains("=") ||
                            query.contains(">") ||
                            query.contains("<"));

//            if ((quotes || freeText) && this.config.fields != null) {
//                MultiMatchQueryBuilder multiMatch = QueryBuilders
//                        .multiMatchQuery(query)
//                        .type(quotes ?
//                                MatchQueryBuilder.Type.PHRASE :
//                                MultiMatchQueryBuilder.Type.MOST_FIELDS);
//
//                this.config.fields.forEach((k, w) -> {
//                    if (w == 1F) {
//                        multiMatch.field(k);
//                    } else {
//                        multiMatch.field(k, w);
//                    }
//                });
//
//                qb = multiMatch;
            if (quotes || freeText) {
                if (this.config.fields == null) {
                    if (quotes) {
                        qb = QueryBuilders.matchPhraseQuery("_all", query);
                    } else {
                        qb = QueryBuilders.matchQuery("_all", query);
                    }
                } else {
                    MultiMatchQueryBuilder multiMatch = QueryBuilders
                            .multiMatchQuery(query)
                            .type(quotes ?
                                    MatchQueryBuilder.Type.PHRASE :
                                    MultiMatchQueryBuilder.Type.MOST_FIELDS);

                    this.config.fields.forEach((k, w) -> {
                        if (w == 1F) {
                            multiMatch.field(k);
                        } else {
                            multiMatch.field(k, w);
                        }
                    });

                    qb = multiMatch;
                }
            } else {
                QueryStringQueryBuilder qsq = QueryBuilders.queryStringQuery(query);
                if (this.config.fields != null) {
                    this.config.fields.forEach((k, w) -> {
                        if (w == 1F) {
                            qsq.field(k);
                        } else {
                            qsq.field(k, w);
                        }
                    });
                }

                qb = qsq;
            }
        }

        List<FilterBuilder> must = new ArrayList<FilterBuilder>();
        List<FilterBuilder> mustNot = new ArrayList<FilterBuilder>();

        addFilter(must, "type", request.getMediaTypes());
        addFilter(must, "filings.products", request.getIncludeProducts());
        addFilter(mustNot, "filings.products", request.getExcludeProducts());

        RangeFilterBuilder range = getDateRangeFilter(request);
        if (range != null) {
            must.add(range);
        }

        int mustSize = must.size();
        int mustNotSize = mustNot.size();
        int total = mustSize + mustNotSize;
        if (total > 0) {
            if (total == 1 && mustNotSize == 0) {
                qb = QueryBuilders.filteredQuery(qb, must.get(0));
            } else {
                BoolFilterBuilder bool = FilterBuilders.boolFilter();

                if (mustSize > 0) {
                    must.forEach(e -> bool.must(e));
                }

                if (mustNotSize > 0) {
                    mustNot.forEach(e -> bool.mustNot(e));
                }

                qb = QueryBuilders.filteredQuery(qb, bool);
            }
        }

//        if (this.functionScoreQuery != null) {
//            qb = this.functionScoreQuery.getQuery(qb);
//        }

        return qb;
    }

    private void addFilter(List<FilterBuilder> list, String field, Object[] values) {
        FilterBuilder filter = getFilter(field, values);
        if (filter != null) {
            list.add(filter);
        }
    }

    private FilterBuilder getFilter(String field, Object[] values) {
        if (values != null) {
            int size = values.length;
            if (size == 1) {
                return FilterBuilders.termFilter(field, values[0]);
            } else if (size > 1) {
                return FilterBuilders.termsFilter(field, values);
            }
        }
        return null;
    }

    private RangeFilterBuilder getDateRangeFilter(SearchRequest request) {
        FieldRange<String> range = request.getDateRange();
        if (range != null && range.isSet()) {
            RangeFilterBuilder rangeFilter = FilterBuilders.rangeFilter(range.getField());

            boolean check = false;

            RangeValue<String> lower = range.getLower();
            if (lower != null) {
                String value = lower.getValue();
                if (!isNullOrWhiteSpace(value)) {
                    check = true;

                    if (lower.getExclusive()) {
                        rangeFilter.gt(value);
                    } else {
                        rangeFilter.gte(value);
                    }
                }
            }

            RangeValue<String> upper = range.getUpper();
            if (upper != null) {
                String value = upper.getValue();
                if (!isNullOrWhiteSpace(value)) {
                    check = true;

                    if (upper.getExclusive()) {
                        rangeFilter.lt(value);
                    } else {
                        rangeFilter.lte(value);
                    }
                }
            }

            if (check) {
                return rangeFilter;
            }
        }

        return null;
    }
}