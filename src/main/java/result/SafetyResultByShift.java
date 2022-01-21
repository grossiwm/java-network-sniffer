package result;

import enums.Shift;

public class SafetyResultByShift extends SafetyResult{

    public SafetyResultByShift(Shift shift) {
        this.shift = shift;
    }

    private Shift shift;

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    private long connCount;

    public long getConnCount() {
        return connCount;
    }

    public void setConnCount(long connCount) {
        this.connCount = connCount;
    }

    @Override
    public String toString() {
        return this.getShift() + " --> " +  super.toString() + "\nnumber of connections: " + this.connCount;
    }
}
