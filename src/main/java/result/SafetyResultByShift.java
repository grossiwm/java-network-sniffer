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

    @Override
    public String toString() {
        return this.getShift() + " --> " +  super.toString();
    }
}
