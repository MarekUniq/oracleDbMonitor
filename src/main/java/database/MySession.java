package database;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 *
 */
class MySession {

    //
    private static String instId;
    private static String sid;
    private static Timestamp sysdate;
    //
    private static SimpleDateFormat sysdateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //
    public static String getInstId() {
        return instId;
    }

    public static void setInstId(String instId) {
        MySession.instId = instId;
    }

    //
    public static String getSid() {
        return sid;
    }

    public static void setSid(String sid) {
        MySession.sid = sid;
    }

    //
    public static Timestamp getSysdate() {
        return sysdate;
    }

    public static String getSysdateFormatted() {
        return sysdateFormat.format(sysdate);
    }

    public static void setSysdate(Timestamp sysdate) {
        MySession.sysdate = sysdate;
    }
}
