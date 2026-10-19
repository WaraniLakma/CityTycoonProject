
package NetworkingProject;

public class Message {

    private MessageType type;
    private String content;

    public Message(MessageType type, String content) {
        this.type = type;
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    // Convert a Message object into text for TCP transmission
    public String encode() {
        return type.name() + "|" + content;
    }

    // Convert received text back into a Message object
	
	public static Message decode(String data) {
	
	    if (data == null || data.isBlank()) {
	        throw new IllegalArgumentException(
	            "Message cannot be empty"
	        );
	    }
	
	    String[] parts = data.split("\\|", 2);
	
	    if (parts.length != 2 || parts[0].isBlank()) {
	        throw new IllegalArgumentException(
	            "Invalid message format"
	        );
	    }
	
	    try {
	        MessageType type = MessageType.valueOf(parts[0]);
	        return new Message(type, parts[1]);
	
	    } catch (IllegalArgumentException e) {
	        throw new IllegalArgumentException(
	            "Unknown message type: " + parts[0]
	        );
	    }
	}

}


