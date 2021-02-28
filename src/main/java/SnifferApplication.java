import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import java.net.InetAddress;
import java.util.Scanner;

public class SnifferApplication {

    public static void main(String[] args) {

        System.out.print("Type the ip address of the nic:");
        Scanner scan = new Scanner(System.in);
        String ip = scan.nextLine();

        try {
            InetAddress addr = InetAddress.getByName(ip);
            PcapNetworkInterface nif = Pcaps.getDevByAddress(addr);

            int snapLen = 65536;
            PcapNetworkInterface.PromiscuousMode mode = PcapNetworkInterface.PromiscuousMode.PROMISCUOUS;
            int timeout = 10;
            PcapHandle handle = nif.openLive(snapLen, mode, timeout);

            Packet packet = handle.getNextPacketEx();
            handle.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
