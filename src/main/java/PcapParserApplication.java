import org.pcap4j.core.PcapNativeException;
import result.SafetyResult;
import result.TcpConnectionsResult;
import result.UdpConnectionsResult;
import utils.PcapParserUtils;

import java.util.Scanner;

public class PcapParserApplication {

    public static void main(String[] args) {
        System.out.print("Type the dump file path: ");
        Scanner scan = new Scanner(System.in);
        String input = scan.nextLine();
        PcapParserUtils pcapParserUtils = PcapParserUtils.forPcap(input);

        try {
            SafetyResult safetyResult = pcapParserUtils.getSafetyResult();
            System.out.println(safetyResult);

            TcpConnectionsResult tcpPortsResult = pcapParserUtils.getTcpConnections();
            System.out.println(tcpPortsResult.getPortsPercentage());

            UdpConnectionsResult udpConnectionsResult = pcapParserUtils.getUdpConnections();
            System.out.println(udpConnectionsResult.getPortsPercentage());
        } catch (PcapNativeException pne) {
            System.out.println("Couldn´t open pcap file of path " + input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
