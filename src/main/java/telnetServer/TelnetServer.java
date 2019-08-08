package telnetServer;

import common.CommandLineArgument;
import common.Log;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 */
public class TelnetServer implements Runnable {

    // server socket
    private ServerSocket serverSocket;
    private final TelnetMessageProvider messageProviderInterface;
    // TelnetSession counter
    private long telnetSessionTotalCounter = 0;
    private long telnetSessionCurrentCount = 0;
    //
    private static final int maxAllowedConnections = 2;
    // Thread name prefix
    public static final String THREAD_GROUP_NAME_PREFIX = "TelnetServer";

    //
    long getTelnetSessionTotalCounter() {
        return telnetSessionTotalCounter;
    }

    public long getTelnetSessionCurrentCount() {
        return telnetSessionCurrentCount;
    }

    public long incrementTelnetSessionCurrentCount() {
        return telnetSessionCurrentCount++;
    }

    public long decrementTelnetSessionCurrentCount() {
        return telnetSessionCurrentCount--;
    }

    public int getMaxAllowedConnections() {
        return maxAllowedConnections;
    }

    //
    public TelnetServer(TelnetMessageProvider messageProviderInterface) throws Exception {
        this.messageProviderInterface = messageProviderInterface;
        //
        String listeningAddress = CommandLineArgument.getListeningHostName();
        int listeningPortNumber = CommandLineArgument.getListeningHostPort();
        //
        InetAddress inetAddress;
        if (listeningAddress != null)
            inetAddress = Inet4Address.getByName(listeningAddress);
        else
            inetAddress = Inet4Address.getLocalHost();
        //
        serverSocket = new ServerSocket(listeningPortNumber, 0, inetAddress);
    }

    @SuppressWarnings({"InfiniteLoopStatement"})
    public void run() {
        //
        Log.println("TelnetServer started");
        Log.println("Listening address: " + serverSocket.toString());
//    Log.println("isDaemon(): " + Thread.currentThread().isDaemon());
        //
        try {
            //
            ThreadGroup sessionThreadGroup = new ThreadGroup(TelnetSession.THREAD_GROUP_NAME_PREFIX);
            //
            while (true) {
                Socket socket = serverSocket.accept();
                telnetSessionTotalCounter++;
                incrementTelnetSessionCurrentCount();
                Thread thread =
                        new Thread(sessionThreadGroup, new TelnetSession(this, socket, messageProviderInterface)
                                , TelnetSession.THREAD_GROUP_NAME_PREFIX + "-" + getTelnetSessionTotalCounter());
                thread.setDaemon(true);
                thread.start();
            }
        } catch (IOException e) {
            Log.println("failure", e);
        }

        //
        try {
//      if (serverSocket != null)
            serverSocket.close();
        } catch (IOException e) {
            Log.println("serverSocket.close() failed", e);
        }

        //
        Log.println("TelnetServer failed");
    }

}
