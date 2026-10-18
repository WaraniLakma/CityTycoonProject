package NetworkingProject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class CityTycoonClient {

    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {

        System.out.println("Connecting to CityTycoon Server...");

        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader input = new BufferedReader(
                 new InputStreamReader(
                     socket.getInputStream(),
                     StandardCharsets.UTF_8));
             PrintWriter output = new PrintWriter(
                 socket.getOutputStream(), true,
                 StandardCharsets.UTF_8);
             Scanner keyboard = new Scanner(System.in)) {

            System.out.println("Connected successfully!");
            System.out.println("Type a message, or type 'exit' to quit.");

            while (true) {

                System.out.print("You: ");
                String message = keyboard.nextLine();

                // Send a message to the server
                output.println(message);

                // Receive the server's response
                String response = input.readLine();

                if (response == null) {
                    System.out.println("Server disconnected.");
                    break;
                }

                System.out.println(response);

                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
            System.err.println("Make sure the server is running first.");
        }
    }
}

