package common;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class Log {

    public static final String EOL = "\n\r";

    //
    private static final SimpleDateFormat s =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");

    //
    public static String getLinePrefix() {
        return s.format(new Date()) + "[" + Thread.currentThread().getName() + "] ";
    }

    //
    public static void println(Object o) {
        System.out.println(getLinePrefix() + o);
    }

    //
    public static void errPrintln(Object o) {
        System.err.println(getLinePrefix() + o);
    }

    //
    public static void print(Object o) {
        System.out.print(o);
    }

    //
    public static void println(Object o, Throwable e) {
        println(o);
        printException(e);
    }

    public static void printException(Throwable e) {
        e.printStackTrace();
    }
}
