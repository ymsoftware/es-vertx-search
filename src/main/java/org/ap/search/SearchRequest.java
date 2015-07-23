package org.ap.search;

/**
 * Created by ymetelkin on 7/14/15.
 */
public class SearchRequest {
    String query;
    String[] mediaTypes;
    Integer[] includeProducts;
    Integer[] excludeProducts;
    String[] fields;
    Integer from;
    Integer size;
    FieldRange<String> dateRange;

    public FieldRange<String> getDateRange() {
        return dateRange;
    }

    public SearchRequest setDateRange(FieldRange<String> dateRange) {
        this.dateRange = dateRange;
        return this;
    }

    public String getQuery() {
        return query;
    }

    public SearchRequest setQuery(String query) {
        this.query = query;
        return this;
    }

    public String[] getMediaTypes() {
        return mediaTypes;
    }

    public SearchRequest setMediaTypes(String[] mediaTypes) {
        this.mediaTypes = mediaTypes;
        return this;
    }

    public Integer[] getIncludeProducts() {
        return includeProducts;
    }

    public SearchRequest setIncludeProducts(Integer[] includeProducts) {
        this.includeProducts = includeProducts;
        return this;
    }

    public Integer[] getExcludeProducts() {
        return excludeProducts;
    }

    public SearchRequest setExcludeProducts(Integer[] excludeProducts) {
        this.excludeProducts = excludeProducts;
        return this;
    }

    public String[] getFields() {
        return fields;
    }

    public SearchRequest setFields(String[] fields) {
        this.fields = fields;
        return this;
    }

    public Integer getFrom() {
        return from;
    }

    public SearchRequest setFrom(Integer from) {
        this.from = from;
        return this;
    }

    public Integer getSize() {
        return size;
    }

    public SearchRequest setSize(Integer size) {
        this.size = size;
        return this;
    }
}
