package src.app.Classes.Models;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import src.app.Classes.Threads.MessageThread;

public class Message {
    private String sender;
    private String recipient;
    private String title;
    private String content;
    private LocalDateTime timestamp;

    public Message(String sender, String recipient, String title, String content) {
        this.sender = sender;
        this.recipient = recipient;
        this.title = title;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public String getSender() {
        return sender;
    }

    public String getTitle() {
        return title;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;

    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Sender: " + sender +
                "\nRecipient: " + recipient +
                "\nContent: " + content +
                "\nTimestamp: " + timestamp;
    }

    @SuppressWarnings("unchecked")
    public static List<Message> readMessagesFromFileForUser(String recipient) {
        Path path = Path.of(MessageThread.MESSAGE_FILE_PATH);
        File file = new File(path.toString());

        try {
            // Load messages from the file
            if (Files.exists(path)) {
                // Read all messages from the file
                FileReader fileReader = new FileReader(file);
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(fileReader);
                JSONArray jsonMessages = (JSONArray) jsonObject.get("messages");

                List<Message> allMessages = (List<Message>) jsonMessages.stream()
                        .map(json -> new Message((String) ((JSONObject) json).get("sender"),
                                (String) ((JSONObject) json).get("recipient"),
                                (String) ((JSONObject) json).get("title"),
                                (String) ((JSONObject) json).get("content")))
                        .collect(Collectors.toList());

                List<Message> userMessages = new ArrayList<>();

                for (Message message : allMessages) {
                    if (message.getRecipient().equals(recipient)) {
                        userMessages.add(message);
                    }
                }

                fileReader.close();

                return userMessages;
            }

            return null;
        } catch (Exception e) {

        }

        return null;
    }
}
