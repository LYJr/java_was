package util;

import java.util.HashMap;
import java.util.Map;

public class TypeUtil {

    private static Map<String, String> stylesheets = new HashMap<>();

    private static void input() {
        stylesheets.put("doc", "application/msword");
        stylesheets.put("pdf", "application/pdf");
        stylesheets.put("xls", "application/vnd.ms-excel");
        stylesheets.put("js", "application/x-javascript");
        stylesheets.put("zip", "application/zip");

        stylesheets.put("jpeg", "image/jpeg");

        stylesheets.put("css", "text/css");
        stylesheets.put("html", "text/html");
        stylesheets.put("txt", "text/plain");
        stylesheets.put("xml", "text/xml");
        stylesheets.put("xsl", "text/xsl");
    }

    private static boolean isInput() {
        return stylesheets != null || stylesheets.size() != 0;
    }

    public static String stylesheet(String data) {
        input();
        return stylesheets.get(data);
    }
}
