package Client;

import Message.ChatMessage;
import Message.ChatMessageType;
import Message.JoinRoomRequest;
import Message.JoinRoomResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class SocketClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 1234;

    private String username;
    private Socket socket;
    private ObjectInputStream serverInput;
    private ObjectOutputStream serverOutput;

    public SocketClient(String username) {
        this.username = username;
    }

    public void connect() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            serverOutput = new ObjectOutputStream(socket.getOutputStream());
            serverInput = new ObjectInputStream(socket.getInputStream());

            // Send JoinRoomRequest to server
            JoinRoomRequest joinRequest = new JoinRoomRequest(username, "Main Room");
            serverOutput.writeObject(joinRequest);
            serverOutput.flush();

            // Receive JoinRoomResponse from server
            JoinRoomResponse joinResponse = (JoinRoomResponse) serverInput.readObject();
            System.out.println(joinResponse.getResponse());

            // Start a new thread to receive messages from the server
            new Thread(new MessageReceiver()).start();

            System.out.println("Connected to the server.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            // Create a ChatMessage and send it to the server
            ChatMessage chatMessage = new ChatMessage(username, "Main Room", ChatMessageType.BROADCAST, message);
            serverOutput.writeObject(chatMessage);
            serverOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            serverOutput.close();
            serverInput.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        SocketClient client = new SocketClient(username);
        client.connect();

        // Example usage: sending messages from console input
        while (true) {
            System.out.print("Enter message: ");
            String message = scanner.nextLine();
            client.sendMessage(message);

            System.out.print("Do you want to send another message? (y/n): ");
            String choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("n")) {
                break;
            }
        }

        // Gracefully shutdown the client
        client.shutdown();
        scanner.close();
    }

    private class MessageReceiver implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    if (serverInput.available() > 0) {
                        Object receivedObject = serverInput.readObject();
                        if (receivedObject instanceof ChatMessage) {
                            ChatMessage receivedMessage = (ChatMessage) receivedObject;
                            System.out.println("Received message: " + receivedMessage.getMessage());
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                shutdown();
            }
        }
    }
}
