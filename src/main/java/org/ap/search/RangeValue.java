package org.ap.search;

/**
 * Created by ymetelkin on 7/15/15.
 */
public class RangeValue<T> {
    T value;
    Boolean exclusive;

    public T getValue() {
        return value;
    }

    public Boolean getExclusive() {
        return exclusive;
    }

    public RangeValue(T value, Boolean exclude) {
        this.value = value;
        this.exclusive = exclude;
    }

    public static <T> RangeValue Include(T value) {
        return new RangeValue(value, false);
    }

    public static <T> RangeValue Exclude(T value) {
        return new RangeValue(value, true);
    }
}
