package result;

import connection.TCPConnection;

import java.util.HashMap;
import java.util.Set;

public class TcpConnectionsResult implements IConnectionsResult {

    private Set<TCPConnection> connections;

    public TcpConnectionsResult(Set<TCPConnection> connections) {
        this.connections = connections;
    }

    @Override
    public int getTotalCount() {
        return connections.size();
    }

    @Override
    public HashMap<Integer, Integer> getPortsCountHashMap() {
        HashMap<Integer, Integer> portsHashMap = new HashMap<>();
        connections.forEach(c -> {
            if (portsHashMap.get(c.getSrcPort()) == null)
                portsHashMap.put(c.getSrcPort(), 0);
            portsHashMap.put(c.getSrcPort(), portsHashMap.get(c.getSrcPort()) + 1);
        });
        return portsHashMap;
    }

    @Override
    public HashMap<Integer, Integer> getKnownPortsCountHashMap() {
        HashMap<Integer, Integer> portsCountHashMap = getPortsCountHashMap();
        portsCountHashMap.entrySet().removeIf(entry -> entry.getKey() > 1023);
        return portsCountHashMap;
    }

    @Override
    public HashMap<Integer, Double> getKnownPortsPercentage() {
        HashMap<Integer, Double> knownPortsPercentageMap = new HashMap<>();
        getKnownPortsCountHashMap().forEach((k, v) -> knownPortsPercentageMap.put(k, ((double) v)/getTotalCount()*100));
        return knownPortsPercentageMap;
    }

    @Override
    public HashMap<Integer, Double> getPortsPercentage() {
        HashMap<Integer, Double> knownPortsPercentageMap = new HashMap<>();
        getPortsCountHashMap().forEach((k, v) -> knownPortsPercentageMap.put(k, ((double) v)/getTotalCount()*100));
        return knownPortsPercentageMap;
    }
}
