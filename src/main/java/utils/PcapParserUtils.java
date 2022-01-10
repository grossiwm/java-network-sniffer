package utils;

import connection.TCPConnection;
import connection.UDPConnection;
import connection.OnlyEqualsSet;
import enums.Shift;
import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import result.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
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

    public SafetyResults getSafetyResultByShift() throws NotOpenException, PcapNativeException {
        SafetyResults safetyResults = new SafetyResults();
        safetyResults.addResult(new SafetyResultByShift(Shift.MORNING));
        safetyResults.addResult(new SafetyResultByShift(Shift.EVENING));
        safetyResults.addResult(new SafetyResultByShift(Shift.NIGHT));
        safetyResults.addResult(new SafetyResultByShift(Shift.DAWN));
        SafetyResult safetyResult = new SafetyResult();
        PcapHandle handle = Pcaps.openOffline(pcapFilePath);
        Packet packet;
        int httpCounter = 0;
        int httpsCounter = 0;
        while ((packet = handle.getNextPacket()) != null) {

            try {

                TcpPacket.TcpHeader transportHeader = (TcpPacket.TcpHeader) packet.getPayload().getPayload().getPayload().getHeader();
                Instant timestamp = ((PcapPacket) packet).getTimestamp();
                if (transportHeader.getAck() && transportHeader.getSyn()) {

                    SafetyResultByShift safetyResultByShift = safetyResults.getResults().stream().map(r -> (SafetyResultByShift) r)
                            .filter(r -> r.getShift().equals(getShift(timestamp))).findFirst().get();

                    if (transportHeader.getSrcPort().valueAsInt() == 443) {
                        httpsCounter += 1;

                        safetyResultByShift.setHttpsCount(safetyResultByShift.getHttpsCount() + 1);

                    }

                    if (transportHeader.getSrcPort().valueAsInt() == 80) {
                        httpCounter += 1;

                        safetyResultByShift.setHttpCount(safetyResultByShift.getHttpCount() + 1);
                    }
                }

            } catch (Exception e) {}

        }
        safetyResults.addResult(new SafetyResult(httpsCounter, httpCounter));

        return safetyResults;
    }

    private Shift getShift(Instant timestamp) {
        int hour = timestamp.atZone(ZoneId.of("America/Sao_Paulo")).getHour();
        return Arrays.stream(Shift.values()).filter(s -> hour >= s.getStart() && hour < s.getEnd()).findFirst().get();
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
                    connection.setShift(getShift(((PcapPacket)packet).getTimestamp()));
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

                Instant timestamp = ((PcapPacket) packet).getTimestamp();
                udpConnection.setTimestamp(timestamp);
                udpConnection.setShift(getShift(timestamp));

                connections.add(udpConnection);

            } catch (Exception e) {}

        }

        return new UdpConnectionsResult(connections);

    }

    public CaptureResults getCaptureResults() throws PcapNativeException, NotOpenException {
        PcapHandle handle = Pcaps.openOffline(pcapFilePath);

        PcapPacket packet = handle.getNextPacket();
        Instant initialTimestamp = packet.getTimestamp();
        Instant lastTimestamp = handle.getNextPacket().getTimestamp();
        Long lengthSum = Integer.toUnsignedLong(packet.length());
        Long numberOfPackets = 1l;
        CaptureResults captureResults = new CaptureResults();

        while ((packet = handle.getNextPacket()) != null) {
            lastTimestamp = packet.getTimestamp();
            lengthSum += Integer.toUnsignedLong(packet.length());
            numberOfPackets += 1;
        }
        captureResults.setBegin(initialTimestamp);
        captureResults.setEnd(lastTimestamp);
        captureResults.setLengthCaptured(lengthSum);
        captureResults.setNumberOfPackets(numberOfPackets);
        return captureResults;
    }

    public PerSecondRateResult calculateRatePerSecond(int secondsDenominator) throws NotOpenException, PcapNativeException {
        PcapHandle handle = Pcaps.openOffline(pcapFilePath);

        PerSecondRateResult result = new PerSecondRateResult();


        PcapPacket firstPacketOfIndex = handle.getNextPacket();
        int sum = firstPacketOfIndex.length();

        PerSecondRateResult.Coordinate coordinate;

        PcapPacket packet;

        int index = 0;

        coordinate = new PerSecondRateResult.Coordinate(new PerSecondRateResult.X(index), new PerSecondRateResult.Y(sum));

        result.addCordinate(coordinate);

        while ((packet = handle.getNextPacket()) != null) {

            sum += packet.length();
            if (ChronoUnit.SECONDS.between(firstPacketOfIndex.getTimestamp(), packet.getTimestamp()) >= secondsDenominator) {

                 coordinate = new PerSecondRateResult.Coordinate(new PerSecondRateResult.X(++index), new PerSecondRateResult.Y(sum));

                result.addCordinate(coordinate);

                firstPacketOfIndex = packet;
            }
        }

        return null;
    }

}
