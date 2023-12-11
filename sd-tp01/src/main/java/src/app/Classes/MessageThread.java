package src.app.Classes;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MessageThread extends Thread {
    private String sender;
    private String recipient;
    private String content;

    // File path for storing messages
    private static final String MESSAGE_FILE_PATH = "sd-tp01/src/main/java/src/app/Data/Messages.json";

    public MessageThread(String sender, String recipient, String content) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
    } 

    @Override
    public void run() {
        // Synchronize access to the file to ensure thread safety
        synchronized (MessageThread.class) {
            try (FileWriter fileWriter = new FileWriter(MESSAGE_FILE_PATH, true)) {
                // Create a new message
                Message message = new Message(sender, recipient, content);

                // Append the message to the file
                fileWriter.write(message.toString() + "\n\n");
                System.out.println("Message sent:\n" + message);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
