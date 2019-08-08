package database;

import common.CommandLineArgument;
import common.Str;

import java.sql.ResultSet;
import java.util.*;

/**
 *
 */
class GvSessionLongops {
    // map sorted by primary key sid,serial,@inst_id
    private static final SortedMap<String, GvSessionLongop> sessionLongopsMapByPrimaryKey = new TreeMap<>();

    // Set sorted by start_time
    private static final SortedSet<GvSessionLongop> sessionLongopsSetByStartTimeDesc = new TreeSet<>(new ComparatorStartTimeDesc());

    //
    private static long fetchTime;

    //
    public static void clear() {
        sessionLongopsMapByPrimaryKey.clear();
        sessionLongopsSetByStartTimeDesc.clear();
    }

    public static void addSessionLongop(ResultSet rs) throws Exception {
        //
        GvSessionLongop sessionLongop = new GvSessionLongop(rs);
        String key = sessionLongop.getPrimaryKey();
        GvSessionLongop oldSessionLongop = sessionLongopsMapByPrimaryKey.put(key, sessionLongop);
        if (oldSessionLongop != null)
            throw new RuntimeException("Bug, duplicate GvSessionLongop : " + oldSessionLongop.getPrimaryKey());

        //
        if (!sessionLongopsSetByStartTimeDesc.add(sessionLongop)) {
            throw new RuntimeException("duplicate value: " + sessionLongop.getPrimaryKey());
        }
    }

    public static void setFetchTime(long fetchTime) {
        GvSessionLongops.fetchTime = fetchTime;
    }

    private static long getFetchTime() {
        return fetchTime;
    }

    //
    public static void getOutputSessionLongops(StringBuffer sb) {
        //
        int maxLongOperationsRows = CommandLineArgument.getMaxLongOperationsRows();
        if (maxLongOperationsRows <= 0)
            return;

        //
        List<GvSessionLongop> sessionLongopList = new ArrayList<>(sessionLongopsSetByStartTimeDesc);

        //
        // Math.min() allows to restrict # of lines if there are too many
        int rowCount = 1 + Math.min(sessionLongopList.size(), maxLongOperationsRows);
        int columnCount = 12;
        int[] alignment = new int[columnCount];
        //
        int colNum = 0;
        int rowNum = 0;
        String[][] sessionLongopArray = new String[columnCount][rowCount];

        // Column Names
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        sessionLongopArray[colNum++][rowNum] = "SES_REF";
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        sessionLongopArray[colNum++][rowNum] = GvSessionLongop.COLUMNS.OPNAME.getColumnName();
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        sessionLongopArray[colNum++][rowNum] = GvSessionLongop.COLUMNS.TARGET.getColumnName();
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        sessionLongopArray[colNum++][rowNum] = GvSessionLongop.COLUMNS.SOFAR.getColumnName();
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        sessionLongopArray[colNum++][rowNum] = "%";
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        sessionLongopArray[colNum++][rowNum] = GvSessionLongop.COLUMNS.TOTALWORK.getColumnName();
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        sessionLongopArray[colNum++][rowNum] = GvSessionLongop.COLUMNS.UNITS.getColumnName();
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        sessionLongopArray[colNum++][rowNum] = GvSessionLongop.COLUMNS.START_TIME.getColumnName();
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        sessionLongopArray[colNum++][rowNum] = GvSessionLongop.COLUMNS.TIME_REMAINING.getColumnName();
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        sessionLongopArray[colNum++][rowNum] = GvSessionLongop.COLUMNS.SQL_ID.getColumnName();
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        sessionLongopArray[colNum++][rowNum] = GvSession.columnNames[GvSession.USERNAME];
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        sessionLongopArray[colNum++][rowNum] = GvSession.columnNames[GvSession.EVENT];

        // Column Values
        for (int i = 0; i < (rowCount - 1); i++) {
            GvSessionLongop sessionLongop = sessionLongopList.get(i);
            colNum = 0;
            rowNum = i + 1;
            GvSession session = GvSessions.getSessionsMapByPrimaryKey().get(sessionLongop.getGvSessionPrimaryKey());
            sessionLongopArray[colNum++][rowNum] = session == null ? null : session.getAlterSystemKillSessionReference();
            sessionLongopArray[colNum++][rowNum] = sessionLongop.getOpname();
            sessionLongopArray[colNum++][rowNum] = sessionLongop.getTarget();
            sessionLongopArray[colNum++][rowNum] = Str.formatDoubleNumber(sessionLongop.getSofar().doubleValue());
            sessionLongopArray[colNum++][rowNum] = sessionLongop.getSofarPercent();
            sessionLongopArray[colNum++][rowNum] = Str.formatDoubleNumber(sessionLongop.getTotalwork().doubleValue());
            sessionLongopArray[colNum++][rowNum] = sessionLongop.getUnits();
            sessionLongopArray[colNum++][rowNum] = Str.formatSecondsNumber(sessionLongop.getStartTime());
            sessionLongopArray[colNum++][rowNum] = Str.formatSecondsNumber(sessionLongop.getTimeRemaining());
            sessionLongopArray[colNum++][rowNum] = sessionLongop.getSqlId();
            sessionLongopArray[colNum++][rowNum] = session == null ? null : session.getUsername();
            sessionLongopArray[colNum++][rowNum] = session == null ? null : session.getEvent();
//      sessionLongopArray[colNum++][rowNum] = Str.rtrunc(sessionLongop.getMachine(), 15);
        }

        //
//    sb.append("\n\r");
        sb.append("Long operations: " + sessionLongopsMapByPrimaryKey.size()
                + ((maxLongOperationsRows == Integer.MAX_VALUE) ? "" : " limit: " + maxLongOperationsRows)
                + " / gv$session_longops"
                + " (" + getFetchTime() + "ms)\n\r");
        Str.convertArrayToStringBufferAsTable(sessionLongopArray, alignment, sb);
        sb.append("\n\r");
    }

    //
    //
    //
    private static class ComparatorStartTimeDesc implements Comparator<GvSessionLongop> {
        @Override
        public int compare(GvSessionLongop o1, GvSessionLongop o2) {
            int result = compareTo(o2.getStartTime(), o1.getStartTime());
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
}
