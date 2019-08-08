package database;

import common.CommandLineArgument;
import common.Log;
import common.Str;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 *
 */
class GvSessions {
    // map sorted by primary key sid,serial#,@inst_id
    private static final SortedMap<String, GvSession> sessionsMapByPrimaryKey = new TreeMap<>();
    // map sorted by short primary key sid,@inst_id
    private static final SortedMap<String, GvSession> sessionsMapByPrimaryKeyShort = new TreeMap<>();
    // map sorted by SADDR@inst_id
    private static final SortedMap<String, GvSession> sessionsMapBySAddr = new TreeMap<>();

    // Set sorted by last_call_et
    private static final SortedSet<GvSession> sessionsSetByLastCallEtDesc = new TreeSet<>(new ComparatorLastCallEtDesc());

    //
    private static long fetchTime;
    private static long gvPxSessionsFetchTime;

    //
    public static void setFetchTime(long fetchTime) {
        GvSessions.fetchTime = fetchTime;
    }

    private static long getFetchTime() {
        return fetchTime;
    }

    public static SortedMap<String, GvSession> getSessionsMapByPrimaryKey() {
        return sessionsMapByPrimaryKey;
    }

    public static SortedMap<String, GvSession> getSessionsMapBySAddr() {
        return sessionsMapBySAddr;
    }

    //
    public static void clear() {
        sessionsMapByPrimaryKey.clear();
        sessionsMapByPrimaryKeyShort.clear();
        sessionsSetByLastCallEtDesc.clear();
        sessionsMapBySAddr.clear();
        //
    }

    //
    public static void addSession(ResultSet rs) throws Exception {
        //
        GvSession session = new GvSession(rs);
        GvSession oldSession;
        //
        String key = session.getPrimaryKey();
        oldSession = sessionsMapByPrimaryKey.put(key, session);
        if (oldSession != null) {
            Log.println(session.getPrimaryKey());
            Log.println(oldSession.getPrimaryKey());
            throw new RuntimeException("duplicate value");
        }
        //
        String keyShort = session.getPrimaryKeyShort();
        oldSession = sessionsMapByPrimaryKeyShort.put(keyShort, session);
        if (oldSession != null) {
            Log.println(session.getPrimaryKey());
            Log.println(oldSession.getPrimaryKey());
            throw new RuntimeException("duplicate value");
        }

        //
        if (!sessionsSetByLastCallEtDesc.add(session)) {
            throw new RuntimeException("duplicate value");
        }

        //
        String saddrKey = session.getPrimaryKeySaddr();
        oldSession = sessionsMapBySAddr.put(saddrKey, session);
        if (oldSession != null) {
            Log.println(session.getPrimaryKey());
            Log.println(oldSession.getPrimaryKey());
            throw new RuntimeException("duplicate value");
        }
    }

    //
    public static void getActiveSessions(StringBuffer sb) {
        //
        int maxActiveSessionsRows = CommandLineArgument.getMaxActiveSessionsRows();
        if (maxActiveSessionsRows <= 0)
            return;

        //
        List<GvSession> sessionList = new ArrayList<>(10);
        //
        for (GvSession session : sessionsSetByLastCallEtDesc) {
            // skip own session
            if (session.isMyOwnMonitoringSession())
                continue;
                // parallel query coordinator with at least one active PQ slave
            else if (session.getParallelQueryCoordinatorSlaveCount() > 0 && session.getParallelQueryCoordinatorActiveSlaveCount() > 0)
                sessionList.add(session);
                // active, non-idle wait class, not parallel query slave
            else if (session.getStatusIsActive() && !session.getWaitClass().equals(GvSession.WAIT_CLASS_IDLE) && !session.isParallelQuerySlave())
                sessionList.add(session);
            else
                /* do nothing */ ;
        }

        //
        // Math.min() allows to restrict # of lines if there are too many
        int rowCount = 1 + Math.min(sessionList.size(), maxActiveSessionsRows);
        int columnCount = 13;
        int[] alignment = new int[columnCount];
        //
        int colNum = 0;
        int rowNum = 0;
        String[][] sessionArray = new String[columnCount][rowCount];

        // Column Names
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        sessionArray[colNum++][rowNum] = "SES_REF";
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        sessionArray[colNum++][rowNum] = GvSession.columnNames[GvSession.USERNAME] + "(PQ)";
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        sessionArray[colNum++][rowNum] = GvSession.columnNames[GvSession.OSUSER];
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        sessionArray[colNum++][rowNum] = GvSession.columnNames[GvSession.MACHINE];
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        sessionArray[colNum++][rowNum] = GvSession.columnNames[GvSession.PROGRAM];
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        sessionArray[colNum++][rowNum] = GvSession.columnNames[GvSession.SQL_ID];
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        sessionArray[colNum++][rowNum] = GvSession.columnNames[GvSession.MODULE];
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        sessionArray[colNum++][rowNum] = GvSession.columnNames[GvSession.ACTION];
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        sessionArray[colNum++][rowNum] = GvSession.columnNames[GvSession.LOGON_TIME];
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        sessionArray[colNum++][rowNum] = "LAST_CALL";
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        sessionArray[colNum++][rowNum] = "IN_WAIT";
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        sessionArray[colNum++][rowNum] = "RIO/WIO/REDO";
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        sessionArray[colNum++][rowNum] = GvSession.columnNames[GvSession.EVENT];

        // Column Values
        for (int i = 0; i < (rowCount - 1); i++) {
            GvSession session = sessionList.get(i);
            colNum = 0;
            rowNum = i + 1;
            sessionArray[colNum++][rowNum] = session.getAlterSystemKillSessionReference();
            sessionArray[colNum++][rowNum] = session.getUsername();
            sessionArray[colNum++][rowNum] = Str.rtrunc(session.getOsuser(), 15);
            sessionArray[colNum++][rowNum] = Str.rtrunc(session.getMachine(), 15);
            sessionArray[colNum++][rowNum] = Str.rtrunc(session.getProgram(), 15);
            sessionArray[colNum++][rowNum] = session.getSqlId();
            sessionArray[colNum++][rowNum] = Str.rtrunc(session.getModule(), 15);
            sessionArray[colNum++][rowNum] = Str.rtrunc(session.getAction(), 15);
            sessionArray[colNum++][rowNum] = Str.formatSecondsNumber(session.getLogonTime());
            sessionArray[colNum++][rowNum] = Str.formatSecondsNumber(session.getLastCallEt());
            sessionArray[colNum++][rowNum] = Str.formatSecondsNumber(session.getSecondsInWait());
            sessionArray[colNum++][rowNum] = GvSesstats.getSessionIOStats(session);
            sessionArray[colNum++][rowNum] = Str.rtrunc(session.getEvent(), 30);
        }

        //
//    sb.append("\n\r");
        sb.append("Active Sessions: " + (sessionList.size())
                + ((maxActiveSessionsRows == Integer.MAX_VALUE) ? "" : " limit: " + maxActiveSessionsRows)
                + " / session,px_session,sesstat"
                + " (" + getFetchTime() + "ms"
                + "," + getGvPxSessionsFetchTime() + "ms"
                + "," + GvSesstats.getGvSesstatFetchTime() + "ms"
                + ") / Total sessions: " + sessionsMapByPrimaryKey.size()
                + "\n\r");
        Str.convertArrayToStringBufferAsTable(sessionArray, alignment, sb);
        sb.append("\n\r");
    }


    //
    public static void postLoadCompletedTasks() {
        //
        // Build blocking tree
        for (GvSession session : sessionsMapByPrimaryKey.values()) {
            //
            if (session.getBlockingInstance() != null) {
                //
                String blokingKeyShort = GvSession.getPrimaryKeyShort(session.getBlockingSession(), session.getBlockingInstance());
                GvSession blockingSession = sessionsMapByPrimaryKeyShort.get(blokingKeyShort);

                //
                if (blockingSession != null) {
                    // don't include parallel query Coordinator / Slave relations (Slave blocks on coordinator)
                    if (session.isParallelQuerySlave()
                            && session.getParallelQueryCoordinatorRef() != null
                            && session.getParallelQueryCoordinatorRef().getPrimaryKey().equals(blockingSession.getPrimaryKey())) {
                        //
                        continue;
                    }
                    // don't include parallel query Slave / Slave relations if the Coordinator is the same
                    else if (session.isParallelQuerySlave() && blockingSession.isParallelQuerySlave()
                            && session.getParallelQueryCoordinatorRef() != null && blockingSession.getParallelQueryCoordinatorRef() != null
                            && session.getParallelQueryCoordinatorRef().getPrimaryKey().equals(blockingSession.getParallelQueryCoordinatorRef().getPrimaryKey())) {
                        //
                        continue;
                    }
                    //
                    session.setBlockingGvSession(blockingSession);
                    blockingSession.addBlockingGvSessionList(session);
                }
            }
        }
    }

    //
    public static void getBlockingTree(StringBuffer sb) {
//    sb.append("\n\r");
        sb.append("Blocking Sessions / gv$session\n\r");
        //
        GvSession.printBlockingTreeRowHeader(sb);
        //
        for (GvSession session : sessionsMapByPrimaryKey.values()) {
            // start printing from sessions that block other sessions but are not blocked by others
            if (session.getBlockingGvSessionList().size() > 0 && session.getBlockingGvSession() == null) {
                session.printBlockingTree(sb, 0);
            }
        }

        //
//    Str.convertArrayToStringBufferAsTable(sessionArray, alignment, sb);
        sb.append("\n\r");
    }

    //
    //
    //
    private static class ComparatorLastCallEtDesc implements Comparator<GvSession> {
        //
        public int compare(GvSession o1, GvSession o2) {
            int result = compareTo(o2.getLastCallEt(), o1.getLastCallEt());
            //
            if (result != 0)
                return result;
            else
                return o2.getPrimaryKey().compareTo(o1.getPrimaryKey());
        }

        //
        private static int compareTo(long o1, long o2) {
            if (o1 > o2) return 1;
            else if (o1 < o2) return -1;
            else return 0;
        }

    }

    //
    //
    //
    enum PX_SESSION_COLUMNS {
        INST_ID("INST_ID"), SID("SID"), SERIAL("SERIAL#") //
        , QCINST_ID("QCINST_ID"), QCSID("QCSID"), QCSERIAL("QCSERIAL#") //
        , SERVER_SET("SERVER_SET");
        //
        private String columnName;

        public String getColumnName() {
            return columnName;
        }

        PX_SESSION_COLUMNS(String columnName) {
            this.columnName = columnName;
        }
    }

    public static void setGvPxSessionsFetchTime(long gvPxSessionsFetchTime) {
        GvSessions.gvPxSessionsFetchTime = gvPxSessionsFetchTime;
    }

    public static long getGvPxSessionsFetchTime() {
        return gvPxSessionsFetchTime;
    }

    //
    public static void addPxSession(ResultSet rs) throws SQLException {
        String inst_id = rs.getString(PX_SESSION_COLUMNS.INST_ID.getColumnName());
        String sid = rs.getString(PX_SESSION_COLUMNS.SID.getColumnName());
        String serial = rs.getString(PX_SESSION_COLUMNS.SERIAL.getColumnName());
        String qcinst_id = rs.getString(PX_SESSION_COLUMNS.QCINST_ID.getColumnName());
        String qcsid = rs.getString(PX_SESSION_COLUMNS.QCSID.getColumnName());
        String qcserial = rs.getString(PX_SESSION_COLUMNS.QCSERIAL.getColumnName());
        String server_set = rs.getString(PX_SESSION_COLUMNS.SERVER_SET.getColumnName());

        //
        if (inst_id == null || sid == null || serial == null
                || qcinst_id == null || qcsid == null || qcserial == null)
            return;

        //
        String slavePk = GvSession.getPrimaryKey(sid, serial, inst_id);
        String coordinatorPk = GvSession.getPrimaryKey(qcsid, qcserial, qcinst_id);

        //
        GvSession querySlave = sessionsMapByPrimaryKey.get(slavePk);
        GvSession queryCoordinator = sessionsMapByPrimaryKey.get(coordinatorPk);

        //
        if (querySlave == null) {
            Log.println("Warning, PX_SESSION, Slave session not found for: " + slavePk);
            return;
        }

        //
        if (queryCoordinator == null) {
            Log.println("Warning, PX_SESSION, Coordinator session not found for: " + coordinatorPk);
            return;
        }

        //
        querySlave.setParallelQuerySlave(true);
        querySlave.setParallelQuerySlave_ServerSet(server_set);
        querySlave.setParallelQueryCoordinatorRef(queryCoordinator);
        //
        queryCoordinator.incrementParallelQueryCoordinatorSlaveCount();
        queryCoordinator.addParallelQuerySlaveList(querySlave);

    }
}
