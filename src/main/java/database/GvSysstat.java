package database;

/**
 *
 */
class GvSysstat {
    //
    private String instId;
    private String name;
    private long value;
    private long valueDiff;

    //
    public GvSysstat(String instId, String name, long value) {
        setInstId(instId);
        setName(name);
        setValue(value);
    }

    //
    //
    public String getInstId() {
        return instId;
    }

    public void setInstId(String instId) {
        this.instId = instId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    long getValue() {
        return value;
    }

    void setValue(long value) {
        this.value = value;
    }

    public long getValueDiff() {
        return valueDiff;
    }

    void setValueDiff(long valueDiff) {
        this.valueDiff = valueDiff;
    }

    //
    //
    public void setNextValue(long nextValue) {
        setValueDiff(nextValue - getValue());
        setValue(nextValue);
    }

    //
    public static String getPrimaryKey(String instId, String name) {
        return instId + "," + name;
    }

}
