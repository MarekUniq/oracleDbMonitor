package database;

import common.CommandLineArgument;
import common.Str;

import java.sql.ResultSet;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 */
class GvSysstats {
    // map sorted by primary key inst_id,name
    private static final SortedMap<String, GvSysstat> sysstatsMapByPrimaryKey = new TreeMap<>();
    //
    private static long fetchTime;

    //
    public static long getFetchTime() {
        return fetchTime;
    }

    public static void setFetchTime(long fetchTime) {
        GvSysstats.fetchTime = fetchTime;
    }

    //
    public static void addSysstat(ResultSet rs) throws Exception {
        //
        String instId = rs.getString("INST_ID");
        String name = rs.getString("NAME");
        long value = rs.getLong("VALUE");
        String primaryKey = GvSysstat.getPrimaryKey(instId, name);
        //
        GvSysstat gvSysstat = sysstatsMapByPrimaryKey.get(primaryKey);
        //
        if (gvSysstat != null) {
            gvSysstat.setNextValue(value);
        }
        else {
            gvSysstat = new GvSysstat(instId, name, value);
            sysstatsMapByPrimaryKey.put(primaryKey, gvSysstat);
        }
    }

    //
    public static long getStats(String instId, String name) {
        //
        String key = GvSysstat.getPrimaryKey(instId, name);
        return sysstatsMapByPrimaryKey.get(key).getValueDiff();
    }

    //
    //
    @SuppressWarnings({"SameParameterValue"})
    public static String getStatsString(String instId, String event) {
        //
        double interval = CommandLineArgument.getUpdateIntervalInSeconds();
        //
        long value = getStats(instId, event);
        //
        return
                Str.formatDoubleNumber((double)value / interval)
                ;
    }

    //
    //
    public static String getPhysicalReadsStats(String instId) {
        //
        double interval = CommandLineArgument.getUpdateIntervalInSeconds();
        //
        long phyReadTotalBytes = getStats(instId, GvStatnames.PHYSICAL_READ_TOTAL_BYTES);
        long phyReadTotalIOReq = getStats(instId, GvStatnames.PHYSICAL_READ_TOTAL_IO_REQUESTS);
        //
        return
                Str.formatDoubleNumber((double)phyReadTotalBytes / interval)
                + "/"
                + Str.formatDoubleNumber((double)phyReadTotalIOReq / interval)
                ;
    }

    //
    //
    public static String getPhysicalWriteStats(String instId) {
        //
        double interval = CommandLineArgument.getUpdateIntervalInSeconds();
        //
        long phyWriteTotalBytes = getStats(instId, GvStatnames.PHYSICAL_WRITE_TOTAL_BYTES);
        long phyWriteTotalIOReq = getStats(instId, GvStatnames.PHYSICAL_WRITE_TOTAL_IO_REQUESTS);
        //
        return
                Str.formatDoubleNumber((double)phyWriteTotalBytes / interval)
                + "/"
                + Str.formatDoubleNumber((double)phyWriteTotalIOReq / interval)
                ;
    }

    //
    //
    public static String getCpuUsedByThisSessionStats(String instId) {
        //
        double interval = CommandLineArgument.getUpdateIntervalInSeconds();
        //
        String cpuUsedKey = GvSysstat.getPrimaryKey(instId, GvStatnames.CPU_USED_BY_THIS_SESSION);
        long cpuUsed = sysstatsMapByPrimaryKey.get(cpuUsedKey).getValueDiff();
        //
        return
                Str.formatDoubleNumber((double)cpuUsed / interval / 100 /* convert centi-sec to sec*/)
                ;
    }

    //
    //
    public static String getSqlNetStats(String instId) {
        //
        double interval = CommandLineArgument.getUpdateIntervalInSeconds();
        //
        long bytesRecv = getStats(instId, GvStatnames.BYTES_RECEIVED_VIA_SQL_NET_FROM_CLIENT) + getStats(instId, GvStatnames.BYTES_RECEIVED_VIA_SQL_NET_FROM_DBLINK);
        long bytesSent = getStats(instId, GvStatnames.BYTES_SENT_VIA_SQL_NET_TO_CLIENT) + getStats(instId, GvStatnames.BYTES_SENT_VIA_SQL_NET_TO_DBLINK);
        long roundTrips = getStats(instId, GvStatnames.SQL_NET_ROUNDTRIPS_TO_FROM_CLIENT) + getStats(instId, GvStatnames.SQL_NET_ROUNDTRIPS_TO_FROM_DBLINK);
        //
        return
                Str.formatDoubleNumber((double)bytesRecv / interval)
                + "/" + Str.formatDoubleNumber((double)bytesSent / interval)
                + "/" + Str.formatDoubleNumber((double)roundTrips / interval)
                ;
    }

    //
    public static String getLogicalReadStats(String instId) {
        //
        double interval = CommandLineArgument.getUpdateIntervalInSeconds();
        //
        long consistentGetsGets = getStats(instId, GvStatnames.CONSISTENT_GETS);
        long dbBlockGets = getStats(instId, GvStatnames.DB_BLOCK_GETS);
        long dbBlockChanges = getStats(instId, GvStatnames.DB_BLOCK_CHANGES);
        //
        return
                Str.formatDoubleNumber((double)consistentGetsGets / interval)
                + "/" + Str.formatDoubleNumber((double)dbBlockGets / interval)
                + "/" + Str.formatDoubleNumber((double)dbBlockChanges / interval)
                ;
    }

    //
    public static String getCallsExecsCommitsRollbacksStats(String instId) {
        //
        double interval = CommandLineArgument.getUpdateIntervalInSeconds();
        //
        long userCalls = getStats(instId, GvStatnames.USER_CALLS);
        long executeCount = getStats(instId, GvStatnames.EXECUTE_COUNT);
        long userCommits = getStats(instId, GvStatnames.USER_COMMITS);
        long userRollbacks = getStats(instId, GvStatnames.USER_ROLLBACKS);
        //
        return
                Str.formatDoubleNumber((double)userCalls / interval)
                + "/" + Str.formatDoubleNumber((double)executeCount / interval)
                + "/" + Str.formatDoubleNumber((double)userCommits / interval)
                + "/" + Str.formatDoubleNumber((double)userRollbacks / interval)
                ;
    }

    //
    @SuppressWarnings({"EmptyMethod"})
    public static void clear() {
        /* do nothing */
    }

}
