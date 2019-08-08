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
public class OracleSystemEventMonitor {

    //
    private Connection c;

    //
    public OracleSystemEventMonitor() throws SQLException {
        this.c = Database.getConnection(CommandLineArgument.getUrl(), CommandLineArgument.getUsername(), CommandLineArgument.getPassword());
    }

    //
    public static void main(String[] args) throws Exception {
        Log.println("Start: OracleSystemEventMonitor");
        CommandLineArgument.decodeArguments(args);
        OracleSystemEventMonitor oracleSystemEventMonitor = new OracleSystemEventMonitor();
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
            Log.println("GV$SESSION_EVENT based monitoring: " + CommandLineArgument.ARGS.SID + ": " + CommandLineArgument.getSid() + " " + CommandLineArgument.ARGS.INST_ID + ": " + CommandLineArgument.getInstId());
            sql = "SELECT SYSDATE, INST_ID, SID ||' ' || EVENT EVENT, TOTAL_WAITS, TOTAL_TIMEOUTS, TIME_WAITED_MICRO, 0 TOTAL_WAITS_FG, 0 TOTAL_TIMEOUTS_FG, 0 TIME_WAITED_MICRO_FG, WAIT_CLASS FROM GV$SESSION_EVENT WHERE WAIT_CLASS <> 'Idle' AND SID = " + CommandLineArgument.getSid();
            //
            if (CommandLineArgument.getInstId() > 0)
                sql += " AND INST_ID = " + CommandLineArgument.getInstId();
        } else {
            Log.println("GV$SYSTEM_EVENT based monitoring");
            sql = "SELECT SYSDATE, INST_ID, EVENT, TOTAL_WAITS, TOTAL_TIMEOUTS, TIME_WAITED_MICRO, TOTAL_WAITS_FG, TOTAL_TIMEOUTS_FG, TIME_WAITED_MICRO_FG, WAIT_CLASS FROM GV$SYSTEM_EVENT WHERE WAIT_CLASS <> 'Idle'";
        }

        //
        Log.println("SQL: " + sql);
        ps = c.prepareStatement(sql);

        //
        SystemEvents ePrev;
        SystemEvents eCurr;
        //
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
        } catch (SQLException e) {
            if (CommandLineArgument.getSid() > 0) {
                // no fallback for session based monitoring
                throw e;
            } else {
                // fallback for system based monitoring
                Log.println("Fallback to columns without Foreground Waits");
                ps = c.prepareStatement("SELECT SYSDATE, INST_ID, EVENT, TOTAL_WAITS, TOTAL_TIMEOUTS, TIME_WAITED_MICRO, 0 TOTAL_WAITS_FG, 0 TOTAL_TIMEOUTS_FG, 0 TIME_WAITED_MICRO_FG, WAIT_CLASS FROM GV$SYSTEM_EVENT WHERE WAIT_CLASS <> 'Idle'");
                rs = ps.executeQuery();
            }
        }
        ePrev = new SystemEvents(rs);
        Thread.sleep(interval);

        //
        while (true) {
            rs = ps.executeQuery();
            eCurr = new SystemEvents(rs);
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
    class SystemEvents {
        private SortedMap<String, SystemEvent> events = new TreeMap<>();

        public SystemEvents(ResultSet rs) throws SQLException {
            while (rs.next()) {
                SystemEvent systemEvent = new SystemEvent(rs);
                events.put(systemEvent.getPrimaryKey(), systemEvent);
            }
        }

        public void printDiff(SystemEvents ePrev) {
            //
            SortedSet<SystemEvent> diffEvents = new TreeSet<>(new Comparator<SystemEvent>() {
                @Override
                public int compare(SystemEvent o1, SystemEvent o2) {
                    int retVal = o2.getTIME_WAITED_MICRO().compareTo(o1.getTIME_WAITED_MICRO());
                    return retVal == 0 ? o1.getPrimaryKey().compareTo(o2.getPrimaryKey()) : retVal;
                }
            });

            //
            {
                for (SystemEvent currEvent : events.values()) {
                    //
                    SystemEvent prevEvent = ePrev.getEventByPrimaryKey(currEvent.getPrimaryKey());
                    //
                    SystemEvent diffEvent = currEvent.minusPerSecond(prevEvent);
                    if (diffEvent.getTOTAL_WAITS().intValue() > 0)
                        diffEvents.add(diffEvent);
                }
            }

            //
            {
                //
                String[][] output = new String[10][diffEvents.size() + 1];
                output[0][0] = "INST_ID";
                output[1][0] = "EVENT";
                output[2][0] = "TOTAL_WAITS";
                output[3][0] = "FG%";
                output[4][0] = "AVG_WAIT";
                output[5][0] = "FG%";
                output[6][0] = "TOTAL_TIMEOUTS";
                output[7][0] = "FG%";
                output[8][0] = "WAIT_CLASS";
                output[9][0] = "TIME_WAITED";
//
//          TOTAL_TIMEOUTS
//        TIME_WAITED_MICRO
//          TOTAL_WAITS_FG
//        TOTAL_TIMEOUTS_FG
//          TIME_WAITED_MICRO_FG
//        WAIT_CLASS
                // header

                //
                int counter = 1;
                for (SystemEvent diffEvent : diffEvents) {
                    output[0][counter] = Str.formatDoubleNumber(diffEvent.getINST_ID().doubleValue());
                    output[1][counter] = diffEvent.getEVENT();
                    output[2][counter] = Str.formatDoubleNumber(diffEvent.getTOTAL_WAITS().doubleValue());

                    if (diffEvent.getTOTAL_WAITS().intValue() == 0)
                        output[3][counter] = "-";
                    else
                        output[3][counter] = Str.formatDoubleNumber((100d * diffEvent.getTOTAL_WAITS_FG().doubleValue() / diffEvent.getTOTAL_WAITS().doubleValue()));

                    if (diffEvent.getTOTAL_WAITS().intValue() == 0)
                        output[4][counter] = "-";
                    else
                        output[4][counter] = Str.formatDoubleNumber(diffEvent.getTIME_WAITED_MICRO().doubleValue() / diffEvent.getTOTAL_WAITS().doubleValue() / 1000000d);

                    if (diffEvent.getTIME_WAITED_MICRO().intValue() == 0)
                        output[5][counter] = "-";
                    else
                        output[5][counter] = Str.formatDoubleNumber(100d * diffEvent.getTIME_WAITED_MICRO_FG().doubleValue() / diffEvent.getTIME_WAITED_MICRO().doubleValue());

                    output[6][counter] = Str.formatDoubleNumber(diffEvent.getTOTAL_TIMEOUTS().doubleValue());

                    if (diffEvent.getTOTAL_TIMEOUTS().intValue() == 0)
                        output[7][counter] = "-";
                    else
                        output[7][counter] = Str.formatDoubleNumber((100d * diffEvent.getTOTAL_TIMEOUTS_FG().doubleValue() / diffEvent.getTOTAL_TIMEOUTS().doubleValue()));

                    output[8][counter] = diffEvent.getWAIT_CLASS();
                    output[9][counter] = Str.formatDoubleNumber(diffEvent.getTIME_WAITED_MICRO().doubleValue() / 1000d / 1000d);
                    //
                    counter++;
                }
                //
                int[] outputColMaxLen = Str.getMaxColumnLength(output);
                //
                Log.println("");
                for (int row = 0; row < output[0].length; row++) {
                    String line = "";
                    for (int col = 0; col < output.length; col++) {
                        line += Str.lpad(output[col][row], outputColMaxLen[col] + 1, ' ');
                    }
                    Log.println(line);
                }
            }

        }

        private SystemEvent getEventByPrimaryKey(String key) {
            return events.get(key);
        }
    }

    //
    enum COLS {
        SYSDATE, INST_ID, EVENT, TOTAL_WAITS, TOTAL_TIMEOUTS, TIME_WAITED_MICRO, TOTAL_WAITS_FG, TOTAL_TIMEOUTS_FG, TIME_WAITED_MICRO_FG, WAIT_CLASS;
    }

    //
    //
    //
    class SystemEvent implements Comparable<SystemEvent> {
        private Timestamp SYSDATE;
        private BigDecimal INST_ID;
        private String EVENT;
        private BigDecimal TOTAL_WAITS;
        private BigDecimal TOTAL_TIMEOUTS;
        private BigDecimal TIME_WAITED_MICRO;
        private BigDecimal TOTAL_WAITS_FG;
        private BigDecimal TOTAL_TIMEOUTS_FG;
        private BigDecimal TIME_WAITED_MICRO_FG;
        private String WAIT_CLASS;

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

        public String getEVENT() {
            return EVENT;
        }

        public void setEVENT(String EVENT) {
            this.EVENT = EVENT;
        }

        public BigDecimal getTOTAL_WAITS() {
            return TOTAL_WAITS;
        }

        public void setTOTAL_WAITS(BigDecimal TOTAL_WAITS) {
            this.TOTAL_WAITS = TOTAL_WAITS;
        }

        public BigDecimal getTOTAL_TIMEOUTS() {
            return TOTAL_TIMEOUTS;
        }

        public void setTOTAL_TIMEOUTS(BigDecimal TOTAL_TIMEOUTS) {
            this.TOTAL_TIMEOUTS = TOTAL_TIMEOUTS;
        }

        public BigDecimal getTIME_WAITED_MICRO() {
            return TIME_WAITED_MICRO;
        }

        public void setTIME_WAITED_MICRO(BigDecimal TIME_WAITED_MICRO) {
            this.TIME_WAITED_MICRO = TIME_WAITED_MICRO;
        }

        public BigDecimal getTOTAL_WAITS_FG() {
            return TOTAL_WAITS_FG;
        }

        public void setTOTAL_WAITS_FG(BigDecimal TOTAL_WAITS_FG) {
            this.TOTAL_WAITS_FG = TOTAL_WAITS_FG;
        }

        public BigDecimal getTOTAL_TIMEOUTS_FG() {
            return TOTAL_TIMEOUTS_FG;
        }

        public void setTOTAL_TIMEOUTS_FG(BigDecimal TOTAL_TIMEOUTS_FG) {
            this.TOTAL_TIMEOUTS_FG = TOTAL_TIMEOUTS_FG;
        }

        public BigDecimal getTIME_WAITED_MICRO_FG() {
            return TIME_WAITED_MICRO_FG;
        }

        public void setTIME_WAITED_MICRO_FG(BigDecimal TIME_WAITED_MICRO_FG) {
            this.TIME_WAITED_MICRO_FG = TIME_WAITED_MICRO_FG;
        }

        public String getWAIT_CLASS() {
            return WAIT_CLASS;
        }

        public void setWAIT_CLASS(String WAIT_CLASS) {
            this.WAIT_CLASS = WAIT_CLASS;
        }

        //
        public SystemEvent(ResultSet rs) throws SQLException {
            setSYSDATE(rs.getTimestamp(COLS.SYSDATE.name()));
            setINST_ID(rs.getBigDecimal(COLS.INST_ID.name()));
            setEVENT(rs.getString(COLS.EVENT.name()));
            setTOTAL_WAITS(rs.getBigDecimal(COLS.TOTAL_WAITS.name()));
            setTOTAL_TIMEOUTS(rs.getBigDecimal(COLS.TOTAL_TIMEOUTS.name()));
            setTIME_WAITED_MICRO(rs.getBigDecimal(COLS.TIME_WAITED_MICRO.name()));
            setTOTAL_WAITS_FG(rs.getBigDecimal(COLS.TOTAL_WAITS_FG.name()));
            setTOTAL_TIMEOUTS_FG(rs.getBigDecimal(COLS.TOTAL_TIMEOUTS_FG.name()));
            setTIME_WAITED_MICRO_FG(rs.getBigDecimal(COLS.TIME_WAITED_MICRO_FG.name()));
            setWAIT_CLASS(rs.getString(COLS.WAIT_CLASS.name()));
        }

        //
        SystemEvent(Timestamp sysdate, BigDecimal INST_ID, String EVENT, BigDecimal TOTAL_WAITS, BigDecimal TOTAL_TIMEOUTS, BigDecimal TIME_WAITED_MICRO, BigDecimal TOTAL_WAITS_FG, BigDecimal TOTAL_TIMEOUTS_FG, BigDecimal TIME_WAITED_MICRO_FG, String WAIT_CLASS) {
            this.SYSDATE = sysdate;
            this.INST_ID = INST_ID;
            this.EVENT = EVENT;
            this.TOTAL_WAITS = TOTAL_WAITS;
            this.TOTAL_TIMEOUTS = TOTAL_TIMEOUTS;
            this.TIME_WAITED_MICRO = TIME_WAITED_MICRO;
            this.TOTAL_WAITS_FG = TOTAL_WAITS_FG;
            this.TOTAL_TIMEOUTS_FG = TOTAL_TIMEOUTS_FG;
            this.TIME_WAITED_MICRO_FG = TIME_WAITED_MICRO_FG;
            this.WAIT_CLASS = WAIT_CLASS;
        }

        String getPrimaryKey() {
            return getINST_ID().toPlainString() + "," + getEVENT();
        }

        @Override
        public int compareTo(SystemEvent o) {
            return getPrimaryKey().compareTo(o.getPrimaryKey());
        }

        public SystemEvent minusPerSecond(SystemEvent prevEvent) {
            //
            SystemEvent diffEvent;
            if (prevEvent == null) {
                BigDecimal seconds = new BigDecimal(CommandLineArgument.getUpdateIntervalInSeconds());
                diffEvent = new SystemEvent(
                        getSYSDATE()
                        , getINST_ID()
                        , getEVENT()
                        , getTOTAL_WAITS().divide(seconds, RoundingMode.HALF_UP)
                        , getTOTAL_TIMEOUTS().divide(seconds, RoundingMode.HALF_UP)
                        , getTIME_WAITED_MICRO().divide(seconds, RoundingMode.HALF_UP)
                        , getTOTAL_WAITS_FG().divide(seconds, RoundingMode.HALF_UP)
                        , getTOTAL_TIMEOUTS_FG().divide(seconds, RoundingMode.HALF_UP)
                        , getTIME_WAITED_MICRO_FG().divide(seconds, RoundingMode.HALF_UP)
                        , getWAIT_CLASS()
                );
            } else {
                BigDecimal seconds = new BigDecimal((getSYSDATE().getTime() - prevEvent.getSYSDATE().getTime()) / 1000d);
                diffEvent = new SystemEvent(
                        getSYSDATE()
                        , getINST_ID()
                        , getEVENT()
                        , getTOTAL_WAITS().subtract(prevEvent.getTOTAL_WAITS()).divide(seconds, RoundingMode.HALF_UP)
                        , getTOTAL_TIMEOUTS().subtract(prevEvent.getTOTAL_TIMEOUTS()).divide(seconds, RoundingMode.HALF_UP)
                        , getTIME_WAITED_MICRO().subtract(prevEvent.getTIME_WAITED_MICRO()).divide(seconds, RoundingMode.HALF_UP)
                        , getTOTAL_WAITS_FG().subtract(prevEvent.getTOTAL_WAITS_FG()).divide(seconds, RoundingMode.HALF_UP)
                        , getTOTAL_TIMEOUTS_FG().subtract(prevEvent.getTOTAL_TIMEOUTS_FG()).divide(seconds, RoundingMode.HALF_UP)
                        , getTIME_WAITED_MICRO_FG().subtract(prevEvent.getTIME_WAITED_MICRO_FG()).divide(seconds, RoundingMode.HALF_UP)
                        , getWAIT_CLASS()
                );
            }
            //
            return diffEvent;
        }

    }

}
