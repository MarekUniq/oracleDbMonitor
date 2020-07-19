package database;

import common.CommandLineArgument;
import common.Log;
import common.Str;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 */
class GvTransactions {
    // map sorted by primary key sid@inst_id
    private static final SortedMap<String, GvTransaction> transactionsMapByPrimaryKey = new TreeMap<>();
    // Set sorted by start_date desc
    private static final SortedSet<GvTransaction> transactionsSetByStartDateDesc = new TreeSet<>(new ComparatorByStartDateDesc());
    //
    private static long fetchTime;

    private static long getFetchTime() {
        return fetchTime;
    }

    public static void setFetchTime(long fetchTime) {
        GvTransactions.fetchTime = fetchTime;
    }

    //
    public static void clear() {
        transactionsMapByPrimaryKey.clear();
        transactionsSetByStartDateDesc.clear();
    }

    //
    public static void addTransaction(ResultSet rs) throws Exception {
        //
        GvTransaction transaction = new GvTransaction(rs);
        GvTransaction oldTransaction;
        //
        String key = transaction.getPrimaryKey();
        oldTransaction = transactionsMapByPrimaryKey.put(key, transaction);
        if (oldTransaction != null) {
            Log.println(transaction.getPrimaryKey());
            Log.println(oldTransaction.getPrimaryKey());
            throw new RuntimeException("duplicate value");
        }
        //
        if (!transactionsSetByStartDateDesc.add(transaction)) {
            throw new RuntimeException("duplicate value");
        }
    }

    //
    public static void postLoadCompletedTasks() {
        //
        for (GvTransaction transaction : transactionsMapByPrimaryKey.values()) {
            String sessAddrKey = GvSession.getPrimaryKeySaddr(transaction.getSesAddr(), transaction.getInstId());
            GvSession session = GvSessions.getSessionsMapBySAddr().get(sessAddrKey);
            //
            if (session != null) {
                if (transaction.getGvSession() == null) {
                    transaction.setGvSession(session);
                }
                else {
                    throw new RuntimeException("duplicate mapping: " + transaction.getPrimaryKey()
                                               + " : " + session.getPrimaryKey()
                                               + " : " + transaction.getGvSession().getPrimaryKey()
                    );
                }
            }
        }
    }

    //
    //
    public static void getOutputTransaction(StringBuffer sb) {
        //
        int maxTransactionsRows = CommandLineArgument.getMaxTransactionsRows();
        if (maxTransactionsRows <= 0) {
            return;
        }
        //
        List<GvTransaction> transactionList = new ArrayList<>(transactionsSetByStartDateDesc);
        //
        // Math.min() allows to restrict # of lines if there are too many
        int rowCount = 1 + Math.min(transactionList.size(), maxTransactionsRows);
        int columnCount = 11;
        int[] alignment = new int[columnCount];
        //
        int colNum = 0;
        int rowNum = 0;
        String[][] transactionArray = new String[columnCount][rowCount];
        // Column Names
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        transactionArray[colNum++][rowNum] = "SES_REF";
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        transactionArray[colNum++][rowNum] = GvTransaction.columnNames[GvTransaction.START_DATE];
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        transactionArray[colNum++][rowNum] = GvTransaction.columnNames[GvTransaction.USED_UBLK];
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        transactionArray[colNum++][rowNum] = GvTransaction.columnNames[GvTransaction.USED_UREC];
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        transactionArray[colNum++][rowNum] = GvTransaction.columnNames[GvTransaction.LOG_IO];
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        transactionArray[colNum++][rowNum] = GvTransaction.columnNames[GvTransaction.PHY_IO];
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        transactionArray[colNum++][rowNum] = GvTransaction.columnNames[GvTransaction.CR_GET];
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        transactionArray[colNum++][rowNum] = GvTransaction.columnNames[GvTransaction.CR_CHANGE];
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        transactionArray[colNum++][rowNum] = GvSession.columnNames[GvSession.USERNAME];
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        transactionArray[colNum++][rowNum] = GvSession.columnNames[GvSession.EVENT];
        // Column Values
        for (int i = 0; i < (rowCount - 1); i++) {
            GvTransaction transaction = transactionList.get(i);
            GvSession session = transaction.getGvSession();
            colNum = 0;
            rowNum = i + 1;
            transactionArray[colNum++][rowNum] = session == null ? "?" : session.getAlterSystemKillSessionReference();
            transactionArray[colNum++][rowNum] = Str.formatSecondsNumber(transaction.getStartDate());
            transactionArray[colNum++][rowNum] = Str.formatDoubleNumber(transaction.getUsedUblk().doubleValue());
            transactionArray[colNum++][rowNum] = Str.formatDoubleNumber(transaction.getUsedUrec().doubleValue());
            transactionArray[colNum++][rowNum] = Str.formatDoubleNumber(transaction.getLogIo().doubleValue());
            transactionArray[colNum++][rowNum] = Str.formatDoubleNumber(transaction.getPhyIo().doubleValue());
            transactionArray[colNum++][rowNum] = Str.formatDoubleNumber(transaction.getCrGet().doubleValue());
            transactionArray[colNum++][rowNum] = Str.formatDoubleNumber(transaction.getCrChange().doubleValue());
            transactionArray[colNum++][rowNum] = session == null ? "?" : session.getUsername();
            transactionArray[colNum++][rowNum] = session == null ? "?" : session.getEvent();
            //      transactionArray[colNum++][rowNum] = Str.rtrunc(transaction.getMachine(), 15);
        }
        //
        sb.append("Transactions: " + transactionsMapByPrimaryKey.size()
                  + ((maxTransactionsRows == Integer.MAX_VALUE) ? "" : " limit: " + maxTransactionsRows)
                  + " / gv$transaction"
                  + " (" + getFetchTime() + "ms)");
        sb.append(Log.EOL);
        Str.convertArrayToStringBufferAsTable(transactionArray, alignment, sb);
        sb.append(Log.EOL);
    }

    //
    //
    //
    private static class ComparatorByStartDateDesc implements Comparator<GvTransaction> {
        //
        public int compare(GvTransaction o1, GvTransaction o2) {
            int result = Integer.valueOf(o2.getStartDate()).compareTo(o1.getStartDate());
            //
            if (result != 0) {
                return result;
            }
            else {
                return o2.getPrimaryKey().compareTo(o1.getPrimaryKey());
            }
        }
    }

}
