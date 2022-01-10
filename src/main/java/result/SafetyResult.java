package result;

public class SafetyResult {
    public SafetyResult(int httpsCount, int httpCount) {
        this.httpsCount = httpsCount;
        this.httpCount = httpCount;
    }

    public SafetyResult() {
    }

    private int httpsCount;
    private int httpCount;

    public int getHttpsCount() {
        return httpsCount;
    }

    public void setHttpsCount(int httpsCount) {
        this.httpsCount = httpsCount;
    }

    public void setHttpCount(int httpCount) {
        this.httpCount = httpCount;
    }

    public int getHttpCount() {
        return httpCount;
    }

    public double getSafePercent() {
        return ((double) httpsCount)/(httpCount+httpsCount)*100;
    }

    public double getunSafePercent() {
        return ((double) httpCount)/(httpCount+httpsCount)*100;
    }

    @Override
    public String toString() {
        return "safe: " + getSafePercent() + " unsafe: " + getunSafePercent();
    }
}
