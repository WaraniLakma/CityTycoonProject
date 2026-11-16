package edu.ruhuna.fourcrowns.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public final class UdpDiscoveryServer {
    public static final int PORT = 3002;
    private UdpDiscoveryServer() { }

    public static void runServer() {
        try (DatagramSocket socket = new DatagramSocket(new InetSocketAddress("127.0.0.1", PORT))) {
            System.out.println("UDP discovery listener on 127.0.0.1:"+PORT);
            byte[] buffer = new byte[512];
            while (!Thread.currentThread().isInterrupted()) {
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                socket.receive(incoming);
                String query = new String(incoming.getData(), incoming.getOffset(), incoming.getLength(), StandardCharsets.UTF_8);
                if (!"FOUR_CROWNS_DISCOVER".equals(query.trim())) continue;
                byte[] reply = "FOUR_CROWNS|HTTP_PORT=3000|ROOMS=4_PLAYERS".getBytes(StandardCharsets.UTF_8);
                DatagramPacket response = new DatagramPacket(reply, reply.length,
                    incoming.getAddress(), incoming.getPort());
                socket.send(response);
            }
        } catch (Exception e) { System.err.println("UDP discovery disabled: "+e.getMessage()); }
    }
}
