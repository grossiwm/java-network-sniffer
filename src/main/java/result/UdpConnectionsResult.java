package result;

import connection.UDPConnection;
import enums.Shift;

import java.util.*;
import java.util.stream.Collectors;

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

    public List<HashMap<Integer, Integer>> getPortsCountHashMapsByShift() {
        final HashMap<Integer, Integer> morningPortsHashMap = new HashMap<>();
        final HashMap<Integer, Integer> eveningPortsHashMap = new HashMap<>();
        final HashMap<Integer, Integer> nightPortsHashMap = new HashMap<>();
        final HashMap<Integer, Integer> dawnPortsHashMap = new HashMap<>();
        connections.forEach(connection -> {
            HashMap<Integer, Integer> portsHashMap;
            Shift shift = connection.getShift();
            if (shift.equals(Shift.MORNING)) {
                portsHashMap = morningPortsHashMap;
            } else if (shift.equals(Shift.EVENING)) {
                portsHashMap = eveningPortsHashMap;
            } else if (shift.equals(Shift.NIGHT)) {
                portsHashMap = nightPortsHashMap;
            } else {
                portsHashMap = dawnPortsHashMap;
            }
            portsHashMap.putIfAbsent(connection.getDstPort(), 0);
            portsHashMap.put(connection.getDstPort(), portsHashMap.get(connection.getDstPort()) + 1);
        });

        return Arrays.asList(morningPortsHashMap, eveningPortsHashMap, nightPortsHashMap, dawnPortsHashMap);
    }

    private List<HashMap<Integer, Double>> getListOfPortsPercentageMapByShiftFromPortCountMap(List<HashMap<Integer, Integer>> portsCountMapList) {
        return portsCountMapList.stream().map(this::getPortsPercentageByCountMap).collect(Collectors.toList());
    }

    private HashMap<Integer, Double> getPortsPercentageByCountMap(HashMap<Integer, Integer> countMap) {
        HashMap<Integer, Double> portsPercentageMap = new HashMap<>();
        Integer totalCount = countMap.values().stream().reduce(0, Integer::sum);
        countMap.forEach((k, v) -> portsPercentageMap.put(k, ((double) v)/totalCount*100));
        return portsPercentageMap;
    }

    public List<HashMap<Integer, Double>> getListOfPortsPercentageMapByShift() {
        return getListOfPortsPercentageMapByShiftFromPortCountMap(getPortsCountHashMapsByShift());
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
