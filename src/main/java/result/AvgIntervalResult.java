package result;

public class AvgIntervalResult {
    double avgIntervalAboveRate;
    long totalTimeAbove;
    long count;

    public double getAvgIntervalAboveRate() {
        return avgIntervalAboveRate;
    }

    public void setAvgIntervalAboveRate(double avgIntervalAboveRate) {
        this.avgIntervalAboveRate = avgIntervalAboveRate;
    }

    public long getTotalTimeAbove() {
        return totalTimeAbove;
    }

    public void setTotalTimeAbove(long totalTimeAbove) {
        this.totalTimeAbove = totalTimeAbove;
    }

    @Override
    public String toString() {
        return String.valueOf(avgIntervalAboveRate).replace(".", ",") + ";" + totalTimeAbove + ";" + count;
    }

    public AvgIntervalResult(double avgIntervalAboveRate, long totalTimeAbove, long count) {
        this.avgIntervalAboveRate = avgIntervalAboveRate;
        this.totalTimeAbove = totalTimeAbove;
        this.count = count;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
