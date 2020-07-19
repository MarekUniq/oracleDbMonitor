package database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 */
class GvStatname {
    //
    private String instId;
    private String statistic;
    private String name;

    //
    public String getInstId() {
        return instId;
    }

    public String getStatistic() {
        return statistic;
    }

    public String getName() {
        return name;
    }

    //
    GvStatname(ResultSet rs) throws SQLException {
        this.instId = rs.getString("INST_ID");
        this.statistic = rs.getString("STATISTIC#");
        this.name = rs.getString("NAME");
    }

    //
    static String getPrimaryKey(String instId, String name) {
        return instId + "," + name;
    }

    //
    String getPrimaryKey() {
        return getPrimaryKey(instId, name);
    }

}
