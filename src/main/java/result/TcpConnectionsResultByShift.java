package result;

import enums.Shift;

import java.util.HashMap;
import java.util.List;

public class TcpConnectionsResultByShift {
    private HashMap<Integer, Integer> connectionsMap;

    private Shift shift;

    private long totalShiftsCount;

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public long getTotalShiftsCount() {
        return totalShiftsCount;
    }

    public void setTotalShiftsCount(long totalShiftsCount) {
        this.totalShiftsCount = totalShiftsCount;
    }

    public TcpConnectionsResultByShift(HashMap<Integer, Integer> connectionsMap, Shift shift, long totalShiftsCount) {
        this.connectionsMap = connectionsMap;
        this.shift = shift;
        this.totalShiftsCount = totalShiftsCount;
    }

    public HashMap<Integer, Integer> getConnectionsMap() {
        return connectionsMap;
    }

    public void setConnectionsMap(HashMap<Integer, Integer> connectionsMap) {
        this.connectionsMap = connectionsMap;
    }

}
