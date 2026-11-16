package edu.ruhuna.fourcrowns.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public final class UdpDiscoveryClient {
    public static void main(String[] args) throws Exception {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(3000);
            byte[] request = "FOUR_CROWNS_DISCOVER".getBytes(StandardCharsets.UTF_8);
            InetAddress host = InetAddress.getLoopbackAddress();
            socket.send(new DatagramPacket(request,request.length,host,UdpDiscoveryServer.PORT));
            byte[] bytes = new byte[256];
            DatagramPacket reply = new DatagramPacket(bytes,bytes.length);
            socket.receive(reply);
            System.out.println("Received UDP datagram: " + new String(reply.getData(),reply.getOffset(),reply.getLength(),StandardCharsets.UTF_8));
        }
    }
}
