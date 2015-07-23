package org.ap.search.vertx;

import io.vertx.core.json.JsonArray;

/**
 * Created by ymetelkin on 7/16/15.
 */
public class Helpers {
    public static String[] toStringArray(JsonArray ja) {
        if (ja == null) return null;

        int size = ja.size();
        if (size == 0) {
            return new String[]{};
        } else {
            String[] array = new String[size];
            for (int i = 0; i < size; i++) {
                array[i] = ja.getString(i);
            }
            return array;
        }
    }

    public static Integer[] toIntegerArray(JsonArray ja) {
        if (ja == null) return null;

        int size = ja.size();
        if (size == 0) {
            return new Integer[]{};
        } else {
            Integer[] array = new Integer[size];
            for (int i = 0; i < size; i++) {
                array[i] = ja.getInteger(i);
            }
            return array;
        }
    }
}
