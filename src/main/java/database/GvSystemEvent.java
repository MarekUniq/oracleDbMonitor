package database;

/**
 *
 */
class GvSystemEvent {

    //
    private String instId;
    private String event;
    private long totalWaits;
    private long totalTimeouts;
    private long timeWaited;

    //
    private long totalWaitsDiff;
    private long totalTimeoutsDiff;
    private long timeWaitedDiff;

    //
    public String getInstId() {
        return instId;
    }

    void setInstId(String instId) {
        this.instId = instId;
    }

    public String getEvent() {
        return event;
    }

    void setEvent(String event) {
        this.event = event;
    }

    long getTotalWaits() {
        return totalWaits;
    }

    void setTotalWaits(long totalWaits) {
        this.totalWaits = totalWaits;
    }

    long getTotalTimeouts() {
        return totalTimeouts;
    }

    void setTotalTimeouts(long totalTimeouts) {
        this.totalTimeouts = totalTimeouts;
    }

    long getTimeWaited() {
        return timeWaited;
    }

    void setTimeWaited(long timeWaited) {
        this.timeWaited = timeWaited;
    }

    public long getTotalWaitsDiff() {
        return totalWaitsDiff;
    }

    void setTotalWaitsDiff(long totalWaitsDiff) {
        this.totalWaitsDiff = totalWaitsDiff;
    }

    public long getTotalTimeoutsDiff() {
        return totalTimeoutsDiff;
    }

    void setTotalTimeoutsDiff(long totalTimeoutsDiff) {
        this.totalTimeoutsDiff = totalTimeoutsDiff;
    }

    public long getTimeWaitedDiff() {
        return timeWaitedDiff;
    }

    void setTimeWaitedDiff(long timeWaitedDiff) {
        this.timeWaitedDiff = timeWaitedDiff;
    }

    //
    public GvSystemEvent(String instId, String event, long totalWaits, long totalTimeouts, long timeWaited) {
        setInstId(instId);
        setEvent(event);
        setTotalWaits(totalWaits);
        setTotalTimeouts(totalTimeouts);
        setTimeWaited(timeWaited);
    }

    //
    //
    public void setNextValue(long totalWaits, long totalTimeouts, long timeWaited) {
        //
        setTotalWaitsDiff(totalWaits - getTotalWaits());
        setTotalWaits(totalWaits);
        //
        setTotalTimeoutsDiff(totalTimeouts - getTotalTimeouts());
        setTotalTimeouts(totalTimeouts);
        //
        setTimeWaitedDiff(timeWaited - getTimeWaited());
        setTimeWaited(timeWaited);
    }


    //
    public static String getPrimaryKey(String instId, String event) {
        return instId + "," + event;
    }
}
