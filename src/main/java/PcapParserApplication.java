import enums.Shift;
import org.pcap4j.core.PcapNativeException;
import result.SafetyResults;
import result.TcpConnectionsResult;
import result.UdpConnectionsResult;
import utils.PcapParserUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class PcapParserApplication {

    public static void main(String[] args) {
        System.out.print("Type the dump file path: ");
        Scanner scan = new Scanner(System.in);
        String input = scan.nextLine();
        PcapParserUtils pcapParserUtils = PcapParserUtils.forPcap(input);

        try {



            /* Safety Results */
            System.out.println("Safety Result:");
            SafetyResults safetyResults = pcapParserUtils.getSafetyResultByShift();
            safetyResults.getResults().forEach(System.out::println);
            PcapParserUtils.generateRatesIntervalCsv(PcapParserUtils.generateRatesIntervalResults());

            /* TCP Results */
            System.out.println("TCP Ports Result:");
            TcpConnectionsResult tcpConnectionsResult = pcapParserUtils.getTcpConnections();
//            List<HashMap<Integer, Double>> tcpPortsPecentageMap =  tcpConnectionsResult.getListOfPortsPercentageMapByShift();
            System.out.println(Shift.MORNING);
//            System.out.println(tcpPortsPecentageMap.get(0));
            System.out.println(tcpConnectionsResult.getPortsCountHashMapsByShift().get(0).values().stream().reduce(0, (a,b) -> a+b));
//
            System.out.println(Shift.EVENING);
//            System.out.println(tcpPortsPecentageMap.get(1));
            System.out.println(tcpConnectionsResult.getPortsCountHashMapsByShift().get(1).values().stream().reduce(0, (a,b) -> a+b));
//
            System.out.println(Shift.NIGHT);
//            System.out.println(tcpPortsPecentageMap.get(2));
            System.out.println(tcpConnectionsResult.getPortsCountHashMapsByShift().get(2).values().stream().reduce(0, (a,b) -> a+b));

//
            System.out.println(Shift.DAWN);
//            System.out.println(tcpPortsPecentageMap.get(3));
            System.out.println(tcpConnectionsResult.getPortsCountHashMapsByShift().get(3).values().stream().reduce(0, (a,b) -> a+b));

//
            System.out.println("TOTAL");
//            System.out.println(tcpConnectionsResult.getPortsPercentage());
            System.out.println(tcpConnectionsResult.getTotalCount());


            /* UDP Results */
            System.out.println("UDP Ports Result:");
            UdpConnectionsResult udpConnectionsResult = pcapParserUtils.getUdpConnections();
            List<HashMap<Integer, Double>> udpPortsPercentageMap = udpConnectionsResult.getListOfPortsPercentageMapByShift();

            System.out.println(Shift.MORNING);
//            System.out.println(udpPortsPercentageMap.get(0));
            System.out.println(udpConnectionsResult.getPortsCountHashMapsByShift().get(0).values().stream().reduce(0, (a,b) -> a+b));

            System.out.println(Shift.EVENING);
//            System.out.println(udpPortsPercentageMap.get(1));
            System.out.println(udpConnectionsResult.getPortsCountHashMapsByShift().get(1).values().stream().reduce(0, (a,b) -> a+b));


            System.out.println(Shift.NIGHT);
//            System.out.println(udpPortsPercentageMap.get(2));
            System.out.println(udpConnectionsResult.getPortsCountHashMapsByShift().get(2).values().stream().reduce(0, (a,b) -> a+b));


            System.out.println(Shift.DAWN);
//            System.out.println(udpPortsPercentageMap.get(3));
            System.out.println(udpConnectionsResult.getPortsCountHashMapsByShift().get(3).values().stream().reduce(0, (a,b) -> a+b));


            System.out.println("TOTAL");
//            System.out.println(udpConnectionsResult.getPortsPercentage());
            System.out.println(udpConnectionsResult.getTotalCount());
//            CaptureResults captureResults = pcapParserUtils.getCaptureResults();
//            System.out.println(captureResults.getTransmissionRate());
//            System.out.println(captureResults.getNumberOfPackets());
//            System.out.println(captureResults.getLengthCaptured());

        } catch (PcapNativeException pne) {
//            System.out.println("Couldn´t open pcap file of path " + input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
