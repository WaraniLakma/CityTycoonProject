
package NetworkingProject;

public class MessageTest {

    public static void main(String[] args) {

        Message original = new Message(
            MessageType.JOIN,
            "Player1"
        );

        String encoded = original.encode();

        System.out.println("Encoded message: " + encoded);

        Message decoded = Message.decode(encoded);

        System.out.println("Decoded type: " + decoded.getType());
        System.out.println("Decoded content: " + decoded.getContent());
        
		
		try {
		    Message.decode("UNKNOWN|Player1");
		    System.out.println("ERROR: Invalid message was accepted");
		
		} catch (IllegalArgumentException e) {
		    System.out.println("Invalid message rejected: " + e.getMessage());
		}

    }
}
