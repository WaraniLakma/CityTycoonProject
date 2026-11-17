package network_pack;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public final class TcpHealthClient {
    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket("127.0.0.1",3000)) {
            socket.setSoTimeout(8000);
            OutputStream output = socket.getOutputStream();
            output.write(("GET /api/health HTTP/1.1\r\nHost: localhost\r\n"+
                "Connection: close\r\n\r\n").getBytes(StandardCharsets.US_ASCII));
            output.flush();
            InputStream input = socket.getInputStream();
            byte[] bytes = input.readAllBytes();
            System.out.println(new String(bytes, StandardCharsets.UTF_8));
        }
    }
}
