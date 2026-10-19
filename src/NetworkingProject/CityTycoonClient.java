
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

        try (
            Socket socket = new Socket(HOST, PORT);

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
            );

            Scanner keyboard = new Scanner(System.in)
        ) {

            System.out.println("Connected successfully!");

            System.out.print("Enter your player name: ");
            String playerName = keyboard.nextLine().trim();

            if (playerName.isEmpty()
                    || playerName.contains("|")
                    || playerName.contains("\n")
                    || playerName.contains("\r")) {
                System.out.println("Invalid player name.");
                return;
            }

            Message joinRequest = new Message(
                MessageType.JOIN,
                playerName
            );

            output.println(joinRequest.encode());

            String responseText = input.readLine();

            if (responseText == null) {
                System.out.println("Server disconnected.");
                return;
            }

            try {
                Message response = Message.decode(responseText);

                System.out.println(
                    "Server response type: " + response.getType()
                );

                System.out.println(
                    "Server response: " + response.getContent()
                );

            } catch (IllegalArgumentException e) {
                System.out.println(
                    "Received an invalid server response."
                );
            }

        } catch (IOException e) {
            System.err.println(
                "Client error: " + e.getMessage()
            );
            System.err.println(
                "Make sure the server is running first."
            );
        }
    }
}
