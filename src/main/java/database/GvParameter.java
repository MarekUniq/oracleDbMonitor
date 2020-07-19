package database;

import java.sql.ResultSet;

/**
 *
 */
class GvParameter {
    //
    private String instId;            // NUMBER
    private NAME name;            // VARCHAR2
    private String value;            // VARCHAR2

    //
    public enum NAME {
        cpu_count, db_block_size
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
            if (name.name().equals(nameString)) {
                this.name = name;
                return;
            }
        }
        throw new RuntimeException("Unexpected value: " + nameString);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    //
    //
    public GvParameter(ResultSet rs) throws Exception {
        setInstId(rs.getString(COLS.INST_ID.name()));
        setName(rs.getString(COLS.NAME.name()));
        setValue(rs.getString(COLS.VALUE.name()));
    }

    //
    public static String getPrimaryKey(String instId, NAME name) {
        return instId + "," + name.name();
    }

    //
    public String getPrimaryKey() {
        return getPrimaryKey(getInstId(), getName());
    }

}
