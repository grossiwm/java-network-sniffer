import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.LinkLayerAddress;

import java.net.InetAddress;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class SnifferApplication {

    public static void main(String[] args) {



        try {

            List<PcapNetworkInterface> devices =  Pcaps.findAllDevs();

            System.out.print("Network Interfaces:\n");

            for (int i=0; i< devices.size(); i++) {

                PcapNetworkInterface device = devices.get(i);
                int humanIndex = i+1;

                String name = device.getName();

                InetAddress ipAddress = null;
                InetAddress netmask = null;

                if (device.getAddresses().size()>0) {
                    ipAddress  = device.getAddresses().get(0).getAddress();
                    netmask = device.getAddresses().get(0).getNetmask();
                }

                LinkLayerAddress macAddress = null;

                if (device.getLinkLayerAddresses().size()>0) {
                    macAddress = device.getLinkLayerAddresses().get(0);
                }

                System.out.println(humanIndex + " - Name: " + name + " | IP Address: " + ipAddress + " | Netmask: " + netmask + " | MAC Address: " + macAddress + "\n");
            }

            System.out.print("Type the name or IP of the selected Network Interface:");

            Scanner scan = new Scanner(System.in);
            String input = scan.nextLine();
            InetAddress addr;
            PcapNetworkInterface nif;

            if (StringUtils.isIP(input)) {
                addr = InetAddress.getByName(input);
                nif = Pcaps.getDevByAddress(addr);
            } else {
                nif = Pcaps.getDevByName(input);
            }

            int snapLen = 65536;
            PcapNetworkInterface.PromiscuousMode mode = PcapNetworkInterface.PromiscuousMode.PROMISCUOUS;
            int timeout = 10;


            try (PcapHandle handle = nif.openLive(snapLen, mode, timeout)) {

                try (PcapDumper dumper = handle.dumpOpen("dump.pcap")) {
                    while (true) {

                        try {

                            Packet packet = handle.getNextPacketEx();
                            dumper.dump(packet);
                            System.out.print("\n" + packet);

                        } catch (TimeoutException ignored) {}

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}