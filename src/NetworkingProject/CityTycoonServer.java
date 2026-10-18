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
            System.out.println("Waiting for a player to connect...");

            // Wait for a client connection
            try (Socket clientSocket = serverSocket.accept();
                 BufferedReader input = new BufferedReader(
                     new InputStreamReader(
                         clientSocket.getInputStream(),
                         StandardCharsets.UTF_8));
                 PrintWriter output = new PrintWriter(
                     clientSocket.getOutputStream(), true,
                     StandardCharsets.UTF_8)) {

                System.out.println("A player has connected!");

                String message;

                // Read messages sent by the client
                while ((message = input.readLine()) != null) {

                    System.out.println("Client says: " + message);

                    if (message.equalsIgnoreCase("exit")) {
                        output.println("Goodbye from CityTycoon Server!");
                        break;
                    }

                    // Send a response to the client
                    output.println("Server received: " + message);
                }

                System.out.println("Client disconnected.");
            }

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}

