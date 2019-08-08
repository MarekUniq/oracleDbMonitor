package oracleDbMonitor;

import common.CommandLineArgument;
import common.Log;
import common.Str;
import database.DbConnection;
import telnetServer.TelnetMessageProvider;

import java.sql.SQLException;

/**
 *
 */
public class OracleDbMonitorImpl implements Runnable, TelnetMessageProvider {

    // counter for dead detection, if not updated then thread is dead
    private int loopSequence = 0;
    //
    private DbConnection dbConnection;


    // in milliseconds
    int getUpdateInterval() {
        return CommandLineArgument.getUpdateInterval();
    }

    //
    public int getLoopSequence() {
        return loopSequence;
    }

    @SuppressWarnings({"InfiniteLoopStatement"})
    public void run() {
        //
        try {
            //
            while (true) {
                try {
                    dbConnection = new DbConnection();
                    //
                    long nextTime = System.currentTimeMillis() + getUpdateInterval();
                    //
                    while (true) {
                        loopSequence++;
                        //
                        updateData();

                        // equal interval management
                        long currentTime = System.currentTimeMillis();
                        // if time is passed
                        while (currentTime >= nextTime) {
                            nextTime += getUpdateInterval();
                        }

                        if (nextTime >= currentTime) {
                            Thread.sleep(nextTime - currentTime);
                        }
                        //
                        nextTime += getUpdateInterval();
                    }
                } catch (SQLException e) {
                    String errorStack = Str.throwableToString(e);
                    Log.println("SQLException" + errorStack);
                    int sleepTimeMin = 5;
                    setTelnetMessage(Log.getLinePrefix() + " : sleep " + sleepTimeMin + " minute"
                            + Log.EOL
                            + Str.convertUnixToDos(errorStack)
                            + Log.EOL
                    );
                    if (dbConnection != null) {
                        dbConnection.close();
                        dbConnection = null;
                    }
                    Thread.sleep(sleepTimeMin * 60 * 1000);
                    loopSequence++;
                }
            }
        } catch (Exception e) {
            Log.printException(e);
            System.exit(-1);
        }
        //
    }

    //
    private void updateData() throws Exception {
        //
        StringBuffer sb = new StringBuffer();

        //
        dbConnection.loadData();

        //
        dbConnection.getOutputDatabase(sb);
        //
        dbConnection.getOutputInstance(sb);
        //
        dbConnection.getOutputActiveSession(sb);
        //
        dbConnection.getOutputBlockingTree(sb);
        //
        dbConnection.getOutputTransaction(sb);
        //
        dbConnection.getOutputSessionLongops(sb);
        //
        dbConnection.getOutputTempsegUsage(sb);

        //
        String message = sb.toString();
        setTelnetMessage(message);
//    Log.print(message);
    }

    //
    //
    //
    private String telnetMessage = "not yet";
    private final Object telnetMessageSync = new Object();

    //
    void setTelnetMessage(String telnetMessage) {
        synchronized (telnetMessageSync) {
            this.telnetMessage = telnetMessage;
            telnetMessageSync.notifyAll();
        }
    }

    //
    public String getTelnetMessage(int waitTime) throws InterruptedException {
        synchronized (telnetMessageSync) {
            telnetMessageSync.wait(waitTime);
            return telnetMessage;
        }
    }
}
