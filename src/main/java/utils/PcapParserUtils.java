package utils;

import connection.TCPConnection;
import connection.UDPConnection;
import connection.OnlyEqualsSet;
import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import result.SafetyResult;
import result.TcpConnectionsResult;
import result.UdpConnectionsResult;

import java.util.HashSet;
import java.util.Set;

public class PcapParserUtils {

    private PcapParserUtils(String pcapFilePath) {
        this.pcapFilePath = pcapFilePath;
    }

    private String pcapFilePath;

    public static PcapParserUtils forPcap(String pcapFilePath) {
        return new PcapParserUtils(pcapFilePath);
    }

    public SafetyResult getSafetyResult() throws NotOpenException, PcapNativeException {
        PcapHandle handle = Pcaps.openOffline(pcapFilePath);
        Packet packet;
        int httpCounter = 0;
        int httpsCounter = 0;
        while ((packet = handle.getNextPacket()) != null) {

            try {

                TcpPacket.TcpHeader transportHeader = (TcpPacket.TcpHeader) packet.getPayload().getPayload().getPayload().getHeader();

                if (transportHeader.getAck() && transportHeader.getSyn()) {
                    if (transportHeader.getSrcPort().valueAsInt() == 443)
                        httpsCounter += 1;

                    if (transportHeader.getSrcPort().valueAsInt() == 80)
                        httpCounter += 1;
                }

            } catch (Exception e) {}

        }

        return new SafetyResult(httpsCounter, httpCounter);
    }

    public TcpConnectionsResult getTcpConnections() throws NotOpenException, PcapNativeException {
        PcapHandle handle = Pcaps.openOffline(pcapFilePath);
        Packet packet;
        Set<TCPConnection> connections = new HashSet<>();

        while ((packet = handle.getNextPacket()) != null) {

            try {

                TcpPacket.TcpHeader transportHeader = (TcpPacket.TcpHeader) packet.getPayload().getPayload().getPayload().getHeader();
                IpV4Packet.IpV4Header networkHeader = (IpV4Packet.IpV4Header) packet.getPayload().getPayload().getHeader();

                if (transportHeader.getAck() && transportHeader.getSyn()) {
                    TCPConnection connection = new TCPConnection();
                    connection.setDstAddr(networkHeader.getDstAddr().toString());
                    connection.setSrcAddr(networkHeader.getSrcAddr().toString());
                    connection.setDstPort(transportHeader.getDstPort().valueAsInt());
                    connection.setSrcPort(transportHeader.getSrcPort().valueAsInt());
                    connections.add(connection);
                }

            } catch (Exception e) {}

        }

        return new TcpConnectionsResult(connections);

    }

    public UdpConnectionsResult getUdpConnections() throws PcapNativeException, NotOpenException {
        PcapHandle handle = Pcaps.openOffline(pcapFilePath);
        Packet packet;
        OnlyEqualsSet connections = new OnlyEqualsSet();

        while ((packet = handle.getNextPacket()) != null) {

            try {

                UdpPacket.UdpHeader transportHeader = (UdpPacket.UdpHeader) packet.getPayload().getPayload().getPayload().getHeader();
                UDPConnection udpConnection = new UDPConnection();
                udpConnection.setDstPort(transportHeader.getDstPort().valueAsInt());
                udpConnection.setSrcPort(transportHeader.getSrcPort().valueAsInt());

                IpV4Packet.IpV4Header networkHeader = (IpV4Packet.IpV4Header) packet.getPayload().getPayload().getHeader();
                udpConnection.setDstAddr(networkHeader.getDstAddr().toString());
                udpConnection.setSrcAddr(networkHeader.getSrcAddr().toString());

                udpConnection.setTimestamp(((PcapPacket) packet).getTimestamp());

                connections.add(udpConnection);

            } catch (Exception e) {}

        }

        return new UdpConnectionsResult(connections);

//        Set<Integer> ports = connections.stream().map(c -> c.getSrcPort()).collect(Collectors.toSet());
//        ports.addAll(connections.stream().map(c -> c.getDstPort()).collect(Collectors.toList()));
    }
}
