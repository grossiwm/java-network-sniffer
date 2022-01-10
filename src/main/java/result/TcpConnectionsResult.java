package result;

import connection.TCPConnection;
import enums.Shift;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

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

    public List<HashMap<Integer, Integer>> getPortsCountHashMapsByShift() {
        final HashMap<Integer, Integer> morningPortsHashMap = new HashMap<>();
        final HashMap<Integer, Integer> eveningPortsHashMap = new HashMap<>();
        final HashMap<Integer, Integer> nightPortsHashMap = new HashMap<>();
        final HashMap<Integer, Integer> dawnPortsHashMap = new HashMap<>();
        connections.forEach(c -> {
            HashMap<Integer, Integer> portsHashMap;
            Shift shift = c.getShift();
            if (shift.equals(Shift.MORNING)) {
                portsHashMap = morningPortsHashMap;
            } else if (shift.equals(Shift.EVENING)) {
                portsHashMap = eveningPortsHashMap;
            } else if (shift.equals(Shift.NIGHT)) {
                portsHashMap = nightPortsHashMap;
            } else {
                portsHashMap = dawnPortsHashMap;
            }
            portsHashMap.putIfAbsent(c.getSrcPort(), 0);
            portsHashMap.put(c.getSrcPort(), portsHashMap.get(c.getSrcPort()) + 1);
        });
        return Arrays.asList(morningPortsHashMap, eveningPortsHashMap, nightPortsHashMap, dawnPortsHashMap);
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

    private HashMap<Integer, Double> getPortsPercentageByCountMap(HashMap<Integer, Integer> countMap) {
        HashMap<Integer, Double> portsPercentageMap = new HashMap<>();
        Integer totalCount = countMap.values().stream().reduce(0, Integer::sum);
        countMap.forEach((k, v) -> portsPercentageMap.put(k, ((double) v)/totalCount*100));
        return portsPercentageMap;
    }

    private List<HashMap<Integer, Double>> getListOfPortsPercentageMapByShiftFromPortCountMap(List<HashMap<Integer, Integer>> portsCountMapList) {
        return portsCountMapList.stream().map(this::getPortsPercentageByCountMap).collect(Collectors.toList());
    }

    public List<HashMap<Integer, Double>> getListOfPortsPercentageMapByShift() {
        return getListOfPortsPercentageMapByShiftFromPortCountMap(getPortsCountHashMapsByShift());
    }
}
