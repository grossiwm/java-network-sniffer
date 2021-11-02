import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;

import java.io.EOFException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

public class PcapParserApplication {

    public static void main(String[] args) throws PcapNativeException, NotOpenException, EOFException, TimeoutException {

        PcapHandle handle = Pcaps.openOffline("dump.pcap");
        Packet packet;
        int httpCounter=0;
        int httpsCounter=0;
        while ((packet = handle.getNextPacket()) != null) {

            try {

                TcpPacket.TcpHeader transportHeader = (TcpPacket.TcpHeader) packet.getPayload().getPayload().getPayload().getHeader();

                if (transportHeader.getAck() && transportHeader.getSyn()) {
                    if (transportHeader.getSrcPort().valueAsInt() == 443)
                        httpsCounter += 1;

                    if (transportHeader.getSrcPort().valueAsInt() == 80)
                        httpCounter += 1;
                }

            } catch (Exception e) {
//                e.printStackTrace();
            }

        }

        double securePercent = ((double) httpsCounter)/(httpsCounter+httpCounter)*100;
        double unsecurePercent = ((double) httpCounter)/(httpsCounter+httpCounter)*100;

        System.out.println("safe: " + securePercent + " unsafe: " + unsecurePercent);
    }
}
