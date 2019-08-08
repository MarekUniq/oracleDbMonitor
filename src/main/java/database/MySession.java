package database;

/**
 *
 */
class MySession {

    private static String instId;
    private static String sid;

    public static String getInstId() {
        return instId;
    }

    public static void setInstId(String instId) {
        MySession.instId = instId;
    }

    public static String getSid() {
        return sid;
    }

    public static void setSid(String sid) {
        MySession.sid = sid;
    }


}
