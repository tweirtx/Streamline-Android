package me.tweirtx.streamlinetv;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class StreamlineDNS extends Thread {
    private String bcastName;

    public StreamlineDNS(String name) {
        bcastName = name;
    }

    public void run() {
        try {
            MulticastSocket multicastSocket = new MulticastSocket();
            InetAddress group = InetAddress.getByName("239.255.70.77");
            multicastSocket.joinGroup(group);
            String bufMessage = "findme:name=" + bcastName;
            byte[] buf = bufMessage.getBytes(StandardCharsets.UTF_8);
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 50765);
                multicastSocket.send(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
