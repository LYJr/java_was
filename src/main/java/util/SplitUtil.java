package util;

public class SplitUtil {

    public static String[] urlSplit(String read) {
        return read.split(" ");
    }

    public static String bodySplit(String read) {
        String[] split = read.replaceAll(" ", "").split(":");
        return split[1];
    }
}
