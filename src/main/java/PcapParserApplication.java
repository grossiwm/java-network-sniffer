import result.SafetyResult;
import result.TcpConnectionsResult;
import utils.PcapParserUtils;

public class PcapParserApplication {

    public static void main(String[] args) {
        PcapParserUtils pcapParserUtils = PcapParserUtils.forPcap("dump.pcap");

        try {
            SafetyResult safetyResult = pcapParserUtils.getSafetyResult();
            System.out.println(safetyResult);

            TcpConnectionsResult tcpPortsResult = pcapParserUtils.getTcpConnections();
            System.out.println(tcpPortsResult.getPortsPercentage());

//            UdpConnectionsResult udpConnectionsResult = pcapParserUtils.getUdpConnections();
//            System.out.println(udpConnectionsResult.getPortsCountHashMap());
//            System.out.println(udpConnectionsResult.getTotalCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
