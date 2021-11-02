package result;

import java.util.HashMap;

public interface IConnectionsResult {

    int getTotalCount();

    HashMap<Integer, Integer> getPortsCountHashMap();

    HashMap<Integer, Integer> getKnownPortsCountHashMap();

    HashMap<Integer, Double> getKnownPortsPercentage();

    HashMap<Integer, Double> getPortsPercentage();
}
