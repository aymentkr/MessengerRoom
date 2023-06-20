package Message;

import java.io.Serializable;

public class ChatMessage implements Serializable {
    private String senderName;
    private String roomName;
    private String message;
    private ChatMessageType type;

    private ChatMessage(String senderName, String roomName, ChatMessageType type) {
        this.senderName = senderName;
        this.roomName = roomName;
        this.type = type;
    }

    public ChatMessage(String senderName, String roomName, ChatMessageType type, String message) {
        this(senderName, roomName, type);
        this.message = message;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getRoomName() {
        return roomName;
    }

    public ChatMessageType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

}
