package database;

import java.math.BigDecimal;
import java.sql.ResultSet;

/**
 *
 */
class GvPgastat {

    //
    private String instId;     // NUMBER
    private NAME name;       // VARCHAR2
    private BigDecimal value;  // NUMBER

    //
    public enum NAME {
        TOTAL_PGA_ALLOCATED("total PGA allocated");
        //
        private String value;

        //
        private NAME(String value) {
            this.value = value;
        }

        //
        public String getValue() {
            return value;
        }
    }

    //
    enum COLS {
        INST_ID, NAME, VALUE
    }

    //
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

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    //
    //
    public GvPgastat(ResultSet rs) throws Exception {
        setInstId(rs.getString(COLS.INST_ID.name()));
        setName(rs.getString(COLS.NAME.name()));
        setValue(rs.getBigDecimal(COLS.VALUE.name()));
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
