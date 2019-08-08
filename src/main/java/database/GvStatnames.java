package database;

import common.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 */
class GvStatnames {

    // map sorted by primary key inst_id
    private static final SortedMap<String, GvStatname> statnamesMapByPrimaryKey = new TreeMap<>();

    //
    public static final String PHYSICAL_READ_TOTAL_BYTES = "physical read total bytes";
    public static final String PHYSICAL_READ_TOTAL_IO_REQUESTS = "physical read total IO requests";
    public static final String PHYSICAL_WRITE_TOTAL_BYTES = "physical write total bytes";
    public static final String PHYSICAL_WRITE_TOTAL_IO_REQUESTS = "physical write total IO requests";
    public static final String CPU_USED_BY_THIS_SESSION = "CPU used by this session";
    //
    public static final String CONSISTENT_GETS = "consistent gets";
    public static final String DB_BLOCK_GETS = "db block gets";
    public static final String DB_BLOCK_CHANGES = "db block changes";
    //
    public static final String REDO_SIZE = "redo size";
    //
    public static final String BYTES_SENT_VIA_SQL_NET_TO_CLIENT = "bytes sent via SQL*Net to client";
    public static final String BYTES_RECEIVED_VIA_SQL_NET_FROM_CLIENT = "bytes received via SQL*Net from client";
    public static final String BYTES_SENT_VIA_SQL_NET_TO_DBLINK = "bytes sent via SQL*Net to dblink";
    public static final String BYTES_RECEIVED_VIA_SQL_NET_FROM_DBLINK = "bytes received via SQL*Net from dblink";
    public static final String SQL_NET_ROUNDTRIPS_TO_FROM_CLIENT = "SQL*Net roundtrips to/from client";
    public static final String SQL_NET_ROUNDTRIPS_TO_FROM_DBLINK = "SQL*Net roundtrips to/from dblink";
    //
    public static final String USER_CALLS = "user calls";
    public static final String EXECUTE_COUNT = "execute count";
    public static final String USER_COMMITS = "user commits";
    public static final String USER_ROLLBACKS = "user rollbacks";

    //
    static void loadStatisticNameMapping(Connection connection) throws SQLException {
        //
        String sql = "select inst_id, statistic#, name from gv$statname\n" +
                "where name IN ('physical read total bytes','physical read total IO requests','physical write total bytes','physical write total IO requests','redo size' /*,'CPU used by this session','consistent gets','db block gets','db block changes','bytes sent via SQL*Net to client','bytes received via SQL*Net from client','bytes sent via SQL*Net to dblink','bytes received via SQL*Net from dblink','SQL*Net roundtrips to/from client','SQL*Net roundtrips to/from dblink','user calls','execute count','user commits','user rollbacks'*/)\n";
        //
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        //
        while (rs.next()) {
            GvStatname gvStatname = new GvStatname(rs);
            String primarykey = gvStatname.getPrimaryKey();
            statnamesMapByPrimaryKey.put(primarykey, gvStatname);
        }
        //
        Log.println("Loaded statistic names: " + statnamesMapByPrimaryKey.size());
    }

    public static String getStatisticInList() {
        String retVal = null;
        //
        for (GvStatname gvStatname : statnamesMapByPrimaryKey.values()) {
            if (retVal == null)
                retVal = gvStatname.getStatistic();
            else
                retVal += ", " + gvStatname.getStatistic();
        }
        //
        return retVal;
    }


    //
    static String getStatistic(String instId, String statisticName) {
        String primaryKey = GvStatname.getPrimaryKey(instId, statisticName);
        return statnamesMapByPrimaryKey.get(primaryKey).getStatistic();
    }

}
