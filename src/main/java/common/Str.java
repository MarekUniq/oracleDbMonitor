package common;

import telnetServer.TelnetSession;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Str {

    public static final char LEFT_SQUARE_BRACKET = '['; // (char) 91
    public static final char RIGHT_SQUARE_BRACKET = ']'; // (char) 93

    //
    @SuppressWarnings({"unchecked"})
    public static List<ArrayList> convertResultSetToListArray(ResultSet rs) throws Exception {
        //
        int columnCount = rs.getMetaData().getColumnCount();
        List<ArrayList> lists = new ArrayList<>(columnCount);
        // initialize
        for (int i = 0; i < columnCount; i++) {
            lists.add(new ArrayList<>(2));
        }
        // copy header
        for (int i = 0; i < columnCount; i++) {
            lists.get(i).add(rs.getMetaData().getColumnName(i + 1));
        }
        // copy data
        while (rs.next()) {
            for (int i = 0; i < columnCount; i++) {
                lists.get(i).add(rs.getObject(i + 1));
            }
        }
        //
        return lists;
    }

    //
    // used for one row tables like v$database
    //
    public static void convertListArrayToStringBufferSameLine(List<ArrayList> l, StringBuffer sb) {
        //
        for (int i = 0; i < l.size(); i++) {
            List col = l.get(i);
            //
            if (i != 0)
                sb.append(" ");
//        sb.append(" | ");
            sb.append(TelnetSession.GREEN);
            sb.append(col.get(0));
//      sb.append(TelnetSession.COLOR_RESET);
            sb.append(": ");
            sb.append(TelnetSession.DARK_YELLOW);
            sb.append(col.get(1));
            sb.append(TelnetSession.COLOR_RESET);
        }
        sb.append(Log.EOL);
    }

    /*
     * Right padding (Dont truncate if longer)
     *
     * @param s
     * @param len
     * @param c
     * @return
     */
    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public static String rpad(String s, int len, char c) {
        // If s == null, then return null
        if (s == null)
            return null;
            // If s is longer or equal than requested then dont cut it
        else if (s.length() >= len)
            return s;

        // new StringBuffer with string
        StringBuffer sb;
        sb = new StringBuffer(s);

        // Adds the requested amount of characters c
        for (int i = s.length(); i < len; i++) {
            sb.append(c);
        }

        // return it as String
        return sb.toString();
    }

    //
    public static void convertArrayToStringBufferAsTable(String[][] sa, int[] alignment, StringBuffer sb) {
        //
        int[] maxLen = new int[sa.length];
        // find max column length
        for (int i = 0; i < sa.length; i++) {
            int maxColumnLength = 0;
            for (int n = 0; n < sa[i].length; n++) {
                String value = sa[i][n];
                if (value != null && maxColumnLength < value.length())
                    maxColumnLength = value.length();
            }
            //
            maxLen[i] = maxColumnLength;
        }

        // print output
        for (int i = 0; i < sa[0].length; i++) {
            //
            if (i == 0)
                sb.append(TelnetSession.GREEN);
            else
                sb.append(TelnetSession.YELLOW);
            //
            for (int n = 0; n < sa.length; n++) {
                //
                if (n != 0) sb.append(' ');
                //
                String value = sa[n][i];
                if (value == null) value = "";
                //
                if (alignment[n] == ALIGNMENT_LEFT)
                    sb.append(rpad(value, maxLen[n], ' '));
                else if (alignment[n] == ALIGNMENT_RIGHT)
                    sb.append(lpad(value, maxLen[n], ' '));
            }
            sb.append(Log.EOL);
            //
            sb.append(TelnetSession.COLOR_RESET);
        }

    }

    /*
     * Left padding (Dont truncate if longer)
     *
     * @param s
     * @param len
     * @param c
     * @return
     */
    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public static String lpad(String s, int len, char c) {
        // If s == null, then return null
        if (s == null)
            return null;
            // If s is longer or equal than requested then dont cut it
        else if (s.length() >= len)
            return s;

        // new StringBuffer
        StringBuffer sb;
        sb = new StringBuffer();

        // Adds the requested amount of characters c
        for (int i = s.length(); i < len; i++) {
            sb.append(c);
        }

        // append the rest
        sb.append(s);

        // return it as String
        return sb.toString();
    }

    //
    public static final int ALIGNMENT_LEFT = 0;
    public static final int ALIGNMENT_RIGHT = 1;

    //
    //
    //
    public static String rtrunc(String s, int maxLength) {
        //
        if (s == null) return null;
        //
        if (s.length() <= maxLength)
            return s;
        else
            return s.substring(0, maxLength);
    }


    //
    //
    //
    private static final MathContext MATH_CONTEXT = new MathContext(2);

    //
    private enum MetricPrefix {
        pico(1E-12d, "p"), nano(1E-9d, "n"), micro(1E-6d, "μ"), milli(1E-3d, "m") //
        , none(1E+0d, "") //
        , kilo(1E+3d, "k"), mega(1E+6d, "M"), giga(1E+9d, "G"), tera(1E+12d, "T"), peta(1E+15d, "P"), exa(1E+18d, "E");

        //
        double denotingFactor;
        String symbol;

        //
        private MetricPrefix(double denotingFactor, String symbol) {
            this.denotingFactor = denotingFactor;
            this.symbol = symbol;
        }

        public double getDenotingFactor() {
            return denotingFactor;
        }

        public String getSymbol() {
            return symbol;
        }

        // see määrab ülemineku piiri. 3E+3d ütleb, et 2999 on 2999 kuid 3001 on 3k
        public double getLessThanOverOne(double num) {
//      if (Math.abs(num) >= 1d)
//        // if values is greater than 1
//        return denotingFactor * 0.8E+3d;
//      else if (Math.abs(num) >= 0.001d)
//        // between
//        return denotingFactor * 0.01E+3d;
//      else
            // below
            return denotingFactor * 0.8E+3d;
        }

    }

    //
    //
    public static String formatDoubleNumber(double number) {
        //
        double num = Math.abs(number);
        BigDecimal bigDecimal = null;
        MetricPrefix symbol = null;

        if (number == 0d) {
            MetricPrefix metricPrefix = MetricPrefix.none;
            bigDecimal = new BigDecimal(num / metricPrefix.getDenotingFactor());
            symbol = metricPrefix;
        } else {
            for (MetricPrefix metricPrefix : MetricPrefix.values()) {
                if (num < metricPrefix.getLessThanOverOne(num)) {
                    bigDecimal = new BigDecimal(num / metricPrefix.getDenotingFactor());
                    symbol = metricPrefix;
                    break;
                }
            }
            //
            if (bigDecimal == null) {
                MetricPrefix metricPrefix = MetricPrefix.exa;
                bigDecimal = new BigDecimal(num / metricPrefix.getDenotingFactor());
                symbol = metricPrefix;
            }
        }

        //
        if (number >= 0d)
            return bigDecimal.round(MATH_CONTEXT).toPlainString() + symbol.getSymbol();
        else
            return "-" + bigDecimal.round(MATH_CONTEXT).toPlainString() + symbol.getSymbol();
    }


    //
    public static String formatSecondsNumber(long num) {
        //
        if (num < 60)
            return Long.toString(num) + "s";
        else if (num < 3600)
            return Long.toString(num / 60) + "m " + Long.toString((num % 60)) + "s";
        else if (num < 86400)
            return Long.toString(num / 3600) + "h " + Long.toString((num % 3600) / 60) + "m " + Long.toString((num % 60)) + "s";
        else
            return Long.toString(num / 86400) + "d " + Long.toString((num % 86400) / 3600) + "h " + Long.toString((num % 3600) / 60) + "m"
//        + " " + Integer.toString((num%60)) + "s"
                    ;
    }

    //
    public static String throwableToString(Throwable t) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        t.printStackTrace(printWriter);
        return result.toString();
    }

    //
    public static String convertUnixToDos(String unix) {
        StringBuilder sb = new StringBuilder();
        //
        for (char c : unix.toCharArray()) {
            sb.append(c);
            if (c == '\n')
                sb.append('\r');
        }
        //
        return sb.toString();
    }

    /*
     * Adds char ' around String
     *
     * @param s
     * @return String
     */
    public static String surroundWithSquarBracket(String s) {
        //
        return surroundWithChar(s, LEFT_SQUARE_BRACKET, RIGHT_SQUARE_BRACKET);
    }

    /*
     * Adds char leftChar around String
     *
     * @param s
     * @return String
     */
    public static String surroundWithChar(String s, char leftChar, char rightChar) {
        // NULL
        if (s == null)
            return null;
            // length is ZERO or ONE
        else if (s.length() <= 1)
            return leftChar + s + rightChar;
            // all others
        else {
            //
            boolean isFirstChar = s.charAt(0) == leftChar;
            boolean isLastChar = s.charAt(s.length() - 1) == rightChar;
            // if first and last char is QUOTATION_MARK mart already then dont add
            if (isFirstChar && isLastChar)
                return s;
            else
                return leftChar + s + rightChar;
        }
    }

    //
    public static int[] getMaxColumnLength(String[][] output) {
        int maxLen[] = new int[output.length];
        //
        for (int i = 0; i < output.length; i++) {
            for (String cell : output[i]) {
                maxLen[i] = Math.max(maxLen[i], cell.length());
            }
        }
        //
        return maxLen;
    }
}
