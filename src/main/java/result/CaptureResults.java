package result;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class CaptureResults {

    private Instant begin;
    private Instant end;

    private Long lengthCaptured;

    private Long numberOfPackets;

    public Long getNumberOfPackets() {
        return numberOfPackets;
    }

    public void setNumberOfPackets(Long numberOfPackets) {
        this.numberOfPackets = numberOfPackets;
    }

    public Double getTransmissionRate() {
        return ((double) lengthCaptured)/(ChronoUnit.SECONDS.between(begin, end));
    }

    public Instant getBegin() {
        return begin;
    }

    public void setBegin(Instant begin) {
        this.begin = begin;
    }

    public Instant getEnd() {
        return end;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }

    public Long getLengthCaptured() {
        return lengthCaptured;
    }

    public void setLengthCaptured(Long lengthCaptured) {
        this.lengthCaptured = lengthCaptured;
    }

}
