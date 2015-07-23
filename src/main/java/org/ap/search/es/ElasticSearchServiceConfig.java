package org.ap.search.es;

import java.util.Map;

/**
 * Created by ymetelkin on 7/16/15.
 */
public class ElasticSearchServiceConfig {
    String index;
    String type;
    Map<String, Float> fields;

    public Map<String, Float> getFields() {
        return fields;
    }

    public ElasticSearchServiceConfig setFields(Map<String, Float> fields) {
        this.fields = fields;
        return this;
    }

    public String getIndex() {
        return index;
    }

    public ElasticSearchServiceConfig setIndex(String index) {
        this.index = index;
        return this;
    }

    public String getType() {
        return type;
    }

    public ElasticSearchServiceConfig setType(String type) {
        this.type = type;
        return this;
    }
}
