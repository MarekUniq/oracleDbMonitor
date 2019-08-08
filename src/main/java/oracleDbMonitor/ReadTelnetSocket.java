package oracleDbMonitor;

import common.CommandLineArgument;
import common.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class ReadTelnetSocket {

    //
    private static String logFileSuffix;

    //
    public static void main(String[] args) throws Exception {
        //
        CommandLineArgument.printHelp();
        //
        String serverAddr = null;
        if (args.length > 0) {
            serverAddr = args[0];
            Log.errPrintln("Argument 1: hostname / ip address: " + serverAddr);
        } else {
            Log.errPrintln("Argument 1: hostname / ip address: " + "[is missing]");
            System.exit(-1);
        }

        //
        String serverPortString = null;
        int serverPort = -1;
        if (args.length > 1) {
            serverPortString = args[1];
            Log.errPrintln("Argument 2: port: " + serverPortString);
            serverPort = Integer.parseInt(serverPortString);
        } else {
            Log.errPrintln("Argument 2: port: " + "[is missing]");
            System.exit(-1);
        }

        //
        if (args.length > 2) {
            logFileSuffix = args[2];
            Log.errPrintln("Argument 3: logFileSuffix: " + logFileSuffix);
        } else {
            Log.errPrintln("Argument 3: logFileSuffix: [is missing]");
            System.exit(-1);
        }

        //
        Socket socket = new Socket(serverAddr, serverPort);
        //
        InputStream inputStream = socket.getInputStream();

        byte[] buf = new byte[512];
        while (true) {
            int length = inputStream.read(buf);
            if (length < 0)
                break;
            //
            write(buf, 0, length);
        }
    }

    //
    private static final SimpleDateFormat s =
            new SimpleDateFormat("yyyy-MM-dd");

    //
    public static String getLogFileName() {
        return s.format(new Date()) + "_" + logFileSuffix;
    }

    //
    private static void write(byte[] bytes, int offset, int length) throws Exception {
        File file = new File(getLogFileName());
        boolean APPEND = true;
        FileOutputStream fileOutputStream = new FileOutputStream(file, APPEND);
        fileOutputStream.write(bytes, offset, length);
        fileOutputStream.close();
    }

}
