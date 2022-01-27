package utils;

import connection.OnlyEqualsSet;
import connection.TCPConnection;
import connection.UDPConnection;
import enums.Shift;
import org.pcap4j.core.*;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;
import result.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

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

                    safetyResultByShift.setConnCount(safetyResultByShift.getConnCount()+1);
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

    public static List<RatesIntervalResult> generateRatesIntervalResults() throws NotOpenException, PcapNativeException {
        File captureFolder = new File("files/youtube");
        List<File> captureFiles = Arrays.asList(Objects.requireNonNull(captureFolder.listFiles()));
        List<RatesIntervalResult> ratesIntervalResults = new ArrayList<>();
        PcapParserUtils pcapParserUtils;

        for (File f : captureFiles) {
            pcapParserUtils = new PcapParserUtils(f.getPath());

            ratesIntervalResults.add(pcapParserUtils.generateRatesIntervalResult());

        }

        return ratesIntervalResults;
    }

    public static List<AvgIntervalResult> generateAVGIntervalsAboveRateResults() throws NotOpenException, PcapNativeException {
        File captureFolder = new File("files/youtube");
        List<File> captureFiles = Arrays.asList(Objects.requireNonNull(captureFolder.listFiles()));
        List<AvgIntervalResult> intervalResults = new ArrayList<>();
        PcapParserUtils pcapParserUtils;
        for (File f : captureFiles) {
            pcapParserUtils = new PcapParserUtils(f.getPath());
            intervalResults.add(pcapParserUtils.getAverageTimeOfIntervalsAboveRate());
        }

        return intervalResults;
    }

    public AvgIntervalResult getAverageTimeOfIntervalsAboveRate() throws NotOpenException, PcapNativeException {
        double avgRateDuringBelowAvgRate = this.generateRatesIntervalResult().getAverageBelowRate();
        PcapHandle handle = Pcaps.openOffline(pcapFilePath);

        PcapPacket packet = handle.getNextPacket();
        PcapPacket firstPacketOfSecond = packet;

        long sumSecond = firstPacketOfSecond.length();
        long interval;
        double currentRate;

        long timeSum = 0;
        long aboveCount = 0;

        boolean lastIsAbove = false;
        boolean isAbove;

        while ((packet = handle.getNextPacket()) != null) {
            sumSecond += packet.length();
            interval = ChronoUnit.SECONDS.between(firstPacketOfSecond.getTimestamp(), packet.getTimestamp());

            if (interval >= 1) {
                currentRate = ((double) sumSecond)/interval;

                if (currentRate > avgRateDuringBelowAvgRate) {
                    isAbove = true;
                } else {
                    isAbove = false;
                }

                if (isAbove) {
                    timeSum += interval;
                    if (!lastIsAbove) {
                        aboveCount += 1;
                    }
                }

                lastIsAbove = isAbove;
                firstPacketOfSecond = packet;
                sumSecond = 0;
            }
        }

        return new AvgIntervalResult((((double) timeSum)/aboveCount), timeSum, aboveCount);
    }

    public RatesIntervalResult generateRatesIntervalResult() throws NotOpenException, PcapNativeException {
        double rate = this.getCaptureResults().getTransmissionRate();
        PcapHandle handle = Pcaps.openOffline(pcapFilePath);

        long intervalBelowRate = 0;
        long intervalAboveRate = 0;

        long aboveRatesLength = 0;
        long belowRatesLength = 0;

        double sum = 0;

        PcapPacket packet = handle.getNextPacket();
        PcapPacket firstPacketOfIndex = packet;
         do {

            sum += packet.length();
            long interval = ChronoUnit.SECONDS.between(firstPacketOfIndex.getTimestamp(), packet.getTimestamp());
            if (interval >= 1) {

                if (sum/interval >= rate) {
                    intervalAboveRate+=interval;
                    aboveRatesLength += sum;
                } else {
                    intervalBelowRate+=interval;
                    belowRatesLength += sum;
                }

                firstPacketOfIndex = handle.getNextPacket();
                sum =0;
            }
        } while ((packet = handle.getNextPacket()) != null);

         RatesIntervalResult result = new RatesIntervalResult();
         result.setAboveRateInterval(intervalAboveRate);
         result.setBelowRateInterval(intervalBelowRate);
         result.setAverageBelowRate(((double) belowRatesLength)/intervalBelowRate);
         result.setAverageAboveRate(((double) aboveRatesLength)/intervalAboveRate);
         result.setAverateRate(rate);

         return result;
    }

    public PerSecondRateResult generatePerSecondResult(int secondsDenominator) throws PcapNativeException, NotOpenException {
        PcapHandle handle = Pcaps.openOffline(pcapFilePath);

        PerSecondRateResult result = new PerSecondRateResult();


        PcapPacket firstPacketOfIndex = handle.getNextPacket();
        int sum = firstPacketOfIndex.length();

        PerSecondRateResult.Coordinate coordinate;

        PcapPacket packet;

        int index = 0;

        coordinate = new PerSecondRateResult.Coordinate(new PerSecondRateResult.X(index), new PerSecondRateResult.Y(sum));

        result.addCordinate(coordinate);
        sum = 0;
        while ((packet = handle.getNextPacket()) != null) {

            sum += packet.length();
            if (ChronoUnit.SECONDS.between(firstPacketOfIndex.getTimestamp(), packet.getTimestamp()) >= secondsDenominator) {

                coordinate = new PerSecondRateResult.Coordinate(new PerSecondRateResult.X(++index), new PerSecondRateResult.Y(sum));

                result.addCordinate(coordinate);

                firstPacketOfIndex = packet;
                sum =0;
            }
        }

        return result;

    }

    public static void generateRatesIntervalCsv(List<RatesIntervalResult> results) {
        File file = new File("files/outputs/rates-interval.csv");

        try (FileWriter fw = new FileWriter(file, true);
             BufferedWriter bf = new BufferedWriter(fw)
        ) {
            results.forEach(r -> {
                try {
                    bf.write(r.toString());
                    bf.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateAverageTimesCsv(List<AvgIntervalResult> results) {
        File file = new File("files/outputs/average-times.csv");

        try (FileWriter fw = new FileWriter(file, true);
             BufferedWriter bf = new BufferedWriter(fw)
        ) {
            results.forEach(r -> {
                try {
                    bf.write(r.toString());
                    bf.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
