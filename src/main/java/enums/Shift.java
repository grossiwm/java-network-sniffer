package enums;

import java.time.Instant;

public enum Shift {
    MORNING(6, 13), EVENING(13,18), NIGHT(18, 24), DAWN(0, 6);

    Shift(int start, int end) {
        this.start=start;
        this.end=end;
    }

    private int start;
    private int end;

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
