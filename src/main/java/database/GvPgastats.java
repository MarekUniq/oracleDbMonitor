package database;

import common.Str;

import java.sql.ResultSet;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 */
class GvPgastats {

    // map sorted by primary key inst_id
    private static final SortedMap<String, GvPgastat> instancesMapByPrimaryKey = new TreeMap<>();

    //
    private static long fetchTime;

    //
    public static long getFetchTime() {
        return fetchTime;
    }

    //
    public static void setFetchTime(long fetchTime) {
        GvPgastats.fetchTime = fetchTime;
    }

    //
    public static void clear() {
        instancesMapByPrimaryKey.clear();

    }

    //
    public static void addItem(ResultSet rs) throws Exception {
        //
        GvPgastat pgastat = new GvPgastat(rs);
        instancesMapByPrimaryKey.put(pgastat.getPrimaryKey(), pgastat);

    }

    public static String getPgaAllocated(String instId) {
        GvPgastat pgastat = instancesMapByPrimaryKey.get(GvPgastat.getPrimaryKey(instId, GvPgastat.NAME.TOTAL_PGA_ALLOCATED));
        return (pgastat == null) ? "-" : Str.formatDoubleNumber(pgastat.getValue().doubleValue());
    }
}
