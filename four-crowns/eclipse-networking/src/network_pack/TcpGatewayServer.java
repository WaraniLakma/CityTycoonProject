package network_pack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;


public final class TcpGatewayServer {
	
    private static final String BIND_HOST = "127.0.0.1";
    private static final String NODE_HOST = "127.0.0.1";
    private static final int LISTEN_PORT = 3000;
    private static final int NODE_PORT = 3001;
    private static final int LIMIT = 96;
    private static final ExecutorService WORKERS = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "four-crowns-tcp-worker");
        t.setDaemon(true);
        return t;
    });
    private static final Semaphore SLOTS = new Semaphore(LIMIT);
    private static final AtomicLong IDS = new AtomicLong();

    private TcpGatewayServer() { }

    public static void main(String[] args) throws IOException {
        System.out.println("FOUR CROWNS · ECLIPSE JAVA TCP GATEWAY");
        System.out.println("Java ServerSocket: http://localhost:" + LISTEN_PORT);
        System.out.println("Node game engine: http://localhost:" + NODE_PORT);
        System.out.println("ngrok command: ngrok http " + LISTEN_PORT);
        System.out.println("Keep Eclipse running during the match. Ctrl+C / Stop to shut down.");
        Thread discovery = new Thread(UdpDiscoveryServer::runServer, "four-crowns-udp-discovery");
        discovery.setDaemon(true);
        discovery.start();

        try (ServerSocket server = new ServerSocket()) {
            server.setReuseAddress(true);
            server.bind(new InetSocketAddress(BIND_HOST, LISTEN_PORT), 128);
            while (true) {
                Socket client = server.accept();
                if (!SLOTS.tryAcquire()) {
                    sendServiceUnavailable(client, "Gateway busy. Retry in a moment.");
                    continue;
                }
                long id = IDS.incrementAndGet();
                WORKERS.execute(() -> {
                    try { bridge(id, client); }
                    finally { SLOTS.release(); }
                });
            }
        }
    }

    private static void bridge(long id, Socket client) {
        Socket node = new Socket();
        try {
            node.connect(new InetSocketAddress(NODE_HOST, NODE_PORT), 2500);
            client.setTcpNoDelay(true);
            node.setTcpNoDelay(true);
            System.out.printf("[TCP %d] connected; active connections: %d%n", id, LIMIT-SLOTS.availablePermits());
            AtomicLong requestBytes = new AtomicLong();
            AtomicLong responseBytes = new AtomicLong();
            Thread upstream = new Thread(() -> transfer(client, node, requestBytes), "client-to-node-"+id);
            Thread downstream = new Thread(() -> transfer(node, client, responseBytes), "node-to-client-"+id);
            upstream.setDaemon(true);
            downstream.setDaemon(true);
            upstream.start();
            downstream.start();

            while (upstream.isAlive() && downstream.isAlive()) {
                upstream.join(500);
                downstream.join(500);
            }
            System.out.printf("[TCP %d] closed; bytes client→server=%d server→client=%d%n",
                id, requestBytes.get(), responseBytes.get());
        } catch (IOException e) {
            System.out.printf("[TCP %d] upstream unavailable: %s%n", id, e.getMessage());
            sendServiceUnavailable(client, "Four Crowns game server is offline. Start Node with PORT=3001.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            close(client);
            close(node);
        }
    }

    private static void transfer(Socket from, Socket to, AtomicLong counter) {
        try {
            InputStream input = from.getInputStream();
            OutputStream output = to.getOutputStream();
            byte[] buffer = new byte[16384];
            int count;
            while ((count = input.read(buffer)) >= 0) {
                if (count == 0) continue;
                output.write(buffer, 0, count);
                output.flush(); 
                counter.addAndGet(count);
            }
        } catch (IOException ignored) {

        }
    }

    private static void sendServiceUnavailable(Socket client, String message) {
        try (client) {
            byte[] body = message.getBytes(StandardCharsets.UTF_8);
            String headers = "HTTP/1.1 503 Service Unavailable\r\n"+
                "Content-Type: text/plain; charset=utf-8\r\n"+
                "Cache-Control: no-store\r\n"+
                "Content-Length: "+body.length+"\r\nConnection: close\r\n\r\n";
            client.getOutputStream().write(headers.getBytes(StandardCharsets.US_ASCII));
            client.getOutputStream().write(body);
        } catch (IOException ignored) { }
    }

    private static void close(Socket socket) {
        if (socket != null) try { socket.close(); } catch (IOException ignored) { }
    }
}

