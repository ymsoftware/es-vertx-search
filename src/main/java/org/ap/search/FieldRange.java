package org.ap.search;

/**
 * Created by ymetelkin on 7/15/15.
 */
public class FieldRange<T> {
    String field;
    RangeValue<T> lower;
    RangeValue<T> upper;

    public String getField() {
        return field;
    }

    public RangeValue<T> getLower() {
        return lower;
    }

    public void setLower(RangeValue<T> lower) {
        this.lower = lower;
    }

    public RangeValue<T> getUpper() {
        return upper;
    }

    public void setUpper(RangeValue<T> upper) {
        this.upper = upper;
    }

    public FieldRange setField(String field) {
        this.field = field;
        return this;
    }

    public FieldRange gt(T value) {
        this.lower = RangeValue.Exclude(value);
        return this;
    }

    public FieldRange gte(T value) {
        this.lower = RangeValue.Include(value);
        return this;
    }

    public FieldRange lt(T value) {
        this.upper = RangeValue.Exclude(value);
        return this;
    }

    public FieldRange lte(T value) {
        this.upper = RangeValue.Include(value);
        return this;
    }

    public boolean isSet() {
        return !Helpers.isNullOrWhiteSpace(this.field) && (this.lower != null || this.upper != null);
    }
}
