
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
                    "A player has connected: "
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

            String message;

            while ((message = input.readLine()) != null) {

                System.out.println(
                    "Message from "
                    + socket.getRemoteSocketAddress()
                    + ": " + message
                );

                if (message.equalsIgnoreCase("exit")) {
                    output.println("Goodbye from CityTycoon Server!");
                    break;
                }

                output.println("Server received: " + message);
            }

        } catch (IOException e) {
            System.err.println(
                "Client connection error: " + e.getMessage()
            );
        }

        System.out.println("A client disconnected.");
    }
}
