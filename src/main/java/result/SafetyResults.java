package result;

import java.util.ArrayList;
import java.util.List;

public class SafetyResults {

    public SafetyResults() {
        this.safetyResults = new ArrayList<>();
    }

    private List<SafetyResult> safetyResults;
    public void addResult(SafetyResult safetyResult) {
        this.safetyResults.add(safetyResult);
    }

    public List<SafetyResult> getResults() {
        return new ArrayList<>(this.safetyResults);
    }
}
