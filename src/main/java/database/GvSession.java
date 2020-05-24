package database;

import common.Str;
import telnetServer.TelnetSession;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class GvSession {

    public static final String WAIT_CLASS_IDLE = "Idle";

    //
    private static final int INST_ID = 0;
    private static final int SADDR = 1;
    private static final int SID = 2;
    private static final int SERIAL = 3;
    static final int USERNAME = 4;
    private static final int STATUS = 5;
    static final int OSUSER = 6;
    static final int MACHINE = 7;
    static final int TERMINAL = 8;
    static final int PROGRAM = 9;
    static final int TYPE = 10;
    static final int SQL_ID = 11;
    static final int MODULE = 12;
    static final int ACTION = 13;
    static final int LOGON_TIME = 14;
    static final int LAST_CALL_ET = 15;
    static final int EVENT = 16;
    private static final int WAIT_CLASS = 17;
    static final int SECONDS_IN_WAIT = 18;
    static final int STATE = 19;
    private static final int BLOCKING_INSTANCE = 20;
    private static final int BLOCKING_SESSION = 21;

    //
    static final String[] columnNames = {
            "INST_ID"
            , "SADDR"
            , "SID"
            , "SERIAL#"
            , "USERNAME"
            , "STATUS"
            , "OSUSER"
            , "MACHINE"
            , "TERMINAL"
            , "PROGRAM"
            , "TYPE"
            , "SQL_ID"
            , "MODULE"
            , "ACTION"
            , "LOGON_TIME"
            , "LAST_CALL_ET"
            , "EVENT"
            , "WAIT_CLASS"
            , "SECONDS_IN_WAIT"
            , "STATE"
            , "BLOCKING_INSTANCE"
            , "BLOCKING_SESSION"
    };

    //
    public static String getColumnName(int columnPosition) {
        return columnNames[columnPosition];
    }

    //  select 'private String ' ||  replace(replace( substr(lower(column_name),1,1) || substr(initcap(column_name),2), '_',''), '#','') || ';            // '
//  ||  data_type from dba_tab_cols where table_name like 'GV_$SESSION' order by column_id

    private String instId;            // NUMBER
    private String saddr;            // RAW
    private String sid;            // NUMBER
    private String serial;            // NUMBER
    //  private String audsid;            // NUMBER
//  private String paddr;            // RAW
//  private String user;            // NUMBER
    private String username;            // VARCHAR2
    //  private String command;            // NUMBER
//  private String ownerid;            // NUMBER
//  private String taddr;            // VARCHAR2
//  private String lockwait;            // VARCHAR2
    private String status;            // VARCHAR2
    //  private String server;            // VARCHAR2
//  private String schema;            // NUMBER
//  private String schemaname;            // VARCHAR2
    private String osuser;            // VARCHAR2
    //  private String process;            // VARCHAR2
    private String machine;            // VARCHAR2
    //  private String port;            // NUMBER
    private String terminal;            // VARCHAR2
    private String program;            // VARCHAR2
    private String type;            // VARCHAR2
    //  private String sqlAddress;            // RAW
//  private String sqlHashValue;            // NUMBER
    private String sqlId;            // VARCHAR2
    //  private String sqlChildNumber;            // NUMBER
//  private String sqlExecStart;            // DATE
//  private String sqlExecId;            // NUMBER
//  private String prevSqlAddr;            // RAW
//  private String prevHashValue;            // NUMBER
//  private String prevSqlId;            // VARCHAR2
//  private String prevChildNumber;            // NUMBER
//  private String prevExecStart;            // DATE
//  private String prevExecId;            // NUMBER
//  private String plsqlEntryObjectId;            // NUMBER
//  private String plsqlEntrySubprogramId;            // NUMBER
//  private String plsqlObjectId;            // NUMBER
//  private String plsqlSubprogramId;            // NUMBER
    private String module;            // VARCHAR2
    //  private String moduleHash;            // NUMBER
    private String action;            // VARCHAR2
    //  private String actionHash;            // NUMBER
//  private String clientInfo;            // VARCHAR2
//  private String fixedTableSequence;            // NUMBER
//  private String rowWaitObj;            // NUMBER
//  private String rowWaitFile;            // NUMBER
//  private String rowWaitBlock;            // NUMBER
//  private String rowWaitRow;            // NUMBER
//  private String topLevelCall;            // NUMBER
//  private String logonTime;            // DATE
    private Timestamp logonTime;            // DATE
    private long lastCallEt;            // NUMBER
    //  private String pdmlEnabled;            // VARCHAR2
//  private String failoverType;            // VARCHAR2
//  private String failoverMethod;            // VARCHAR2
//  private String failedOver;            // VARCHAR2
//  private String resourceConsumerGroup;            // VARCHAR2
//  private String pdmlStatus;            // VARCHAR2
//  private String pddlStatus;            // VARCHAR2
//  private String pqStatus;            // VARCHAR2
//  private String currentQueueDuration;            // NUMBER
//  private String clientIdentifier;            // VARCHAR2
//  private String blockingSessionStatus;            // VARCHAR2
    private String blockingInstance;            // NUMBER
    private String blockingSession;            // NUMBER

    // this session is blocked by the following session
    private GvSession blockingGvSession;
    // the following sessions are blocked by this session
    private final List<GvSession> blockingGvSessionList = new ArrayList<>();

    //  private String finalBlockingSessionStatus;            // VARCHAR2
//  private String finalBlockingInstance;            // NUMBER
//  private String finalBlockingSession;            // NUMBER
//  private String seq;            // NUMBER
//  private String event;            // NUMBER
    private String event;            // VARCHAR2
    //  private String p1text;            // VARCHAR2
//  private String p1;            // NUMBER
//  private String p1raw;            // RAW
//  private String p2text;            // VARCHAR2
//  private String p2;            // NUMBER
//  private String p2raw;            // RAW
//  private String p3text;            // VARCHAR2
//  private String p3;            // NUMBER
//  private String p3raw;            // RAW
//  private String waitClassId;            // NUMBER
//  private String waitClass;            // NUMBER
    private String waitClass;            // VARCHAR2
    //  private String waitTime;            // NUMBER
    private long secondsInWait;            // NUMBER
    private String state;            // VARCHAR2
//  private String waitTimeMicro;            // NUMBER
//  private String timeRemainingMicro;            // NUMBER
//  private String timeSinceLastWaitMicro;            // NUMBER
//  private String serviceName;            // VARCHAR2
//  private String sqlTrace;            // VARCHAR2
//  private String sqlTraceWaits;            // VARCHAR2
//  private String sqlTraceBinds;            // VARCHAR2
//  private String sqlTracePlanStats;            // VARCHAR2
//  private String sessionEditionId;            // NUMBER
//  private String creatorAddr;            // RAW
//  private String creatorSerial;            // NUMBER
//  private String ecid;            // VARCHAR2

    // flag: Session is Parallel Query Slave
    private boolean isParallelQuerySlave = false;
    // gv$px_session.server_set
    private String parallelQuerySlave_ServerSet;
    // if parallel Query Slave then Query Coordinator is saved here
    private GvSession parallelQueryCoordinatorRef;

    // counter: if Session is Parallel Query Coordinator, then count Slaves here
    private int parallelQueryCoordinatorSlaveCount = 0;
    // if parallel Query Coordinator then Slave list is saved here
    private List<GvSession> parallelQuerySlaveList;

    //
    // Getters & Setters
    //
    String getInstId() {
        return instId;
    }

    void setInstId(String instId) {
        this.instId = instId;
    }

    public String getSaddr() {
        return saddr;
    }

    void setSaddr(String saddr) {
        this.saddr = saddr;
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

    public String getUsername() {
        String retVal;
        //
        if (username == null && getType().equals("BACKGROUND"))
            retVal = getProgram();
        else
            retVal = username;

        //
        if (isParallelQuerySlave())
            return retVal + "(" + getParallelQueryCoordinatorRef().getAlterSystemKillSessionReference() + ":" + getParallelQuerySlave_ServerSet() + ")";
        else if (getParallelQueryCoordinatorSlaveCount() > 0)
            return retVal + "(QC:" + getParallelQueryCoordinatorSlaveCount() + ":" + getParallelQueryCoordinatorActiveSlaveCount() + ")";
        else
            return retVal;
    }

    void setUsername(String username) {
        this.username = username;
    }

    public boolean getStatusIsActive() {
        return getStatus().equals("ACTIVE");
    }

    public String getStatus() {
        return status;
    }

    void setStatus(String status) {
        this.status = status;
    }

    String getOsuser() {
        return osuser;
    }

    void setOsuser(String osuser) {
        this.osuser = osuser;
    }

    public String getMachine() {
        return machine;
    }

    void setMachine(String machine) {
        this.machine = machine;
    }

    public String getTerminal() {
        return terminal;
    }

    void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getProgram() {
        //
        if (program == null)
            return null;
        //
        String prefix = "oracle@" + getMachine();
        if (program.startsWith(prefix))
            return program.substring(prefix.length()).trim();
        else
            return program;
    }

    void setProgram(String program) {
        this.program = program;
    }

    String getType() {
        return type;
    }

    void setType(String type) {
        this.type = type;
    }

    public String getSqlId() {
        return sqlId;
    }

    void setSqlId(String sqlId) {
        this.sqlId = sqlId;
    }

    public String getModule() {
        return module;
    }

    void setModule(String module) {
        this.module = module;
    }

    public String getAction() {
        return action;
    }

    void setAction(String action) {
        this.action = action;
    }

    public Timestamp getLogonTime() {
        return logonTime;
    }

    public long getLogonTimeSeconds() {
        return Duration.between(logonTime.toInstant(), MySession.getSysdate().toInstant()).getSeconds();
    }

    void setLogonTime(Timestamp logonTime) {
        this.logonTime = logonTime;
    }

    public long getLastCallEt() {
        return lastCallEt;
    }

    void setLastCallEt(long lastCallEt) {
        this.lastCallEt = lastCallEt;
    }

    public String getBlockingInstance() {
        return blockingInstance;
    }

    void setBlockingInstance(String blockingInstance) {
        this.blockingInstance = blockingInstance;
    }

    public String getBlockingSession() {
        return blockingSession;
    }

    void setBlockingSession(String blockingSession) {
        this.blockingSession = blockingSession;
    }

    public String getEvent() {
        if (getStatusIsActive()) {
            if (getState().equals("WAITING"))
                return event;
            else
                return "CPU:" + event;
        } else
            return "Idle:" + event;
    }

    void setEvent(String event) {
        this.event = event;
    }

    public String getWaitClass() {
        return waitClass;
    }

    void setWaitClass(String waitClass) {
        this.waitClass = waitClass;
    }

    public long getSecondsInWait() {
        return secondsInWait;
    }

    public void setSecondsInWait(long secondsInWait) {
        this.secondsInWait = secondsInWait;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public GvSession getBlockingGvSession() {
        return blockingGvSession;
    }

    public void setBlockingGvSession(GvSession blockingGvSession) {
        this.blockingGvSession = blockingGvSession;
    }

    public List<GvSession> getBlockingGvSessionList() {
        return blockingGvSessionList;
    }

    public void addBlockingGvSessionList(GvSession blockingGvSession) {
        this.blockingGvSessionList.add(blockingGvSession);
    }

    public boolean isParallelQuerySlave() {
        return isParallelQuerySlave;
    }

    public void setParallelQuerySlave(boolean parallelQuerySlave) {
        isParallelQuerySlave = parallelQuerySlave;
    }

    public void setParallelQuerySlave_ServerSet(String parallelQuerySlave_ServerSet) {
        this.parallelQuerySlave_ServerSet = parallelQuerySlave_ServerSet;
    }

    public String getParallelQuerySlave_ServerSet() {
        return parallelQuerySlave_ServerSet;
    }

    public GvSession getParallelQueryCoordinatorRef() {
        return parallelQueryCoordinatorRef;
    }

    public void setParallelQueryCoordinatorRef(GvSession parallelQueryCoordinatorRef) {
        this.parallelQueryCoordinatorRef = parallelQueryCoordinatorRef;
    }

    public int getParallelQueryCoordinatorSlaveCount() {
        return parallelQueryCoordinatorSlaveCount;
    }

    public void incrementParallelQueryCoordinatorSlaveCount() {
        this.parallelQueryCoordinatorSlaveCount++;
    }

    public List<GvSession> getParallelQuerySlaveList() {
        return parallelQuerySlaveList;
    }

    public void addParallelQuerySlaveList(GvSession parallelQuerySlave) {
        //
        if (parallelQuerySlaveList == null)
            parallelQuerySlaveList = new ArrayList<>();
        //
        this.parallelQuerySlaveList.add(parallelQuerySlave);
    }

    //
    //
    //
    public GvSession(ResultSet rs) throws Exception {
        setInstId(rs.getString(getColumnName(INST_ID)));
        setSaddr(rs.getString(getColumnName(SADDR)));
        setSid(rs.getString(getColumnName(SID)));
        setSerial(rs.getString(getColumnName(SERIAL)));
        setUsername(rs.getString(getColumnName(USERNAME)));
        setStatus(rs.getString(getColumnName(STATUS)));
        setOsuser(rs.getString(getColumnName(OSUSER)));
        setMachine(rs.getString(getColumnName(MACHINE)));
        setTerminal(rs.getString(getColumnName(TERMINAL)));
        setProgram(rs.getString(getColumnName(PROGRAM)));
        setType(rs.getString(getColumnName(TYPE)));
        setSqlId(rs.getString(getColumnName(SQL_ID)));
        setModule(rs.getString(getColumnName(MODULE)));
        setAction(rs.getString(getColumnName(ACTION)));
        setLogonTime(rs.getTimestamp(getColumnName(LOGON_TIME)));
        setLastCallEt(rs.getLong(getColumnName(LAST_CALL_ET)));
        setEvent(rs.getString(getColumnName(EVENT)));
        setWaitClass(rs.getString(getColumnName(WAIT_CLASS)));
        setSecondsInWait(rs.getLong(getColumnName(SECONDS_IN_WAIT)));
        setState(rs.getString(getColumnName(STATE)));
        setBlockingInstance(rs.getString(getColumnName(BLOCKING_INSTANCE)));
        setBlockingSession(rs.getString(getColumnName(BLOCKING_SESSION)));
    }


    //
    public void printBlockingTree(StringBuffer sb, int level) {
        String paddingText = "   ";
        String padding = "";
        for (int i = 0; i < level; i++)
            padding += paddingText;

        //
        if (level == 0)
            sb.append(TelnetSession.DARK_RED);
        else {
            sb.append(TelnetSession.YELLOW);
            sb.append(padding).append("-> ");
        }
        //
        printBlockingTreeRow(this, sb);
        //
        sb.append(TelnetSession.COLOR_RESET);
        //
        for (GvSession next : getBlockingGvSessionList()) {
            if (next.getBlockingGvSessionList().size() > 0) {
                next.printBlockingTree(sb, level + 1);
            } else {
                sb.append(TelnetSession.YELLOW);
                sb.append(paddingText).append(padding).append("-> ");
                printBlockingTreeRow(next, sb);
                sb.append(TelnetSession.COLOR_RESET);
            }
        }
    }

    //
    public static void printBlockingTreeRowHeader(StringBuffer sb) {
        sb.append(TelnetSession.GREEN);
        sb.append("SES_REF | USERNAME | LAST_CALL_ET | SQL_ID | SECONDS_IN_WAIT | EVENT");
        sb.append("\n\r");
        sb.append(TelnetSession.COLOR_RESET);
    }

    //
    private void printBlockingTreeRow(GvSession session, StringBuffer sb) {
        sb.append(session.getAlterSystemKillSessionReference())
                .append(" | ").append(session.getUsername())
                .append(" | ").append(session.getLastCallEt())
                .append(" | ").append(session.getSqlId())
                .append(" | ").append(Str.formatSecondsNumber(session.getSecondsInWait()))
                .append(" | ").append(session.getEvent())
                .append("\n\r");
    }


    //
    public String getPrimaryKeyShort() {
        return getPrimaryKeyShort(getSid(), getInstId());
    }

    //
    public static String getPrimaryKeyShort(String sid, String instId) {
        return sid + ",@" + instId;
    }

    //
    public String getPrimaryKeySaddr() {
        return getPrimaryKeySaddr(getSaddr(), getInstId());
    }

    //
    public static String getPrimaryKeySaddr(String saddr, String instId) {
        return saddr + ",@" + instId;
    }

    //
    public String getPrimaryKey() {
        return getPrimaryKey(getSid(), getSerial(), getInstId());
    }

    //
    public static String getPrimaryKey(String sid, String serial, String instId) {
        return sid + "," + serial + ",@" + instId;
    }

    //
    public String getAlterSystemKillSessionReference() {
//    return "'" + getPrimaryKey() + "'";
        return getPrimaryKey();
    }

    //
    public boolean isMyOwnMonitoringSession() {
        return (getSid().equals(MySession.getSid()) && getInstId().equals(MySession.getInstId()));
    }

    public int getParallelQueryCoordinatorActiveSlaveCount() {
        int activeCount = 0;
        //
        for (GvSession session : parallelQuerySlaveList) {
            if (session.getStatusIsActive() && !session.getWaitClass().equals(WAIT_CLASS_IDLE))
                activeCount++;
        }
        //
        return activeCount;
    }
}
