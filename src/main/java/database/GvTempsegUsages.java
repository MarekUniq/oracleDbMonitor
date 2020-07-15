package database;

import common.CommandLineArgument;
import common.Log;
import common.Str;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 */
class GvTempsegUsages {

    // map sorted by primary key session_addr@inst_id
    private static final SortedMap<String, GvTempsegUsage> tempsegUsageMapByPrimaryKey = new TreeMap<>();
    //
    private static long fetchTime;


    public static void setFetchTime(long fetchTime) {
        GvTempsegUsages.fetchTime = fetchTime;
    }

    public static long getFetchTime() {
        return fetchTime;
    }

    public static void clear() {
        tempsegUsageMapByPrimaryKey.clear();
    }

    //
    public static void addTempsegUsage(ResultSet rs) throws Exception {
        //
        GvTempsegUsage tempsegUsage = new GvTempsegUsage(rs);
        GvTempsegUsage oldTempsegUsage;
        //
        String key = tempsegUsage.getPrimaryKey();
        oldTempsegUsage = tempsegUsageMapByPrimaryKey.put(key, tempsegUsage);
        if (oldTempsegUsage != null) {
            Log.println("new: " + tempsegUsage.getPrimaryKey());
            Log.println("old: " + oldTempsegUsage.getPrimaryKey());
//      throw new RuntimeException("duplicate value");
            Log.println("Warning: addTempsegUsage() - duplicate value");
        }
    }

    public static void postLoadCompletedTasks() {
        //
        for (GvTempsegUsage tempsegUsage : tempsegUsageMapByPrimaryKey.values()) {
            String sessAddrKey = GvSession.getPrimaryKeySaddr(tempsegUsage.getSessionAddr(), tempsegUsage.getInstId());
            GvSession session = GvSessions.getSessionsMapBySAddr().get(sessAddrKey);
            //
            if (session != null) {
                if (tempsegUsage.getGvSession() == null)
                    tempsegUsage.setGvSession(session);
                else
                    throw new RuntimeException("duplicate mapping: " + tempsegUsage.getPrimaryKey()
                            + " : " + session.getPrimaryKey()
                            + " : " + tempsegUsage.getGvSession().getPrimaryKey()
                    );
            }
        }
    }

    //
    public static void getOutputTempsegUsage(StringBuffer sb) {
        //
        int maxTempSegmentUsageRows = CommandLineArgument.getMaxTempSegmentUsageRows();
        if (maxTempSegmentUsageRows <= 0)
            return;

        //
        List<GvTempsegUsage> tempsegUsageList = new ArrayList<>(tempsegUsageMapByPrimaryKey.values());

        //
        // Math.min() allows to restrict # of lines if there are too many
        int rowCount = 1 + Math.min(tempsegUsageList.size(), maxTempSegmentUsageRows);
        int columnCount = 9;
        int[] alignment = new int[columnCount];
        //
        int colNum = 0;
        int rowNum = 0;
        String[][] tempsegArray = new String[columnCount][rowCount];

        // Column Names
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        tempsegArray[colNum++][rowNum] = "SES_REF";
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        tempsegArray[colNum++][rowNum] = GvTempsegUsage.columnNames[GvTempsegUsage.SQL_ID];
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        tempsegArray[colNum++][rowNum] = GvTempsegUsage.columnNames[GvTempsegUsage.TABLESPACE];
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        tempsegArray[colNum++][rowNum] = GvTempsegUsage.columnNames[GvTempsegUsage.SEGTYPE];
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        tempsegArray[colNum++][rowNum] = GvTempsegUsage.columnNames[GvTempsegUsage.BLOCKS];
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        tempsegArray[colNum++][rowNum] = GvSession.columnNames[GvSession.USERNAME];
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        tempsegArray[colNum++][rowNum] = GvSession.columnNames[GvSession.OSUSER];
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        tempsegArray[colNum++][rowNum] = GvSession.columnNames[GvSession.LAST_CALL_ET];
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        tempsegArray[colNum++][rowNum] = GvSession.columnNames[GvSession.EVENT];

        // Column Values
        for (int i = 0; i < (rowCount - 1); i++) {
            GvTempsegUsage tempsegUsage = tempsegUsageList.get(i);
            GvSession session = tempsegUsage.getGvSession();
            colNum = 0;
            rowNum = i + 1;
            tempsegArray[colNum++][rowNum] = session == null ? "?" : session.getAlterSystemKillSessionReference();
            tempsegArray[colNum++][rowNum] = tempsegUsage.getSqlId();
            tempsegArray[colNum++][rowNum] = tempsegUsage.getTablespace();
            tempsegArray[colNum++][rowNum] = tempsegUsage.getSegType();
            tempsegArray[colNum++][rowNum] = Str.formatDoubleNumber(tempsegUsage.getBlocks());
            tempsegArray[colNum++][rowNum] = session == null ? "?" : session.getUsername();
            tempsegArray[colNum++][rowNum] = session == null ? "?" : session.getOsuser();
            tempsegArray[colNum++][rowNum] = session == null ? "?" : Str.formatSecondsNumber(session.getLastCallEt());
            tempsegArray[colNum++][rowNum] = session == null ? "?" : session.getEvent();
        }

        //
        sb.append("Temporary Segment Usage: " + tempsegUsageMapByPrimaryKey.size()
                + ((maxTempSegmentUsageRows == Integer.MAX_VALUE) ? "" : " limit: " + maxTempSegmentUsageRows)
                + " / gv$tempseg_usage"
                + " (" + getFetchTime() + "ms)");
        sb.append(Log.EOL);
        Str.convertArrayToStringBufferAsTable(tempsegArray, alignment, sb);
        sb.append(Log.EOL);
    }
}
