package common;


import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class Log {

    public static final String EOL = System.lineSeparator();

    //
    private static final SimpleDateFormat s =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");

    //
    public static String getLinePrefix() {
        return s.format(new Date()) + "[" + Thread.currentThread().getName() + "] ";
    }

    //
    public static void println(Object o) {
        _println(o, System.out);
    }

    //
    public static void errPrintln(Object o) {
        _println(o, System.err);
    }

    //
    private static void _println(Object o, PrintStream stream) {
        String[] lines = o.toString().split("\\n");
        String prefix = getLinePrefix();
        for (String line : lines) {
            stream.println(prefix + line);
        }
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
