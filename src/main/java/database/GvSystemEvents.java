package database;

import common.CommandLineArgument;
import common.Str;

import java.sql.ResultSet;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 */
class GvSystemEvents {
    //
    public static final String DB_FILE_SCATTERED_READ = "db file scattered read";
    public static final String DB_FILE_SEQUENTIAL_READ = "db file sequential read";
    public static final String LOG_FILE_SYNC = "log file sync";

    // map sorted by primary key inst_id,name
    private static final SortedMap<String, GvSystemEvent> systemEventMapByPrimaryKey = new TreeMap<>();
    //
    private static long fetchTime;

    //
    public static long getFetchTime() {
        return fetchTime;
    }

    public static void setFetchTime(long fetchTime) {
        GvSystemEvents.fetchTime = fetchTime;
    }

    //
    public static void addSystemEvent(ResultSet rs) throws Exception {
        //
        String instId = rs.getString("INST_ID");
        String event = rs.getString("EVENT");
        long totalWaits = rs.getLong("TOTAL_WAITS");
        long totalTimeouts = rs.getLong("TOTAL_TIMEOUTS");
        long timeWaited = rs.getLong("TIME_WAITED");

        String primaryKey = GvSystemEvent.getPrimaryKey(instId, event);
        //
        GvSystemEvent gvSystemEvent = systemEventMapByPrimaryKey.get(primaryKey);
        //
        if (gvSystemEvent != null) {
            gvSystemEvent.setNextValue(totalWaits, totalTimeouts, timeWaited);
        } else {
            gvSystemEvent = new GvSystemEvent(instId, event, totalWaits, totalTimeouts, timeWaited);
            systemEventMapByPrimaryKey.put(primaryKey, gvSystemEvent);
        }
    }

    //
    public static String getStats(String instId, String event) {
        //
        double interval = CommandLineArgument.getUpdateIntervalInSeconds();
        //
        String key = GvSystemEvent.getPrimaryKey(instId, event);
        GvSystemEvent systemEvent = systemEventMapByPrimaryKey.get(key);

        //
        double waits = systemEvent.getTotalWaitsDiff() / interval;
        double timeouts = 0;
        double timeWaited = 0;
        //
        if (systemEvent.getTotalWaitsDiff() != 0) {
            timeouts = (double) systemEvent.getTotalTimeoutsDiff() / (double) systemEvent.getTotalWaitsDiff();
            timeWaited = (double) systemEvent.getTimeWaitedDiff() / (double) 10 / (double) systemEvent.getTotalWaitsDiff();
        }

        //
        return
                Str.formatDoubleNumber(waits)
                        + "/"
                        + Str.formatDoubleNumber(timeouts)
                        + "/"
                        + Str.formatDoubleNumber(timeWaited)
                ;
    }


    //
    @SuppressWarnings({"EmptyMethod"})
    public static void clear() {
        /* do nothing */
    }
}
