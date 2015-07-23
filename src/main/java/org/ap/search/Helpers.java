package org.ap.search;

/**
 * Created by ymetelkin on 7/17/15.
 */
public class Helpers {
    public static boolean isNullOrWhiteSpace(String text) {
        if (text == null || text.length() == 0) return true;
        return text.trim().length() == 0;
    }

    public static boolean inQuotes(String text) {
        if (isNullOrWhiteSpace(text) || text == "\"\"") return false;
        return text.indexOf("\"") == 0 && text.indexOf("\"", 1) == text.length() - 1;
    }
}
