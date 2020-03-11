package util;

public class SplitUtil {

    public static String urlSplit(String read) {
        String[] split = read.split(" ");
        return split[1];
    }

    public static String bodySplit(String read) {
        String[] split = read.replaceAll(" ", "").split(":");
        return split[1];
    }
}
