package telnetServer;

import common.CommandLineArgument;
import common.Log;
import common.Str;

import java.io.*;
import java.net.Socket;

/**
 *
 */
public class TelnetSession implements Runnable {

    public static final String THREAD_GROUP_NAME_PREFIX = "TelnetSession";

    //
    public static final String COLOR_RESET = "\u001B[0m";
    //  public static final String COLOR_BOLD = "\u001B[1m";
    //  public static final String COLOR_UNDERLINE = "\u001B[4m";
    //  public static final String COLOR_REVERSE = "\u001B[7m";
    //
    //  public static final String BLACK = "\u001B[0;30m";
    public static final String RED = "\u001B[0;31m";
    public static final String GREEN = "\u001B[0;32m";
    public static final String YELLOW = "\u001B[0;33m";
    public static final String BLUE = "\u001B[0;34m";
    //  public static final String MAGENTA = "\u001B[0;35m";
    //  public static final String CYAN = "\u001B[0;36m";
    public static final String WHITE = "\u001B[0;37m";
    //
    //  public static final String DARK_BLACK = "\u001B[1;30m";
    public static final String DARK_RED = "\u001B[1;31m";
    public static final String DARK_GREEN = "\u001B[1;32m";
    public static final String DARK_YELLOW = "\u001B[1;33m";
    public static final String DARK_BLUE = "\u001B[1;34m";
    //  public static final String DARK_MAGENTA = "\u001B[1;35m";
    //  public static final String DARK_CYAN = "\u001B[1;36m";
    public static final String DARK_WHITE = "\u001B[1;37m";

    //  public static final String BACKGROUND_BLACK = "\u001B[40m";
    //  public static final String BACKGROUND_RED = "\u001B[41m";
    //  public static final String BACKGROUND_GREEN = "\u001B[42m";
    //  public static final String BACKGROUND_YELLOW = "\u001B[43m";
    //  public static final String BACKGROUND_BLUE = "\u001B[44m";
    //  public static final String BACKGROUND_MAGENTA = "\u001B[45m";
    //  public static final String BACKGROUND_CYAN = "\u001B[46m";
    //  public static final String BACKGROUND_WHITE = "\u001B[47m";

    //
    private final TelnetServer telnetServer;
    //
    private Socket sessionSocket;
    private final TelnetMessageProvider messageProviderInterface;

    //
    TelnetSession(TelnetServer telnetServer, Socket sessionSocket, TelnetMessageProvider messageProviderInterface) {
        //
        this.telnetServer = telnetServer;
        this.sessionSocket = sessionSocket;
        this.messageProviderInterface = messageProviderInterface;
    }

    //
    public static final byte[] clearScreenSequence = {0x1b, 0x5b, 0x48, 0x1b, 0x5b, 0x32, 0x4a};

    //
    public void run() {
        //
        Log.println("TelnetSession started : " + telnetServer.getTelnetSessionTotalCounter() + " getTelnetSessionCurrentCount(): " + telnetServer.getTelnetSessionCurrentCount());
        Log.println("getRemoteSocketAddress(): " + sessionSocket.getRemoteSocketAddress());
        //
        boolean cleanExit = false;
        //
        try {
            // >= because main (socket.accept()) thread is first
            if (telnetServer.getTelnetSessionCurrentCount() > telnetServer.getMaxAllowedConnections()) {
                String message = "max allowed sessions limit " + telnetServer.getMaxAllowedConnections() + " exceeded. Exit.";
                Log.println(message);
                telnetWrite(message);
            } else
                doWork();
            //
            cleanExit = true;
        } catch (Exception e) {
            Log.println("failure", e);
        }

        //
        if (sessionSocket != null) {
            try {
                sessionSocket.close();
            } catch (IOException e) {
                Log.println("sessionSocket.close() failed", e);
            }
        }

        telnetServer.decrementTelnetSessionCurrentCount();
        //
        if (cleanExit)
            Log.println("TelnetSession session ended");
        else
            Log.println("TelnetSession failed");
    }


    //
    private byte[] getByteMessage(String message) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(clearScreenSequence);
        outputStream.write(message.getBytes("utf-8"));
        outputStream.close();
        return outputStream.toByteArray();
    }

    //
    private void doWork() throws Exception {

        //
        InputStream stdinInputStream = sessionSocket.getInputStream();
        final Reader stdinReader = new InputStreamReader(stdinInputStream, "utf8");
        String message = this.messageProviderInterface.getTelnetMessage(1);
        final Thread callingThread = Thread.currentThread();
        final Pause pause = new Pause();
        //
        //
        Runnable stdinReaderRunnable = new Runnable() {
            //
            public void run() {
                char[] chars = new char[1];
                int len;
                String logMessage;
                try {
                    while ((len = stdinReader.read(chars)) > 0) {
                        char c = chars[0];
                        char upperC = Character.toUpperCase(c);
//            Log.println("len: " + len + " char: " + c);
                        if (upperC == 'P') {
                            logMessage = " togglePause()";
                            Log.println(logMessage);
                            telnetWrite(logMessage);
                            pause.togglePause();
                        } else if (c == '+') {
                            int newUpdateInterval = CommandLineArgument.getUpdateInterval() + 1000;
                            logMessage = " IncUpdateInterval: " + Str.surroundWithSquarBracket(Integer.toString(newUpdateInterval));
                            Log.println(logMessage);
                            telnetWrite(logMessage);
                            CommandLineArgument.setUpdateInterval(newUpdateInterval);
                        } else if (c == '-') {
                            int newUpdateInterval = CommandLineArgument.getUpdateInterval() - 1000;
                            if (newUpdateInterval < 1000)
                                newUpdateInterval = 1000;
                            logMessage = " DecUpdateInterval: " + Str.surroundWithSquarBracket(Integer.toString(newUpdateInterval));
                            Log.println(logMessage);
                            telnetWrite(logMessage);
                            CommandLineArgument.setUpdateInterval(newUpdateInterval);
                        } else if (upperC == 'Q') {
                            telnetWrite(" Quit");
                            callingThread.interrupt();
                            break;
                        } else
                            telnetWrite(" [" + Integer.toHexString(c) + "]");
                    }
                } catch (IOException e) {
                    Log.printException(e);
                }
            }
        };

        Thread stdinReaderThread = new Thread(stdinReaderRunnable);
        stdinReaderThread.setDaemon(true);
        stdinReaderThread.setName(callingThread.getName() + "-stdin");
        stdinReaderThread.start();

        //
        while (true) {
            //
            pause.checkPause();
            //
            telnetWrite(getByteMessage(message
                    + "sessions: " + (Thread.activeCount() - 1)
                    + " | clnt: " + sessionSocket.getRemoteSocketAddress()
                    + " | srv: " + sessionSocket.getLocalSocketAddress()
                    + " | url: " + CommandLineArgument.getUrl()
                    + "\n\r"
            ));

            //
            try {
                message = this.messageProviderInterface.getTelnetMessage(0);
            } catch (InterruptedException e) {
                Log.println("User requested exit");
                sessionSocket.close();
                sessionSocket = null;
                break;
            }
        }
        //
        stdinReaderThread.join();
    }

    class Pause {
        private boolean pause = false;

        public synchronized void togglePause() throws IOException {
            pause = !pause;
            if (pause)
                telnetWrite("pause enabled");
            else
                telnetWrite("pause disabled");
            notifyAll();
        }

        public synchronized void checkPause() throws InterruptedException {
            if (pause)
                wait();
        }
    }


    //
    private void telnetWrite(byte[] message) throws IOException {
        sessionSocket.getOutputStream().write(message);
    }

    private void telnetWrite(String message) throws IOException {
        sessionSocket.getOutputStream().write(message.getBytes("utf8"));
    }

}
