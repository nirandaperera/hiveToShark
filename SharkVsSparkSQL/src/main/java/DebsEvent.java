/**
 * Created by niranda on 8/19/14.
 */

import java.io.Serializable;


public class DebsEvent implements Serializable {

    /**
     * id â€“ a unique identifier of the measurement [64 bit unsigned integer value]
     * timestamp â€“ timestamp of measurement (number of seconds since January 1, 1970, 00:00:00 GMT) [32 bit unsigned integer value]
     * value â€“ the measurement [32 bit floating point]
     * property â€“ type of the measurement: 0 for work or 1 for load [boolean]
     * plug_id â€“ a unique identifier (within a household) of the smart plug [32 bit unsigned integer value]
     * household_id â€“ a unique identifier of a household (within a house) where the plug is located [32 bit unsigned integer value]
     * house_id â€“ a unique identifier of a house where the household with the plug is located [32 bit unsigned integer value]
     */
 /*
 String tbl_props = "id bigint, " +
            "time_stamp int, " +
            "value float, " +
            "measure_type boolean, " +
            "plug_id int, " +
            "household_id int, " +
            "house_id int ";
            */


    private long id;
    private int timeStamp, plugId, householdId, houseId;
    private float value;
    private boolean measureType = true;

    public void setHouseholdId(int householdId) {
        this.householdId = householdId;
    }

    public void setHouseId(int houseId) {
        this.houseId = houseId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setMeasureType(boolean measureType) {
        this.measureType = measureType;
    }

    public void setPlugId(int plugId) {
        this.plugId = plugId;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    public int getHouseholdId() {
        return householdId;
    }

    public int getHouseId() {
        return houseId;
    }

    public int getPlugId() {
        return plugId;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public long getId() {
        return id;
    }

    public boolean getMeasureType() {
        return measureType;
    }

    public String convertToString() {
        return Long.toString(id) + " " +
                Integer.toString(timeStamp) + " " +
                Integer.toString(plugId) + " " +
                Integer.toString(householdId) + " " +
                Integer.toString(houseId) + " " +
                Float.toString(value) + " " +
                Boolean.toString(measureType);
    }
}
