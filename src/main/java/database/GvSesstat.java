package database;

/**
 *
 */
class GvSesstat {
    //
    private final String instId;
    private final String sid;
    private final String statistic;
    private long value;
    private long valuePrevious;
    //
    private long updateCounter;

    //
    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.valuePrevious = this.value;
        this.value = value;
    }

    public long getValueDiff() {
        return this.value - this.valuePrevious;
    }

    public long getUpdateCounter() {
        return updateCounter;
    }

    public void setUpdateCounter(long updateCounter) {
        this.updateCounter = updateCounter;
    }

    //
    GvSesstat(String instId, String sid, String statistic, long value) {
        this.instId = instId;
        this.sid = sid;
        this.statistic = statistic;
        this.value = value;
    }

    //
    static String getPrimaryKey(String instId, String sid, String statistic) {
        return instId + "," + sid + "," + statistic;
    }

    String getPrimaryKey() {
        return getPrimaryKey(instId, sid, statistic);
    }

}
