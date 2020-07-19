package database;

import java.sql.ResultSet;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 */
class GvParameters {
    // map sorted by primary key inst_id
    private static final SortedMap<String, GvParameter> instancesMapByPrimaryKey = new TreeMap<>();
    //
    private static long fetchTime;

    public static long getFetchTime() {
        return fetchTime;
    }

    public static void setFetchTime(long fetchTime) {
        GvParameters.fetchTime = fetchTime;
    }

    //
    public static void clear() {
        instancesMapByPrimaryKey.clear();
    }

    public static void addParameter(ResultSet rs) throws Exception {
        //
        GvParameter parameter = new GvParameter(rs);
        instancesMapByPrimaryKey.put(parameter.getPrimaryKey(), parameter);
    }

    //
    public static String getCpuCount(String instId) {
        GvParameter parameter = instancesMapByPrimaryKey.get(GvParameter.getPrimaryKey(instId, GvParameter.NAME.cpu_count));
        if (parameter == null) {
            return "missing";
        }
        else {
            return parameter.getValue();
        }
    }

    public static String getDbBlockSize() {
        GvParameter parameter = instancesMapByPrimaryKey.get(GvParameter.getPrimaryKey("1", GvParameter.NAME.db_block_size));
        if (parameter == null) {
            return "missing";
        }
        else {
            return parameter.getValue();
        }
    }
}
