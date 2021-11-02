package connection;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class UDPConnection {

    private Integer srcPort;
    private Integer dstPort;

    private String srcAddr;
    private String dstAddr;

    public Integer getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(Integer srcPort) {
        this.srcPort = srcPort;
    }

    public Integer getDstPort() {
        return dstPort;
    }

    public void setDstPort(Integer dstPort) {
        this.dstPort = dstPort;
    }

    public String getSrcAddr() {
        return srcAddr;
    }

    public void setSrcAddr(String srcAddr) {
        this.srcAddr = srcAddr;
    }

    public String getDstAddr() {
        return dstAddr;
    }

    public void setDstAddr(String dstAddr) {
        this.dstAddr = dstAddr;
    }

    public Set<Integer> getPortsSet() {
        Set<Integer> portsSet = new HashSet<>();
        portsSet.add(srcPort);
        portsSet.add(dstPort);

        return portsSet;
    }

    public Set<String> getAddressSet() {
        Set<String> addressSet = new HashSet<>();
        addressSet.add(srcAddr);
        addressSet.add(dstAddr);

        return addressSet;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        UDPConnection conn = (UDPConnection) obj;
        return conn.getAddressSet().equals(getAddressSet()) && conn.getPortsSet().equals(getPortsSet());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAddressSet(), getPortsSet());
    }
}
