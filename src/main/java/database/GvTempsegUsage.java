package database;

import java.sql.ResultSet;

/**
 *
 */
public class GvTempsegUsage {

    //
    static final int INST_ID = 0;
    static final int SESSION_ADDR = 1;
    static final int SQL_ID = 2;
    static final int TABLESPACE = 3;
    static final int SEGTYPE = 4;
    static final int SEGBLK = 5;
    static final int BLOCKS = 6;

    //
    static final String[] columnNames = {
            "INST_ID"
            , "SESSION_ADDR"
            , "SQL_ID"
            , "TABLESPACE"
            , "SEGTYPE"
            , "SEGBLK#"
            , "BLOCKS"
    };

    //
    public static String getColumnName(int columnPosition) {
        return columnNames[columnPosition];
    }

    //
    private String instId;            // NUMBER
    private String sessionAddr;            // RAW
    private GvSession gvSession;
    private String sqlId;            // VARCHAR2
    private String tablespace;            // VARCHAR2
    private String segType;            // VARCHAR2
    private String segBlk;            // VARCHAR2
    private long blocks;               // NUMBER

    //
    public String getInstId() {
        return instId;
    }

    public void setInstId(String instId) {
        this.instId = instId;
    }

    public String getSessionAddr() {
        return sessionAddr;
    }

    public void setSessionAddr(String sessionAddr) {
        this.sessionAddr = sessionAddr;
    }

    public GvSession getGvSession() {
        return gvSession;
    }

    public void setGvSession(GvSession gvSession) {
        this.gvSession = gvSession;
    }

    public String getSqlId() {
        return sqlId;
    }

    public void setSqlId(String sqlId) {
        this.sqlId = sqlId;
    }

    public String getTablespace() {
        return tablespace;
    }

    public void setTablespace(String tablespace) {
        this.tablespace = tablespace;
    }

    public String getSegType() {
        return segType;
    }

    public void setSegType(String segType) {
        this.segType = segType;
    }

    public String getSegBlk() {
        return segBlk;
    }

    public void setSegBlk(String segBlk) {
        this.segBlk = segBlk;
    }

    public long getBlocks() {
        return blocks;
    }

    public void setBlocks(long blocks) {
        this.blocks = blocks;
    }

    //
    public GvTempsegUsage(ResultSet rs) throws Exception {
        setInstId(rs.getString(getColumnName(INST_ID)));
        setSessionAddr(rs.getString(getColumnName(SESSION_ADDR)));
        setSqlId(rs.getString(getColumnName(SQL_ID)));
        setTablespace(rs.getString(getColumnName(TABLESPACE)));
        setSegType(rs.getString(getColumnName(SEGTYPE)));
        setSegBlk(rs.getString(getColumnName(SEGBLK)));
        setBlocks(rs.getLong(getColumnName(BLOCKS)));
    }

    //
    public String getPrimaryKey() {
        return getPrimaryKey(getSegBlk(), getSessionAddr(), getInstId());
    }

    //
    public static String getPrimaryKey(String segBlk, String sessionAddr, String instId) {
        return segBlk + "," + sessionAddr + "@" + instId;
    }

}
