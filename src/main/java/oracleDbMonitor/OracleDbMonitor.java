package oracleDbMonitor;

import common.CommandLineArgument;
import common.Log;
import telnetServer.TelnetServer;

/**
 *
 */
public class OracleDbMonitor {
    //
    public static void main(String[] args) throws Exception {
        Log.println("Start: DbMonitor");
        CommandLineArgument.decodeArguments(args);
        OracleDbMonitor oracleDbMonitor = new OracleDbMonitor();
        oracleDbMonitor.run();
        oracleDbMonitor.close();
    }

    //
    private void run() throws Exception {
        // DbMonitor
        OracleDbMonitorImpl oracleDbMonitor = new OracleDbMonitorImpl();
        Thread oracleDbMonitorThread = new Thread(oracleDbMonitor);
        oracleDbMonitorThread.setName("DbMonitor");
        oracleDbMonitorThread.start();
        // telnetServer
        Log.println("Start: " + TelnetServer.THREAD_GROUP_NAME_PREFIX);
        TelnetServer telnetServer = new TelnetServer(oracleDbMonitor);
        // create a separate group to have isolated threads
        ThreadGroup threadGroup = new ThreadGroup(TelnetServer.THREAD_GROUP_NAME_PREFIX);
        Thread telnetServerThread = new Thread(threadGroup, telnetServer);
        //    Thread telnetServerThread = new Thread( telnetServer);
        telnetServerThread.setName(TelnetServer.THREAD_GROUP_NAME_PREFIX);
        telnetServerThread.start();
        //
        int dbMonitorWatchdogCounter = oracleDbMonitor.getLoopSequence();
        // watchdog
        while (true) {
            Thread.sleep(30 * 60 * 1000);
            //      Log.println("watchdog");
            //
            int dbMonitorWatchdogCounterNew = oracleDbMonitor.getLoopSequence();
            //
            //
            if (dbMonitorWatchdogCounter == dbMonitorWatchdogCounterNew) {
                Log.println("watchdog: no activity: dbMonitorWatchdogCounter");
                System.exit(-1);
            }
            //
            dbMonitorWatchdogCounter = dbMonitorWatchdogCounterNew;
        }

    }

    //
    @SuppressWarnings({"EmptyMethod"})
    private void close() {
        /* do nothing */
    }

}
