package common;

/**
 *
 */
public class CommandLineArgument {
    //
    public static enum ARGS {
        DBM_INTERVAL //
        , DBM_HOST, DBM_PORT //
        , DBM_USER, DBM_PSWD, DBM_URL //
        //
        , MAX_ACTIVE_SESSIONS_ROWS, MAX_TRANSACTIONS_ROWS, MAX_LONG_OPERATIONS_ROWS, MAX_TEMP_SEGMENT_USAGE_ROWS //
        //
        , SID, INST_ID
    }

    //
    private static int updateInterval = 30000; // in milli-seconds, default: 30 sec
    private static String listeningHostName = "localhost";
    private static int listeningHostPort;
    private static String username;
    private static String password;
    private static String url;
    //
    private static int maxActiveSessionsRows = Integer.MAX_VALUE;
    private static int maxTransactionsRows = Integer.MAX_VALUE;
    private static int maxLongOperationsRows = Integer.MAX_VALUE;
    private static int maxTempSegmentUsageRows = Integer.MAX_VALUE;
    //
    private static int sid;
    private static int instId;

    //
    public static int getUpdateInterval() {
        return updateInterval;
    }

    public static double getUpdateIntervalInSeconds() {
        return CommandLineArgument.getUpdateInterval() / 1000d;
    }

    public static void setUpdateInterval(int updateInterval) {
        CommandLineArgument.updateInterval = updateInterval;
        //
        if (getUpdateInterval() < 1000) {
            throw new RuntimeException("Argument value too small: " + ARGS.DBM_INTERVAL + " : " + getUpdateInterval());
        }
    }

    public static void setUpdateInterval(String updateInterval) {
        setUpdateInterval(Integer.parseInt(updateInterval));
        Log.println(ARGS.DBM_INTERVAL + " : " + getUpdateInterval());
    }

    public static String getListeningHostName() {
        return listeningHostName;
    }

    private static void setListeningHostName(String listeningHostName) {
        CommandLineArgument.listeningHostName = listeningHostName;
        Log.println(ARGS.DBM_HOST + " : " + getListeningHostName());
    }

    public static int getListeningHostPort() {
        return listeningHostPort;
    }

    private static void setListeningHostPort(String listeningHostPort) {
        CommandLineArgument.listeningHostPort = Integer.parseInt(listeningHostPort);
        //
        if (getListeningHostPort() <= 1024) {
            throw new RuntimeException("Argument value too small: " + ARGS.DBM_PORT + " : " + getListeningHostPort());
        }
        //
        Log.println(ARGS.DBM_PORT + " : " + getListeningHostPort());
    }

    public static String getUsername() {
        return username;
    }

    private static void setUsername(String username) {
        CommandLineArgument.username = username;
    }

    public static String getPassword() {
        return password;
    }

    private static void setPassword(String password) {
        CommandLineArgument.password = password;
    }

    public static String getUrl() {
        return url;
    }

    private static void setUrl(String url) {
        CommandLineArgument.url = url;
    }

    public static int getMaxActiveSessionsRows() {
        return maxActiveSessionsRows;
    }

    public static void setMaxActiveSessionsRows(String maxActiveSessionsRows) {
        CommandLineArgument.maxActiveSessionsRows = Integer.parseInt(maxActiveSessionsRows);
        Log.println(ARGS.MAX_ACTIVE_SESSIONS_ROWS + " : " + getMaxActiveSessionsRows());
    }

    public static int getMaxTransactionsRows() {
        return maxTransactionsRows;
    }

    public static void setMaxTransactionsRows(String maxTransactionsRows) {
        CommandLineArgument.maxTransactionsRows = Integer.parseInt(maxTransactionsRows);
        Log.println(ARGS.MAX_TRANSACTIONS_ROWS + " : " + getMaxTransactionsRows());
    }

    public static int getMaxLongOperationsRows() {
        return maxLongOperationsRows;
    }

    public static void setMaxLongOperationsRows(String maxLongOperationsRows) {
        CommandLineArgument.maxLongOperationsRows = Integer.parseInt(maxLongOperationsRows);
        Log.println(ARGS.MAX_LONG_OPERATIONS_ROWS + " : " + getMaxLongOperationsRows());
    }

    public static int getMaxTempSegmentUsageRows() {
        return maxTempSegmentUsageRows;
    }

    public static void setMaxTempSegmentUsageRows(String maxTempSegmentUsageRows) {
        CommandLineArgument.maxTempSegmentUsageRows = Integer.parseInt(maxTempSegmentUsageRows);
        Log.println(ARGS.MAX_TEMP_SEGMENT_USAGE_ROWS + " : " + getMaxTempSegmentUsageRows());
    }

    public static void setSid(String sid) {
        CommandLineArgument.sid = Integer.parseInt(sid);
    }

    public static void setInstId(String instId) {
        CommandLineArgument.instId = Integer.parseInt(instId);
    }

    public static int getSid() {
        return sid;
    }

    public static int getInstId() {
        return instId;
    }

    //
    public static void printHelp() throws Exception {
        //
        Thread.sleep(100);
        //
        Log.errPrintln("Usage:");
        Log.errPrintln("  java oracleDbMonitor/OracleDbMonitor");
        Log.errPrintln("Arguments or Environment variables:");
        Log.errPrintln("  DBM_INTERVAL - database polling interval in milliseconds");
        Log.errPrintln("  DBM_HOST     - telnet server hostname / ip address - NB! java serves result over telnet server");
        Log.errPrintln("  DBM_PORT     - telnet server port");
        Log.errPrintln("  DBM_USER     - oracle database username");
        Log.errPrintln("  DBM_PSWD     - oracle database password");
        Log.errPrintln("  DBM_URL      - oracle database connect string, example: jdbc:oracle:oci:@db_url");
        Log.errPrintln("  MAX_ACTIVE_SESSIONS_ROWS    - max number of active sessions    to print, zero does not print section, default is unlimited");
        Log.errPrintln("  MAX_TRANSACTIONS_ROWS       - max number of transaction        to print, zero does not print section, default is unlimited");
        Log.errPrintln("  MAX_LONG_OPERATIONS_ROWS    - max number of long operations    to print, zero does not print section, default is unlimited");
        Log.errPrintln("  MAX_TEMP_SEGMENT_USAGE_ROWS - max number of temporary segments to print, zero does not print section, default is unlimited");
        Log.errPrintln("Comments:");
        Log.errPrintln("  - arguments can be passed in two ways: (1) as command line arguments or (2) as environment variables");
        Log.errPrintln("    command line argument overrides environment variables");
        Log.errPrintln("  - output of telnet server can be stored into log files using oracleDbMonitor/ReadTelnetSocket");
        Log.errPrintln("");
        Log.errPrintln("Usage:");
        Log.errPrintln("  java oracleDbMonitor/ReadTelnetSocket <host/ip> <port> <log_file_suffix>");
        Log.errPrintln("Arguments:");
        Log.errPrintln("  <host/ip>         - telnet server hostname / ip address");
        Log.errPrintln("  <port>            - telnet server port");
        Log.errPrintln("  <log_file_suffix> - log file name is prefix 'yyyy-MM-dd_' + <log_file_suffix>");
        Log.errPrintln("Comments:");
        Log.errPrintln("  - output log files can be viewed later using 'less -r' or 'less -R'");
        Log.errPrintln("In telnet session:");
        Log.errPrintln("  - q|Q - quit; p|P - pause; +/- - increase/decrease polling interval");
        Log.errPrintln("");
        Log.errPrintln("Usage:");
        Log.errPrintln("  java oracleDbMonitor/OracleSystemEventMonitor");
        Log.errPrintln("Arguments or Environment variables:");
        Log.errPrintln("  SID     - gv$session.sid     to monitor");
        Log.errPrintln("  INST_ID - gv$session.inst_id to monitor");
        Log.errPrintln("");
        Log.errPrintln("Usage:");
        Log.errPrintln("  java oracleDbMonitor/OracleSysstatMonitor");
        Log.errPrintln("Arguments or Environment variables:");
        Log.errPrintln("  SID     - gv$session.sid     to monitor");
        Log.errPrintln("  INST_ID - gv$session.inst_id to monitor");
        //
        Thread.sleep(100);
    }

    //
    //
    private static void setArgumentValueUpdateInterval(ARGS arg, String envValue) {
        switch (arg) {
            case DBM_INTERVAL:
                setUpdateInterval(envValue);
                break;
            case DBM_HOST:
                setListeningHostName(envValue);
                break;
            case DBM_PORT:
                setListeningHostPort(envValue);
                break;
            case DBM_USER:
                setUsername(envValue);
                break;
            case DBM_PSWD:
                setPassword(envValue);
                break;
            case DBM_URL:
                setUrl(envValue);
                break;
            case MAX_ACTIVE_SESSIONS_ROWS:
                setMaxActiveSessionsRows(envValue);
                break;
            case MAX_TRANSACTIONS_ROWS:
                setMaxTransactionsRows(envValue);
                break;
            case MAX_LONG_OPERATIONS_ROWS:
                setMaxLongOperationsRows(envValue);
                break;
            case MAX_TEMP_SEGMENT_USAGE_ROWS:
                setMaxTempSegmentUsageRows(envValue);
                break;
            case SID:
                setSid(envValue);
                break;
            case INST_ID:
                setInstId(envValue);
                break;
            default:
                throw new RuntimeException("invalid argument: " + arg.name());
        }
    }

    //
    public static void decodeArguments(String[] args) throws Exception {
        //
        printHelp();
        //
        // Process environment
        for (ARGS arg : ARGS.values()) {
            String envValue = getEnvironmentVariableValue(arg);
            if (envValue != null) {
                setArgumentValueUpdateInterval(arg, envValue);
            }
        }
        //
        // Process command line
        cmdLineFor:
        for (String cmdLineArg : args) {
            // search for match
            for (ARGS arg : ARGS.values()) {
                // many allowed separators
                String start1 = arg.name() + "=";
                String start2 = arg.name() + ":";
                // if match
                if (cmdLineArg.startsWith(start1) || cmdLineArg.startsWith(start2)) {
                    // get value
                    String value = cmdLineArg.substring(start1.length()).trim();
                    // set value if not null
                    if (value != null && value.length() > 0) {
                        setArgumentValueUpdateInterval(arg, value);
                        continue cmdLineFor;
                    }
                }
            }
            // unknown argument
            throw new RuntimeException("invalid argument: " + cmdLineArg);
        }
    }

    //
    //
    //
    private static String getEnvironmentVariableValue(ARGS name) {
        return System.getenv().get(name.name());
    }

}
