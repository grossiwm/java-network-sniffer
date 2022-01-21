package result;

public class RatesIntervalResult {

    private double averageBelowRate;
    private double averageAboveRate;
    private long aboveRateInterval;
    private long belowRateInterval;
    private double averateRate;

    public double getAverageBelowRate() {
        return averageBelowRate;
    }

    public void setAverageBelowRate(double averageBelowRate) {
        this.averageBelowRate = averageBelowRate;
    }

    public double getAverageAboveRate() {
        return averageAboveRate;
    }

    public void setAverageAboveRate(double averageAboveRate) {
        this.averageAboveRate = averageAboveRate;
    }

    public long getAboveRateInterval() {
        return aboveRateInterval;
    }

    public double getAverateRate() {
        return averateRate;
    }

    public void setAverateRate(double averateRate) {
        this.averateRate = averateRate;
    }

    public void setAboveRateInterval(long aboveRateInterval) {
        this.aboveRateInterval = aboveRateInterval;
    }

    public long getBelowRateInterval() {
        return belowRateInterval;
    }

    public void setBelowRateInterval(long belowRateInterval) {
        this.belowRateInterval = belowRateInterval;
    }

    @Override
    public String toString() {
        return this.aboveRateInterval + ";" + this.belowRateInterval + ";" + this.averageAboveRate + ";" + this.averageBelowRate + ";" + this.averateRate;
    }
}
