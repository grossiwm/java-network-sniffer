package result;

import connection.UDPConnection;

import java.util.*;

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
        HashMap<Integer, Integer> countMap = new HashMap<>();
        connections.forEach(connection -> {
            Optional<Integer> countOp = Optional.ofNullable(countMap.get(connection.getDstPort()));
            countMap.put(connection.getDstPort(), countOp.orElse(0) + 1);
        });

        return countMap;
    }

    @Override
    public HashMap<Integer, Integer> getKnownPortsCountHashMap() {
        return null;
    }

    @Override
    public HashMap<Integer, Double> getKnownPortsPercentage() {
        return null;
    }

    @Override
    public HashMap<Integer, Double> getPortsPercentage() {
        HashMap<Integer, Double> portsPercentageMap = new HashMap<>();
        getPortsCountHashMap().forEach((k, v) -> portsPercentageMap.put(k, ((double) v)/getTotalCount()*100));
        return portsPercentageMap;
    }
}
