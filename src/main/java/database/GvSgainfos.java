package database;

import common.Str;

import java.sql.ResultSet;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 */
class GvSgainfos {

    // map sorted by primary key inst_id
    private static final SortedMap<String, GvSgainfo> instancesMapByPrimaryKey = new TreeMap<>();

    //
    private static long fetchTime;

    //
    public static long getFetchTime() {
        return fetchTime;
    }

    //
    public static void setFetchTime(long fetchTime) {
        GvSgainfos.fetchTime = fetchTime;
    }

    //
    public static void clear() {
        instancesMapByPrimaryKey.clear();

    }

    //
    public static void addItem(ResultSet rs) throws Exception {
        //
        GvSgainfo sgainfo = new GvSgainfo(rs);
        instancesMapByPrimaryKey.put(sgainfo.getPrimaryKey(), sgainfo);

    }

    public static String getBufferCacheSize(String instId) {
        GvSgainfo sgainfo = instancesMapByPrimaryKey.get(GvSgainfo.getPrimaryKey(instId, GvSgainfo.NAME.BUFFER_CACHE_SIZE));
        return (sgainfo == null) ? "-" : Str.formatDoubleNumber(sgainfo.getBytes().doubleValue());
    }

    public static String getSharedPoolSize(String instId) {
        GvSgainfo sgainfo = instancesMapByPrimaryKey.get(GvSgainfo.getPrimaryKey(instId, GvSgainfo.NAME.SHARED_POOL_SIZE));
        return (sgainfo == null) ? "-" : Str.formatDoubleNumber(sgainfo.getBytes().doubleValue());
    }

    public static String getLargePoolSize(String instId) {
        GvSgainfo sgainfo = instancesMapByPrimaryKey.get(GvSgainfo.getPrimaryKey(instId, GvSgainfo.NAME.LARGE_POOL_SIZE));
        return (sgainfo == null) ? "-" : Str.formatDoubleNumber(sgainfo.getBytes().doubleValue());
    }

    public static String getSharedIOPoolSize(String instId) {
        GvSgainfo sgainfo = instancesMapByPrimaryKey.get(GvSgainfo.getPrimaryKey(instId, GvSgainfo.NAME.SHARED_IO_POOL_SIZE));
        return (sgainfo == null) ? "-" : Str.formatDoubleNumber(sgainfo.getBytes().doubleValue());
    }

    public static String getFreeSGAMemoryAvailable(String instId) {
        GvSgainfo sgainfo = instancesMapByPrimaryKey.get(GvSgainfo.getPrimaryKey(instId, GvSgainfo.NAME.FREE_SGA_MEMORY_AVAILABLE));
        return (sgainfo == null) ? "-" : Str.formatDoubleNumber(sgainfo.getBytes().doubleValue());
    }
}
