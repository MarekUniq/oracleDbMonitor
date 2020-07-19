package database;

import java.math.BigDecimal;
import java.sql.ResultSet;

/**
 *
 */
class GvTransaction {
    //  select 'static final int ' ||  column_name || ' = ' || rownum || ';'  from (
    //  select column_name from dba_tab_cols where table_name like 'GV_$TRANSACTION' order by column_id )
    static final int INST_ID = 0;
    static final int ADDR = 1;
    static final int SES_ADDR = 2;
    static final int USED_UBLK = 3;
    static final int USED_UREC = 4;
    static final int LOG_IO = 5;
    static final int PHY_IO = 6;
    static final int CR_GET = 7;
    static final int CR_CHANGE = 8;
    static final int START_DATE = 9;
    //  select ', "' ||  column_name || '"'  from (
    //  select column_name from dba_tab_cols where table_name like 'GV_$TRANSACTION' order by column_id )
    static final String[] columnNames = {
            "INST_ID"
            , "ADDR"
            , "SES_ADDR"
            , "USED_UBLK"
            , "USED_UREC"
            , "LOG_IO"
            , "PHY_IO"
            , "CR_GET"
            , "CR_CHANGE"
            , "START_DATE"
    };

    //
    public static String getColumnName(int columnPosition) {
        return columnNames[columnPosition];
    }

    //  select 'private String ' ||  replace(replace( substr(lower(column_name),1,1) || substr(initcap(column_name),2), '_',''), '#','') || ';            // '
    //  ||  data_type from dba_tab_cols where table_name like 'GV_$TRANSACTION' order by column_id
    private String instId;            // NUMBER
    private String addr;            // RAW
    private GvSession gvSession;
    //  private String xidusn;            // NUMBER
    //  private String xidslot;            // NUMBER
    //  private String xidsqn;            // NUMBER
    //  private String ubafil;            // NUMBER
    //  private String ubablk;            // NUMBER
    //  private String ubasqn;            // NUMBER
    //  private String ubarec;            // NUMBER
    //  private String status;            // VARCHAR2
    //  private String startTime;            // VARCHAR2
    //  private String startScnb;            // NUMBER
    //  private String startScnw;            // NUMBER
    //  private String startUext;            // NUMBER
    //  private String startUbafil;            // NUMBER
    //  private String startUbablk;            // NUMBER
    //  private String startUbasqn;            // NUMBER
    //  private String startUbarec;            // NUMBER
    private String sesAddr;            // RAW
    //  private String flag;            // NUMBER
    //  private String space;            // VARCHAR2
    //  private String recursive;            // VARCHAR2
    //  private String noundo;            // VARCHAR2
    //  private String ptx;            // VARCHAR2
    //  private String name;            // VARCHAR2
    //  private String prvXidusn;            // NUMBER
    //  private String prvXidslt;            // NUMBER
    //  private String prvXidsqn;            // NUMBER
    //  private String ptxXidusn;            // NUMBER
    //  private String ptxXidslt;            // NUMBER
    //  private String ptxXidsqn;            // NUMBER
    //  private String dscn-B;            // NUMBER
    //  private String dscn-W;            // NUMBER
    private BigDecimal usedUblk;            // NUMBER
    private BigDecimal usedUrec;            // NUMBER
    private BigDecimal logIo;            // NUMBER
    private BigDecimal phyIo;            // NUMBER
    private BigDecimal crGet;            // NUMBER
    private BigDecimal crChange;            // NUMBER
    //  private String startDate;            // DATE
    private int startDate;            // DATE
    //  private String dscnBase;            // NUMBER
    //  private String dscnWrap;            // NUMBER
    //  private String startScn;            // NUMBER
    //  private String dependentScn;            // NUMBER
    //  private String xid;            // RAW
    //  private String prvXid;            // RAW
    //  private String ptxXid;            // RAW

    //
    //
    String getInstId() {
        return instId;
    }

    void setInstId(String instId) {
        this.instId = instId;
    }

    String getAddr() {
        return addr;
    }

    void setAddr(String addr) {
        this.addr = addr;
    }

    public void setGvSession(GvSession gvSession) {
        this.gvSession = gvSession;
    }

    public GvSession getGvSession() {
        return gvSession;
    }

    public String getSesAddr() {
        return sesAddr;
    }

    void setSesAddr(String sesAddr) {
        this.sesAddr = sesAddr;
    }

    public BigDecimal getUsedUblk() {
        return usedUblk;
    }

    void setUsedUblk(BigDecimal usedUblk) {
        this.usedUblk = usedUblk;
    }

    public BigDecimal getUsedUrec() {
        return usedUrec;
    }

    void setUsedUrec(BigDecimal usedUrec) {
        this.usedUrec = usedUrec;
    }

    public BigDecimal getLogIo() {
        return logIo;
    }

    void setLogIo(BigDecimal logIo) {
        this.logIo = logIo;
    }

    public BigDecimal getPhyIo() {
        return phyIo;
    }

    void setPhyIo(BigDecimal phyIo) {
        this.phyIo = phyIo;
    }

    public BigDecimal getCrGet() {
        return crGet;
    }

    void setCrGet(BigDecimal crGet) {
        this.crGet = crGet;
    }

    public BigDecimal getCrChange() {
        return crChange;
    }

    void setCrChange(BigDecimal crChange) {
        this.crChange = crChange;
    }

    public int getStartDate() {
        return startDate;
    }

    void setStartDate(int startDate) {
        this.startDate = startDate;
    }

    //
    //  select 'set' ||  replace(initcap( column_name),'_','') || '(rs.getString(getColumnName('||column_name||')));'  from (
    //  select column_name from dba_tab_cols where table_name like 'GV_$TRANSACTION' order by column_id )
    public GvTransaction(ResultSet rs) throws Exception {
        setInstId(rs.getString(getColumnName(INST_ID)));
        setAddr(rs.getString(getColumnName(ADDR)));
        setSesAddr(rs.getString(getColumnName(SES_ADDR)));
        setUsedUblk(rs.getBigDecimal(getColumnName(USED_UBLK)));
        setUsedUrec(rs.getBigDecimal(getColumnName(USED_UREC)));
        setLogIo(rs.getBigDecimal(getColumnName(LOG_IO)));
        setPhyIo(rs.getBigDecimal(getColumnName(PHY_IO)));
        setCrGet(rs.getBigDecimal(getColumnName(CR_GET)));
        setCrChange(rs.getBigDecimal(getColumnName(CR_CHANGE)));
        setStartDate(rs.getInt(getColumnName(START_DATE)));
    }

    public String getPrimaryKey() {
        return getAddr() + "@" + getInstId();
    }
}
