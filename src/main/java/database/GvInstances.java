package database;

import common.Str;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 */
class GvInstances {

    // map sorted by primary key inst_id
    private static final SortedMap<String, GvInstance> instancesMapByPrimaryKey = new TreeMap<>();
    //
    private static long fetchTime;

    //
    public static void setFetchTime(long fetchTime) {
        GvInstances.fetchTime = fetchTime;
    }

    private static long getFetchTime() {
        return fetchTime;
    }

    //
    public static void clear() {
        instancesMapByPrimaryKey.clear();
    }

    //
    public static void addInstance(ResultSet rs) throws Exception {
        GvInstance instance = new GvInstance(rs);
        String key = instance.getPrimaryKey();
        GvInstance oldInstance = instancesMapByPrimaryKey.put(key, instance);
        if (oldInstance != null)
            throw new RuntimeException("Bug, duplicate GvInstance : " + oldInstance.getPrimaryKey());
    }


    //
    public static void getInstances(StringBuffer sb) {
        //
        List<GvInstance> instanceList = new ArrayList<>();
        instanceList.addAll(instancesMapByPrimaryKey.values());

        //
        int columnCount = 16;
        int rowCount = 1 + instanceList.size();
        int[] alignment = new int[columnCount];
        //
        int colNum = 0;
        int rowNum = 0;
        String[][] instanceArray = new String[columnCount][rowCount];

        // Column Names
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        instanceArray[colNum++][rowNum] = GvInstance.columnNames[GvInstance.INST_ID];
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        instanceArray[colNum++][rowNum] = "seqRead";
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        instanceArray[colNum++][rowNum] = "scaRead";
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        instanceArray[colNum++][rowNum] = "logSync";
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        instanceArray[colNum++][rowNum] = "phyRead";
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        instanceArray[colNum++][rowNum] = "phyWrite";
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        instanceArray[colNum++][rowNum] = "ConDbbChg";
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        instanceArray[colNum++][rowNum] = "Redo";
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        instanceArray[colNum++][rowNum] = "Cpu";
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        instanceArray[colNum++][rowNum] = "RecvSentTrip";
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        instanceArray[colNum++][rowNum] = "CalExeComRol";
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        instanceArray[colNum++][rowNum] = "Pga/Buf/Shr/Lrg/IO/Free";
        alignment[colNum] = Str.ALIGNMENT_RIGHT;
        instanceArray[colNum++][rowNum] = GvInstance.columnNames[GvInstance.STARTUP_TIME];
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        instanceArray[colNum++][rowNum] = GvInstance.columnNames[GvInstance.VERSION];
        alignment[colNum] = Str.ALIGNMENT_LEFT;
        instanceArray[colNum++][rowNum] = GvInstance.columnNames[GvInstance.HOST_NAME];

        // Column Values
        for (int i = 0; i < instanceList.size(); i++) {
            GvInstance instance = instanceList.get(i);
            colNum = 0;
            rowNum = i + 1;
            instanceArray[colNum++][rowNum] = instance.getInstId() + "(" + GvParameters.getCpuCount(instance.getInstId()) + ")";
            instanceArray[colNum++][rowNum] = GvSystemEvents.getStats(instance.getInstId(), GvSystemEvents.DB_FILE_SEQUENTIAL_READ);
            instanceArray[colNum++][rowNum] = GvSystemEvents.getStats(instance.getInstId(), GvSystemEvents.DB_FILE_SCATTERED_READ);
            instanceArray[colNum++][rowNum] = GvSystemEvents.getStats(instance.getInstId(), GvSystemEvents.LOG_FILE_SYNC);
            instanceArray[colNum++][rowNum] = GvSysstats.getPhysicalReadsStats(instance.getInstId());
            instanceArray[colNum++][rowNum] = GvSysstats.getPhysicalWriteStats(instance.getInstId());
            instanceArray[colNum++][rowNum] = GvSysstats.getLogicalReadStats(instance.getInstId());
            instanceArray[colNum++][rowNum] = GvSysstats.getStatsString(instance.getInstId(), GvStatnames.REDO_SIZE);
            instanceArray[colNum++][rowNum] = GvSysstats.getCpuUsedByThisSessionStats(instance.getInstId());
            instanceArray[colNum++][rowNum] = GvSysstats.getSqlNetStats(instance.getInstId());
            instanceArray[colNum++][rowNum] = GvSysstats.getCallsExecsCommitsRollbacksStats(instance.getInstId());
            instanceArray[colNum++][rowNum] = GvPgastats.getPgaAllocated(instance.getInstId()) + "/" + GvSgainfos.getBufferCacheSize(instance.getInstId())
                    + "/" + GvSgainfos.getSharedPoolSize(instance.getInstId()) + "/" + GvSgainfos.getLargePoolSize(instance.getInstId())
                    + "/" + GvSgainfos.getSharedIOPoolSize(instance.getInstId()) + "/" + GvSgainfos.getFreeSGAMemoryAvailable(instance.getInstId())
            ;
            instanceArray[colNum++][rowNum] = Str.formatSecondsNumber(instance.getStartupTime());
            instanceArray[colNum++][rowNum] = instance.getVersion();
            instanceArray[colNum++][rowNum] = Str.rtrunc(instance.getHostName(), 15);
        }

        //
        sb.append("\n\r");
        sb.append("Instances: " + instancesMapByPrimaryKey.size()
                + " / instance,sysstat,system_event,pgastat,sgainfo (" + getFetchTime() + "ms," + GvSysstats.getFetchTime() + "ms," + GvSystemEvents.getFetchTime() + "ms,"
                + GvPgastats.getFetchTime() + "ms," + GvSgainfos.getFetchTime() + "ms)"
                + " / waits/timeouts/waited  bytes/totIOreq  recv/sent/trip\n\r");
        Str.convertArrayToStringBufferAsTable(instanceArray, alignment, sb);
        sb.append("\n\r");
    }

}
