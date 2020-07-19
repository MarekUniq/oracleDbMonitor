package database;

import java.sql.ResultSet;

/**
 *
 */
class GvInstance {
    //
    static final int INST_ID = 0;
    static final int HOST_NAME = 1;
    static final int VERSION = 2;
    static final int STARTUP_TIME = 3;
    //  static final int STATUS = 4;
    //
    static final String[] columnNames = {
            "INST_ID"
            , "HOST_NAME"
            , "VERSION"
            , "STARTUP_TIME"
            //    , "STATUS"
    };

    //
    private static String getColumnName(int columnPosition) {
        return columnNames[columnPosition];
    }

    //
    private String instId;            // NUMBER
    private String hostName;            // VARCHAR2
    private String version;            // VARCHAR2
    //  private String startupTime;            // DATE
    private int startupTime;            // DATE
    //  private String status;            // VARCHAR2

    //
    public String getInstId() {
        return instId;
    }

    void setInstId(String instId) {
        this.instId = instId;
    }

    public String getHostName() {
        return hostName;
    }

    void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getVersion() {
        return version;
    }

    void setVersion(String version) {
        this.version = version;
    }

    public int getStartupTime() {
        return startupTime;
    }

    void setStartupTime(int startupTime) {
        this.startupTime = startupTime;
    }

    //
    //
    public GvInstance(ResultSet rs) throws Exception {
        setInstId(rs.getString(getColumnName(INST_ID)));
        setHostName(rs.getString(getColumnName(HOST_NAME)));
        setVersion(rs.getString(getColumnName(VERSION)));
        setStartupTime(rs.getInt(getColumnName(STARTUP_TIME)));
    }

    public String getPrimaryKey() {
        return getInstId();
    }
}
