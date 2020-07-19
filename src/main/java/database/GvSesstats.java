package database;

import common.CommandLineArgument;
import common.Str;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 *
 */
class GvSesstats {
    // map sorted by primary key inst_id,name
    private static final SortedMap<String, GvSesstat> sesstatsMapByPrimaryKey = new TreeMap<>();
    //
    private static long gvSesstatFetchTime;

    static void setGvSesstatFetchTime(long gvSesstatFetchTime) {
        GvSesstats.gvSesstatFetchTime = gvSesstatFetchTime;
    }

    static long getGvSesstatFetchTime() {
        return gvSesstatFetchTime;
    }

    //
    private static long updateCounter = 0;

    //
    static void addSesstat(ResultSet rs) throws SQLException {
        //
        String instId = rs.getString("INST_ID");
        String sid = rs.getString("SID");
        String statistic = rs.getString("STATISTIC#");
        long value = rs.getLong("VALUE");
        //
        String primaryKey = GvSesstat.getPrimaryKey(instId, sid, statistic);
        //
        GvSesstat gvSesstat = sesstatsMapByPrimaryKey.get(primaryKey);
        //
        if (gvSesstat != null) {
            gvSesstat.setValue(value);
        }
        else {
            gvSesstat = new GvSesstat(instId, sid, statistic, value);
            sesstatsMapByPrimaryKey.put(primaryKey, gvSesstat);
        }
        //
        gvSesstat.setUpdateCounter(updateCounter);
    }

    //
    static void postLoadCompletedTasks() {
        //
        List<String> removeList = new ArrayList<>();
        // detect stats to delete
        for (GvSesstat gvSesstat : sesstatsMapByPrimaryKey.values()) {
            if (gvSesstat.getUpdateCounter() != updateCounter) {
                removeList.add(gvSesstat.getPrimaryKey());
            }
        }
        // delete
        for (String key : removeList) {
            sesstatsMapByPrimaryKey.remove(key);
        }
        // update counter
        updateCounter++;
    }

    //
    public static String getSessionIOStats(GvSession session) {
        //
        String[] statsList = {
                GvStatnames.PHYSICAL_READ_TOTAL_BYTES
                , GvStatnames.PHYSICAL_READ_TOTAL_IO_REQUESTS
                , GvStatnames.PHYSICAL_WRITE_TOTAL_BYTES
                , GvStatnames.PHYSICAL_WRITE_TOTAL_IO_REQUESTS
                , GvStatnames.REDO_SIZE
                , GvStatnames.CPU_USED_BY_THIS_SESSION
        };
        //
        return Arrays.stream(statsList)
                .map(s -> getStatisticValue(session, s))
                .map(Str::formatDoubleNumber)
                .collect(Collectors.joining("/"));
    }

    //
    public static double getStatisticValue(GvSession session, String statisticsName) {
        //
        double interval = CommandLineArgument.getUpdateIntervalInSeconds();
        //
        String statistic = GvStatnames.getStatistic(session.getInstId(), statisticsName);
        String primaryKey = GvSesstat.getPrimaryKey(session.getInstId(), session.getSid(), statistic);
        GvSesstat gvSesstat = sesstatsMapByPrimaryKey.get(primaryKey);
        //
        double value;
        if (session.isNewSessionFlag()) {
            // for new sessions take value
            value = gvSesstat == null ? 0d : gvSesstat.getValue();
        }
        else {
            // for old sessions take diff
            value = gvSesstat == null ? 0d : gvSesstat.getValueDiff();
        }
        //
        if (GvStatnames.CPU_USED_BY_THIS_SESSION.equals(statisticsName)) {
            // CPU time is reported "in 10s of milliseconds"
            return value / interval / 100d;
        }
        else {
            return value / interval;
        }
    }

}
