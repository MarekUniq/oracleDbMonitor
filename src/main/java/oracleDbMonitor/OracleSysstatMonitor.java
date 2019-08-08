package oracleDbMonitor;

import common.CommandLineArgument;
import common.Database;
import common.Log;
import common.Str;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.*;

/**
 *
 */
public class OracleSysstatMonitor {

    //
    private Connection c;

    //
    public OracleSysstatMonitor() throws SQLException {
        this.c = Database.getConnection(CommandLineArgument.getUrl(), CommandLineArgument.getUsername(), CommandLineArgument.getPassword());
    }

    //
    public static void main(String[] args) throws Exception {
        Log.println("Start: OracleSysstatMonitor");
        CommandLineArgument.decodeArguments(args);
        OracleSysstatMonitor oracleSystemEventMonitor = new OracleSysstatMonitor();
        oracleSystemEventMonitor.run();
        oracleSystemEventMonitor.close();
    }

    private void run() throws Exception {
        //
        final int interval = CommandLineArgument.getUpdateInterval();
        PreparedStatement ps;
        String sql;

        //
        if (CommandLineArgument.getSid() > 0) {
            Log.println("SESSTAT based monitoring: " + CommandLineArgument.ARGS.SID + ": " + CommandLineArgument.getSid() + " " + CommandLineArgument.ARGS.INST_ID + ": " + CommandLineArgument.getInstId());
            sql = "SELECT SYSDATE, INST_ID, SID ||' ' || NAME NAME, VALUE FROM GV$SESSTAT S NATURAL INNER JOIN GV$STATNAME SN WHERE SID = " + CommandLineArgument.getSid();
            //
            if (CommandLineArgument.getInstId() > 0)
                sql += " AND INST_ID = " + CommandLineArgument.getInstId();
        } else {
            Log.println("SYSSTAT based monitoring");
            sql = "SELECT SYSDATE, INST_ID, NAME, VALUE FROM GV$SYSSTAT";
        }

        //
        Log.println("SQL: " + sql);
        ps = c.prepareStatement(sql);

        //
        Sysstats ePrev;
        Sysstats eCurr;
        //
        ResultSet rs = ps.executeQuery();

        ePrev = new Sysstats(rs);
        Thread.sleep(interval);

        //
        while (true) {
            rs = ps.executeQuery();
            eCurr = new Sysstats(rs);
            eCurr.printDiff(ePrev);
            //
            ePrev = eCurr;
            Thread.sleep(interval);
        }
    }

    private void close() throws SQLException {
        c.close();
    }

    //
    //
    //
    private class Sysstats {

        private SortedMap<String, Sysstat> stats = new TreeMap<>();

        public Sysstats(ResultSet rs) throws SQLException {
            while (rs.next()) {
                Sysstat sysstat = new Sysstat(rs);
                stats.put(sysstat.getPrimaryKey(), sysstat);
            }
        }

        public void printDiff(Sysstats ePrev) {
            //
            SortedSet<Sysstat> diffStats = new TreeSet<>(new Comparator<Sysstat>() {
                @Override
                public int compare(Sysstat o1, Sysstat o2) {
                    int retVal = o2.getVALUE().abs().compareTo(o1.getVALUE().abs());
                    return retVal == 0 ? o1.getPrimaryKey().compareTo(o2.getPrimaryKey()) : retVal;
                }
            });

            //
            {
                for (Sysstat currStat : stats.values()) {
                    //
                    Sysstat prevStat = ePrev.getEventByPrimaryKey(currStat.getPrimaryKey());
                    //
                    Sysstat diffStat = currStat.minusPerSecond(prevStat);
                    if (diffStat.getVALUE().abs().longValue() > 10)
                        diffStats.add(diffStat);
                }
            }

            //
            {
                //
                String[][] output = new String[3][diffStats.size() + 1];
                output[0][0] = "INST_ID";
                output[1][0] = "NAME";
                output[2][0] = "VALUE";

                //
                int counter = 1;
                for (Sysstat diffEvent : diffStats) {
                    output[0][counter] = Str.formatDoubleNumber(diffEvent.getINST_ID().doubleValue());
                    output[1][counter] = diffEvent.getNAME();
                    output[2][counter] = Str.formatDoubleNumber(diffEvent.getVALUE().doubleValue());
                    //
                    counter++;
                }
                //
                int[] outputColMaxLen = Str.getMaxColumnLength(output);
                //
                Log.println("");
                int maxRows = Math.min(output[0].length, 50);
                for (int row = 0; row < maxRows; row++) {
                    String line = "";
                    for (int col = 0; col < output.length; col++) {
                        line += Str.lpad(output[col][row], outputColMaxLen[col] + 1, ' ');
                    }
                    Log.println(line);
                }
            }

        }

        //
        private Sysstat getEventByPrimaryKey(String key) {
            return stats.get(key);
        }
    }

    //
    enum COLS {
        SYSDATE, INST_ID, NAME, VALUE;
    }

    //
    //
    //
    private class Sysstat {
        //
        private Timestamp SYSDATE;
        private BigDecimal INST_ID;
        private String NAME;
        private BigDecimal VALUE;

        public Timestamp getSYSDATE() {
            return SYSDATE;
        }

        public void setSYSDATE(Timestamp SYSDATE) {
            this.SYSDATE = SYSDATE;
        }

        public BigDecimal getINST_ID() {
            return INST_ID;
        }

        public void setINST_ID(BigDecimal INST_ID) {
            this.INST_ID = INST_ID;
        }

        public String getNAME() {
            return NAME;
        }

        public void setNAME(String NAME) {
            this.NAME = NAME;
        }

        public BigDecimal getVALUE() {
            return VALUE;
        }

        public void setVALUE(BigDecimal VALUE) {
            this.VALUE = VALUE;
        }

        public Sysstat(ResultSet rs) throws SQLException {
            setSYSDATE(rs.getTimestamp(COLS.SYSDATE.name()));
            setINST_ID(rs.getBigDecimal(COLS.INST_ID.name()));
            setNAME(rs.getString(COLS.NAME.name()));
            setVALUE(rs.getBigDecimal(COLS.VALUE.name()));
        }

        private Sysstat(Timestamp SYSDATE, BigDecimal INST_ID, String NAME, BigDecimal VALUE) {
            this.SYSDATE = SYSDATE;
            this.INST_ID = INST_ID;
            this.NAME = NAME;
            this.VALUE = VALUE;
        }

        public String getPrimaryKey() {
            return getINST_ID().toPlainString() + "," + getNAME();
        }

        public Sysstat minusPerSecond(Sysstat prevStat) {
            BigDecimal seconds = new BigDecimal((getSYSDATE().getTime() - prevStat.getSYSDATE().getTime()) / 1000d);
            //
            Sysstat diffStat = null;
            try {
                diffStat = new Sysstat(
                        getSYSDATE()
                        , getINST_ID()
                        , getNAME()
                        , getVALUE().subtract(prevStat.getVALUE()).divide(seconds, RoundingMode.HALF_UP)
                );
            } catch (Exception e) {
                Log.println("1: " + getVALUE().toPlainString());
                Log.println("2: " + prevStat.getVALUE().toPlainString());
                Log.println("3: " + seconds.toPlainString());
                throw new RuntimeException(e);
            }
            //
            return diffStat;
        }
    }

}
