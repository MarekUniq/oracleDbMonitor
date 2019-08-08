package database;

import java.math.BigDecimal;
import java.sql.ResultSet;

/**
 *
 */
class GvSgainfo {

    //
    private String instId;     // NUMBER
    private NAME name;       // VARCHAR2
    private BigDecimal bytes;  // NUMBER

    //
    public enum NAME {
        BUFFER_CACHE_SIZE("Buffer Cache Size"), SHARED_POOL_SIZE("Shared Pool Size"), LARGE_POOL_SIZE("Large Pool Size"), //
        FREE_SGA_MEMORY_AVAILABLE("Free SGA Memory Available"), SHARED_IO_POOL_SIZE("Shared IO Pool Size");
        //
        private String value;

        //
        private NAME(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }

    //
    enum COLS {
        INST_ID, NAME, BYTES
    }

    public String getInstId() {
        return instId;
    }

    public void setInstId(String instId) {
        this.instId = instId;
    }

    public NAME getName() {
        return name;
    }

    public void setName(String nameString) {
        //
        for (NAME name : NAME.values()) {
            if (name.getValue().equals(nameString)) {
                this.name = name;
                return;
            }
        }
        throw new RuntimeException("Unexpected value: " + nameString);
    }

    public BigDecimal getBytes() {
        return bytes;
    }

    public void setBytes(BigDecimal bytes) {
        this.bytes = bytes;
    }


    //
    //
    public GvSgainfo(ResultSet rs) throws Exception {
        setInstId(rs.getString(COLS.INST_ID.name()));
        setName(rs.getString(COLS.NAME.name()));
        setBytes(rs.getBigDecimal(COLS.BYTES.name()));
    }

    //
    public static String getPrimaryKey(String instId, NAME name) {
        return instId + "," + name.getValue();
    }

    //
    public String getPrimaryKey() {
        return getPrimaryKey(getInstId(), getName());
    }

}
