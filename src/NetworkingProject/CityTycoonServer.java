
package NetworkingProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class CityTycoonServer {

    private static final int PORT = 5000;

    public static void main(String[] args) {

        System.out.println("Starting CityTycoon Server...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println("Server is listening on port " + PORT);

            while (true) {

                System.out.println("Waiting for a player to connect...");

                Socket clientSocket = serverSocket.accept();

                System.out.println(
                    "A client connected: "
                    + clientSocket.getRemoteSocketAddress()
                );

                Thread clientThread = new Thread(
                    () -> handleClient(clientSocket)
                );

                clientThread.start();
            }

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {

        try (
            Socket socket = clientSocket;
            BufferedReader input = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream(),
                    StandardCharsets.UTF_8
                )
            );
            PrintWriter output = new PrintWriter(
                socket.getOutputStream(),
                true,
                StandardCharsets.UTF_8
            )
        ) {

            String receivedText;

            while ((receivedText = input.readLine()) != null) {

                System.out.println("Received: " + receivedText);

                try {
                    Message request = Message.decode(receivedText);

                    Message response;

                    switch (request.getType()) {

                        case JOIN:
                            response = new Message(
                                MessageType.JOIN_SUCCESS,
                                "Welcome " + request.getContent()
                            );
                            break;

                        default:
                            response = new Message(
                                MessageType.ERROR,
                                "Unsupported message type: "
                                + request.getType()
                            );
                            break;
                    }

                    output.println(response.encode());

                } catch (IllegalArgumentException e) {

                    Message error = new Message(
                        MessageType.ERROR,
                        "Invalid message format or type"
                    );

                    output.println(error.encode());
                }
            }

        } catch (IOException e) {
            System.err.println(
                "Client connection error: " + e.getMessage()
            );
        }

        System.out.println("A client disconnected.");
    }
}
