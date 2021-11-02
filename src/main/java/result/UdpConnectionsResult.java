package result;

import connection.UDPConnection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class UdpConnectionsResult implements IConnectionsResult {
    public UdpConnectionsResult(Set<UDPConnection> conexoes) {
        this.connections = conexoes;
    }

    private Set<UDPConnection> connections;

    public Set<UDPConnection> getConnections() {
        return new HashSet<>(connections);
    }

    @Override
    public int getTotalCount() {
        return connections.size();
    }

    @Override
    public HashMap<Integer, Integer> getPortsCountHashMap() {
        return null;
    }

    public HashMap<Set<Integer>, Integer> getPortsSetCountHashMap() {
        HashMap<Set<Integer>, Integer> portsHashMap = new HashMap<>();
        connections.forEach(c -> {
            if (portsHashMap.get(c.getPortsSet()) == null)
                portsHashMap.put(c.getPortsSet(), 0);

            portsHashMap.put(c.getPortsSet(), (portsHashMap.get(c.getPortsSet()))+1);
            portsHashMap.put(c.getPortsSet(), (portsHashMap.get(c.getPortsSet()))+1);
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
        HashMap<Integer, Double> portsPercentageMap = new HashMap<>();
        getPortsCountHashMap().forEach((k, v) -> portsPercentageMap.put(k, ((double) v)/getTotalCount()*100));
        return portsPercentageMap;
    }
}
