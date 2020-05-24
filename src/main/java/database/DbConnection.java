package database;

import common.CommandLineArgument;
import common.Database;
import common.Log;
import common.Str;
import oracle.jdbc.internal.OracleConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class DbConnection {
    //
    @SuppressWarnings({"FieldCanBeLocal"})
    private Connection connection;

    //
    private PreparedStatement my_session;
    private PreparedStatement gv_parameter;
    private PreparedStatement gv_session;
    private PreparedStatement gv_px_session;
    private PreparedStatement gv_transaction;
    private PreparedStatement gv_database;
    private PreparedStatement gv_instance;
    private PreparedStatement gv_pgastat;
    private PreparedStatement gv_sgainfo;
    private PreparedStatement gv_session_longops;
    private PreparedStatement gv_sysstat;
    private PreparedStatement gv_system_event;
    private PreparedStatement gv_tempseg_usage;
    private PreparedStatement gv_sesstat;

    //
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                Log.println("DbConnection.close() failed : " + e.getMessage());
            }
        }
    }

    //
    private short getDbVersion() throws SQLException {
        return ((OracleConnection) connection).getVersionNumber();
    }

    //
    public DbConnection() throws Exception {
        //
        connection = Database.getConnection(CommandLineArgument.getUrl(), CommandLineArgument.getUsername(), CommandLineArgument.getPassword());

        //
        // prepare statements
        //

        // load Statname
        GvStatnames.loadStatisticNameMapping(connection);

        // database parameters
        gv_database = connection.prepareStatement("select name, log_mode, platform_name, force_logging, flashback_on from gv$database");

        // instance parameters
        gv_parameter = connection.prepareStatement("select inst_id, name, value from gv$parameter where name in ('cpu_count', 'db_block_size')");
        if (getDbVersion() >= 18000)
            gv_instance = connection.prepareStatement("select i.inst_id, i.host_name, i.version_full as version, (sysdate - i.startup_time)*24*60*60 startup_time from gv$instance i");
        else
            gv_instance = connection.prepareStatement("select i.inst_id, i.host_name, i.version, (sysdate - i.startup_time)*24*60*60 startup_time from gv$instance i");
        gv_pgastat = connection.prepareStatement("select inst_id, name, value from gv$pgastat where name = 'total PGA allocated'");
        gv_sgainfo = connection.prepareStatement("select inst_id, name, bytes from gv$sgainfo where name IN ('Buffer Cache Size','Shared Pool Size','Large Pool Size','Free SGA Memory Available','Shared IO Pool Size')");

        // instance statistics
        gv_sysstat = connection.prepareStatement("select inst_id, name, value from gv$sysstat where name IN ('physical read total bytes','physical read total IO requests','physical write total bytes','physical write total IO requests','CPU used by this session','consistent gets','db block gets','db block changes','redo size','bytes sent via SQL*Net to client','bytes received via SQL*Net from client','bytes sent via SQL*Net to dblink','bytes received via SQL*Net from dblink','SQL*Net roundtrips to/from client','SQL*Net roundtrips to/from dblink','user calls','execute count','user commits','user rollbacks')");
        gv_system_event = connection.prepareStatement("select inst_id, event, total_waits, total_timeouts, time_waited from gv$system_event where event in ('db file scattered read','db file sequential read','log file sync')");

        // session info
        my_session = connection.prepareStatement("select SYS_CONTEXT('USERENV', 'INSTANCE') as INST_ID, SYS_CONTEXT('USERENV', 'SID') as SID, sysdate from dual");
        gv_session = connection.prepareStatement("select inst_id, saddr, sid, serial#, username, status, osuser, machine, terminal, program, type, sql_id, module, action, logon_time, last_call_et, event, wait_class, seconds_in_wait, state, blocking_instance, blocking_session from gv$session");
        gv_px_session = connection.prepareStatement("select inst_id, sid, serial#, qcinst_id, qcsid, qcserial#, server_set from gv$px_session");
        gv_session_longops = connection.prepareStatement("select rownum as rownum_for_pk, inst_id, sid, serial#, opname, target, sofar, totalwork, units, (sysdate - start_time)*24*60*60 start_time, time_remaining, sql_id from gv$session_longops where sofar <> totalwork");
        String statisticInList = GvStatnames.getStatisticInList();
        gv_sesstat = connection.prepareStatement("select inst_id, sid, statistic#, value from gv$sesstat where statistic# IN (" + statisticInList + ")");

        // transactions
        gv_transaction = connection.prepareStatement("select inst_id, addr, ses_addr, used_ublk, used_urec, log_io, phy_io, cr_get, cr_change, (sysdate - start_date)*24*60*60 start_date from gv$transaction");

        // temporary segments
        gv_tempseg_usage = connection.prepareStatement("select inst_id, session_addr, sql_id, tablespace, segtype, segblk#, blocks from gv$tempseg_usage");
    }


    //
    public void loadData() throws Exception {
        // load my session
        {
            //
            ResultSet rs = my_session.executeQuery();
            rs.next();
            MySession.setInstId(rs.getString("INST_ID"));
            MySession.setSid(rs.getString("SID"));
            MySession.setSysdate(rs.getTimestamp("SYSDATE"));
        }

        // load parameters
        {
            //
            long startTime = System.currentTimeMillis();
            //
            ResultSet rs = gv_parameter.executeQuery();
            GvParameters.clear();
            while (rs.next())
                GvParameters.addParameter(rs);
            //
            long endTime = System.currentTimeMillis();
            GvParameters.setFetchTime(endTime - startTime);
        }

        // load sessions
        {
            //
            long startTime = System.currentTimeMillis();
            //
            ResultSet rs = gv_session.executeQuery();
            GvSessions.clear();
            while (rs.next())
                GvSessions.addSession(rs);
            //
            long endTime = System.currentTimeMillis();
            GvSessions.setFetchTime(endTime - startTime);
        }

        // load Parallel Query session flags
        {
            //
            long startTime = System.currentTimeMillis();
            //
            ResultSet rs = gv_px_session.executeQuery();
            while (rs.next())
                GvSessions.addPxSession(rs);
            //
            long endTime = System.currentTimeMillis();
            GvSessions.setGvPxSessionsFetchTime(endTime - startTime);
            //
            GvSessions.postLoadCompletedTasks();
        }

        // load Session statistics
        {
            //
            long startTime = System.currentTimeMillis();
            //
            ResultSet rs = gv_sesstat.executeQuery();
            while (rs.next())
                GvSesstats.addSesstat(rs);
            //
            long endTime = System.currentTimeMillis();
            GvSesstats.setGvSesstatFetchTime(endTime - startTime);
            //
            GvSesstats.postLoadCompletedTasks();
        }

        // load instances
        {
            //
            long startTime = System.currentTimeMillis();
            //
            ResultSet rs = gv_instance.executeQuery();
            GvInstances.clear();
            while (rs.next())
                GvInstances.addInstance(rs);
            //
            long endTime = System.currentTimeMillis();
            GvInstances.setFetchTime(endTime - startTime);
        }

        // load pgastat
        {
            //
            long startTime = System.currentTimeMillis();
            //
            ResultSet rs = gv_pgastat.executeQuery();
            GvPgastats.clear();
            while (rs.next())
                GvPgastats.addItem(rs);
            //
            long endTime = System.currentTimeMillis();
            GvPgastats.setFetchTime(endTime - startTime);
        }

        // load sgainfo
        {
            //
            long startTime = System.currentTimeMillis();
            //
            ResultSet rs = gv_sgainfo.executeQuery();
            GvSgainfos.clear();
            while (rs.next())
                GvSgainfos.addItem(rs);
            //
            long endTime = System.currentTimeMillis();
            GvSgainfos.setFetchTime(endTime - startTime);
        }

        // load sysstat
        {
            //
            long startTime = System.currentTimeMillis();
            //
            ResultSet rs = gv_sysstat.executeQuery();
            GvSysstats.clear();
            while (rs.next())
                GvSysstats.addSysstat(rs);
            //
            long endTime = System.currentTimeMillis();
            GvSysstats.setFetchTime(endTime - startTime);
        }

        // load system_event
        {
            //
            long startTime = System.currentTimeMillis();
            //
            ResultSet rs = gv_system_event.executeQuery();
            GvSystemEvents.clear();
            while (rs.next())
                GvSystemEvents.addSystemEvent(rs);
            //
            long endTime = System.currentTimeMillis();
            GvSystemEvents.setFetchTime(endTime - startTime);
        }

        // load transactions
        {
            //
            long startTime = System.currentTimeMillis();
            //
            ResultSet rs = gv_transaction.executeQuery();
            GvTransactions.clear();
            while (rs.next())
                GvTransactions.addTransaction(rs);
            //
            long endTime = System.currentTimeMillis();
            GvTransactions.setFetchTime(endTime - startTime);
            //
            GvTransactions.postLoadCompletedTasks();
        }

        // load gv$session_longops
        {
            //
            long startTime = System.currentTimeMillis();
            //
            ResultSet rs = gv_session_longops.executeQuery();
            GvSessionLongops.clear();
            while (rs.next())
                GvSessionLongops.addSessionLongop(rs);
            //
            long endTime = System.currentTimeMillis();
            GvSessionLongops.setFetchTime(endTime - startTime);
        }

        // load gv$session_longops
        {
            //
            long startTime = System.currentTimeMillis();
            //
            ResultSet rs = gv_tempseg_usage.executeQuery();
            GvTempsegUsages.clear();
            while (rs.next())
                GvTempsegUsages.addTempsegUsage(rs);
            //
            long endTime = System.currentTimeMillis();
            GvTempsegUsages.setFetchTime(endTime - startTime);
            //
            GvTempsegUsages.postLoadCompletedTasks();
        }
    }

    //
    public void getOutputDatabase(StringBuffer sb) throws Exception {
        //
        ResultSet rs = gv_database.executeQuery();
        List<ArrayList> listDatabase = new ArrayList<>();
        //
        ArrayList<Object> sysdate = new ArrayList<>(2);
        sysdate.add("SYSDATE");
        sysdate.add(MySession.getSysdateFormatted());
        listDatabase.add(sysdate);
        //
        List<ArrayList> resultSetList = Str.convertResultSetToListArray(rs);
        listDatabase.addAll(resultSetList);
        //
        ArrayList<Object> dbBlockSize = new ArrayList<>(2);
        dbBlockSize.add(GvParameter.NAME.db_block_size.name());
        dbBlockSize.add(GvParameters.getDbBlockSize());
        listDatabase.add(dbBlockSize);
        //
        Str.convertListArrayToStringBufferSameLine(listDatabase, sb);
    }

    //
    public void getOutputActiveSession(StringBuffer sb) {
        //
        GvSessions.getActiveSessions(sb);
    }

    public void getOutputInstance(StringBuffer sb) {
        //
        GvInstances.getInstances(sb);
    }

    public void getOutputBlockingTree(StringBuffer sb) {
        //
        GvSessions.getBlockingTree(sb);
    }

    public void getOutputTransaction(StringBuffer sb) {
        //
        GvTransactions.getOutputTransaction(sb);
    }

    public void getOutputSessionLongops(StringBuffer sb) {
        GvSessionLongops.getOutputSessionLongops(sb);
    }

    public void getOutputTempsegUsage(StringBuffer sb) {
        GvTempsegUsages.getOutputTempsegUsage(sb);
    }
}
