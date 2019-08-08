package database;

import common.Str;

import java.math.BigDecimal;
import java.sql.ResultSet;

/**
 *
 */
class GvSessionLongop {
    //  select 'static final int ' ||  column_name || ' = ' || rownum || ';'  from (
//   select column_name from dba_tab_cols where table_name like 'GV_$SESSION_LONGOPS' order by column_id )
    enum COLUMNS {
        ROWNUM_FOR_PK("ROWNUM_FOR_PK") //
        , INST_ID("INST_ID"), SID("SID"), SERIAL("SERIAL#"), OPNAME("OPNAME"), TARGET("TARGET")
        //  TARGET_DESC
        , SOFAR("SOFAR"), TOTALWORK("TOTALWORK"), UNITS("UNITS"), START_TIME("START_TIME")
        //  LAST_UPDATE_TIME, TIMESTAMP
        , TIME_REMAINING("TIME_REMAINING")
        // ELAPSED_SECONDS, CONTEXT, MESSAGE, USERNAME, SQL_ADDRESS, SQL_HASH_VALUE
        , SQL_ID("SQL_ID")
        // SQL_PLAN_HASH_VALUE,  SQL_EXEC_START, SQL_EXEC_ID, SQL_PLAN_LINE_ID, SQL_PLAN_OPERATION, SQL_PLAN_OPTIONS, QCSID
        ;

        //
        private String columnName;

        public String getColumnName() {
            return columnName;
        }

        private COLUMNS(String columnName) {
            this.columnName = columnName;
        }
    }

    //
    //  select 'private String ' ||  replace(replace( substr(lower(column_name),1,1) || substr(initcap(column_name),2), '_',''), '#','') || ';            // '
//    ||  data_type from dba_tab_cols where table_name like 'GV_$SESSION_LONGOPS' order by column_id
    private int rownumForPk;            // NUMBER
    private String instId;            // NUMBER
    private String sid;            // NUMBER
    private String serial;            // NUMBER
    private String opname;            // VARCHAR2
    private String target;            // VARCHAR2
    //  private String targetDesc;            // VARCHAR2
    private BigDecimal sofar;            // NUMBER
    private BigDecimal totalwork;            // NUMBER
    private String units;            // VARCHAR2
    //  private String startTime;            // DATE
    private int startTime;            // DATE
    //  private String lastUpdateTime;            // DATE
//  private String timestamp;            // DATE
    private long timeRemaining;            // NUMBER
    //  private String elapsedSeconds;            // NUMBER
//  private String context;            // NUMBER
//  private String message;            // VARCHAR2
//  private String username;            // VARCHAR2
//  private String sqlAddress;            // RAW
//  private String sqlHashValue;            // NUMBER
    private String sqlId;            // VARCHAR2
//  private String sqlPlanHashValue;            // NUMBER
//  private String sqlExecStart;            // DATE
//  private String sqlExecId;            // NUMBER
//  private String sqlPlanLineId;            // NUMBER
//  private String sqlPlanOperation;            // VARCHAR2
//  private String sqlPlanOptions;            // VARCHAR2
//  private String qcsid;            // NUMBER


    public int getRownumForPk() {
        return rownumForPk;
    }

    public void setRownumForPk(int rownumForPk) {
        this.rownumForPk = rownumForPk;
    }

    String getInstId() {
        return instId;
    }

    void setInstId(String instId) {
        this.instId = instId;
    }

    String getSid() {
        return sid;
    }

    void setSid(String sid) {
        this.sid = sid;
    }

    String getSerial() {
        return serial;
    }

    void setSerial(String serial) {
        this.serial = serial;
    }

    public String getOpname() {
        return opname;
    }

    void setOpname(String opname) {
        this.opname = opname;
    }

    public String getTarget() {
        return target;
    }

    void setTarget(String target) {
        this.target = target;
    }

    public BigDecimal getSofar() {
        return sofar;
    }

    void setSofar(BigDecimal sofar) {
        this.sofar = sofar;
    }

    public BigDecimal getTotalwork() {
        return totalwork;
    }

    void setTotalwork(BigDecimal totalwork) {
        this.totalwork = totalwork;
    }

    public String getUnits() {
        return units;
    }

    void setUnits(String units) {
        this.units = units;
    }

    public int getStartTime() {
        return startTime;
    }

    void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public long getTimeRemaining() {
        return timeRemaining;
    }

    void setTimeRemaining(long timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public String getSqlId() {
        return sqlId;
    }

    void setSqlId(String sqlId) {
        this.sqlId = sqlId;
    }

    //  select 'set' ||  replace(initcap( column_name),'_','') || '(rs.getString(getColumnName('||column_name||')));'  from (
//   select column_name from dba_tab_cols where table_name like 'GV_$SESSION_LONGOPS' order by column_id )
    public GvSessionLongop(ResultSet rs) throws Exception {
        setRownumForPk(rs.getInt(COLUMNS.ROWNUM_FOR_PK.getColumnName()));
        setInstId(rs.getString(COLUMNS.INST_ID.getColumnName()));
        setSid(rs.getString(COLUMNS.SID.getColumnName()));
        setSerial(rs.getString(COLUMNS.SERIAL.getColumnName()));
        setOpname(rs.getString(COLUMNS.OPNAME.getColumnName()));
        setTarget(rs.getString(COLUMNS.TARGET.getColumnName()));
//    setTargetDesc(rs.getString(getColumnName(TARGET_DESC)));
        setSofar(rs.getBigDecimal(COLUMNS.SOFAR.getColumnName()));
        setTotalwork(rs.getBigDecimal(COLUMNS.TOTALWORK.getColumnName()));
        setUnits(rs.getString(COLUMNS.UNITS.getColumnName()));
        setStartTime(rs.getInt(COLUMNS.START_TIME.getColumnName()));
//    setLastUpdateTime(rs.getString(getColumnName(LAST_UPDATE_TIME)));
//    setTimestamp(rs.getString(getColumnName(TIMESTAMP)));
        setTimeRemaining(rs.getLong(COLUMNS.TIME_REMAINING.getColumnName()));
//    setElapsedSeconds(rs.getString(getColumnName(ELAPSED_SECONDS)));
//    setContext(rs.getString(getColumnName(CONTEXT)));
//    setMessage(rs.getString(getColumnName(MESSAGE)));
//    setUsername(rs.getString(getColumnName(USERNAME)));
//    setSqlAddress(rs.getString(getColumnName(SQL_ADDRESS)));
//    setSqlHashValue(rs.getString(getColumnName(SQL_HASH_VALUE)));
        setSqlId(rs.getString(COLUMNS.SQL_ID.getColumnName()));
//    setSqlPlanHashValue(rs.getString(getColumnName(SQL_PLAN_HASH_VALUE)));
//    setSqlExecStart(rs.getString(getColumnName(SQL_EXEC_START)));
//    setSqlExecId(rs.getString(getColumnName(SQL_EXEC_ID)));
//    setSqlPlanLineId(rs.getString(getColumnName(SQL_PLAN_LINE_ID)));
//    setSqlPlanOperation(rs.getString(getColumnName(SQL_PLAN_OPERATION)));
//    setSqlPlanOptions(rs.getString(getColumnName(SQL_PLAN_OPTIONS)));
//    setQcsid(rs.getString(getColumnName(QCSID)));
    }

    public String getGvSessionPrimaryKey() {
        return getSid() + "," + getSerial() + ",@" + getInstId();
    }

    public String getPrimaryKey() {
        return getSid() + "," + getSerial() + ",@" + getInstId() + "," + getRownumForPk();
    }

    public String getSofarPercent() {
        if (getSofar() != null && getTotalwork() != null && getTotalwork().doubleValue() > 0d) {
            return Str.formatDoubleNumber(100 * getSofar().doubleValue() / getTotalwork().doubleValue()) + "%";
        } else
            return "?";
    }
}
