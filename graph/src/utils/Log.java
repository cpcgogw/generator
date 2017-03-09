package utils;

/**
 * utils.Log class for printing out information during execution.
 *
 * Created by time on 2/22/17.
 */

public class Log {
    public enum LEVEL {NONE, ERROR, WARNING, DEBUG, INFO};

    private static boolean LOGGING = false;
    public static LEVEL level = LEVEL.NONE;
    public static String prefix = "";

    public static void print(String message, LEVEL l) {
        if (level.compareTo(l) >= 0 && level != LEVEL.NONE) {
            System.out.println(l+": "+prefix+message);
        }
    }

    public static void print(Object message, LEVEL l) {
        print(message.toString(), l);
    }
}
