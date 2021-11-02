package result;

public class SafetyResult {
    public SafetyResult(int httpsCount, int httpCount) {
        this.httpsCount = httpsCount;
        this.httpCount = httpCount;
    }

    private int httpsCount;
    private int httpCount;

    public int getHttpsCount() {
        return httpsCount;
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
