package database;

/**
 *
 */
class GvSesstat {

    //
    private String instId;
    private String sid;
    private String statistic;
    private long value;
    //
    private long valueDiff;

    //
    long updateCounter;

    //
    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public long getValueDiff() {
        return valueDiff;
    }

    public void setValueDiff(long valueDiff) {
        this.valueDiff = valueDiff;
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

    public void setNextValue(long nextValue) {
        setValueDiff(nextValue - getValue());
        setValue(nextValue);
    }

}
