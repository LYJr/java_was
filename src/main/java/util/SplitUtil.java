package util;

public class SplitUtil {

    public static String[] urlSplit(String read) {
        String[] split = read.split(" ");
        return split;
    }
    public static String bodySplit(String read) {
        String[] split = read.replaceAll(" ", "").split(":");
        return split[1];
    }

    public static String stylesheetSplit(String url) {
        String[] split = url.split("/|\\.");
        return split[split.length-1];
    }
}
