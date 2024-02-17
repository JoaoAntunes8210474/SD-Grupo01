package src.app.Classes.Models;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import src.app.Classes.Threads.MessageThread;
import src.app.Interfaces.IGetName;

public class Message implements IGetName {
    // Attributes
    private String sender;
    private String recipient;
    private String channel;
    private String title;
    private String content;
    private String approved;
    private LocalDateTime timestamp;

    // Constructor for message between users or to a channel
    public Message(String sender, String recipient, String channel, String title, String content) {
        this.sender = sender;
        this.recipient = recipient;
        this.channel = channel;
        this.title = title;
        this.content = content;
        this.approved = "Awaiting approval";
        this.timestamp = LocalDateTime.now();
    }

    public Message(String sender, String channel, String title, String content) {
        this.sender = sender;
        this.channel = channel;
        this.title = title;
        this.content = content;
        this.approved = "";
        this.timestamp = LocalDateTime.now();
    }

    // Constructor for message between users with specified approved
    public Message(String sender, String recipient, String channel, String title, String content, String approved,
            LocalDateTime timestamp) {
        this.sender = sender;
        this.recipient = recipient;
        this.channel = channel;
        this.title = title;
        this.content = content;
        this.approved = approved;
        this.timestamp = timestamp;
    }

    // Getters
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

    public String getApproved() {
        return approved;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getChannel() {
        return channel;
    }

    // Setters
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

    public void setApproved(String approved) {
        this.approved = approved;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    /**
     * Function to display the message in a readable format
     */
    @Override
    public String toString() {
        return "Sender: " + sender +
                "\nRecipient: " + recipient +
                "\nContent: " + content +
                "\nTimestamp: " + timestamp;
    }

    public static List<Message> readMessagesFromFileForUser(String nameOfRecipient) {
        try {
            Path path = Path.of(MessageThread.FILE_PATH);
            File file = new File(path.toString());

            long fileSize = Files.size(path);

            // Load messages from the file
            if (fileSize > 0) {
                // Read all messages from the file
                FileReader fileReader = new FileReader(file);
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(fileReader);
                JSONArray jsonMessages = (JSONArray) jsonObject.get("messages");

                List<Message> userMessages = new ArrayList<>();

                for (Object message : jsonMessages) {
                    JSONObject jsonMessage = (JSONObject) message;

                    String sender = (String) jsonMessage.get("sender");
                    String recipient = (String) jsonMessage.get("recipient");
                    String title = (String) jsonMessage.get("title");
                    String content = (String) jsonMessage.get("content");
                    String approved = (String) jsonMessage.get("approved");

                    LocalDateTime timestamp = LocalDateTime.parse((String) jsonMessage.get("timestamp"));

                    if (sender == null ||
                            recipient == null ||
                            title == null ||
                            content == null ||
                            approved == null ||
                            timestamp == null) {
                        continue;
                    }

                    Message newMessage = new Message(sender, recipient, "", title, content, approved, timestamp);

                    if (newMessage.getRecipient().equals(nameOfRecipient)) {
                        userMessages.add(newMessage);
                    }
                }

                fileReader.close();

                return userMessages;
            }

            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public static List<Message> readMessagesFromFileForChannel(String nameOfChannel) {
        try {
            Path path = Path.of(MessageThread.FILE_PATH);
            File file = new File(path.toString());

            long fileSize = Files.size(path);

            // Load messages from the file
            if (fileSize > 0) {
                // Read all messages from the file
                FileReader fileReader = new FileReader(file);
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(fileReader);
                JSONArray jsonMessages = (JSONArray) jsonObject.get("messages");

                List<Message> channelMessages = new ArrayList<>();

                for (Object message : jsonMessages) {
                    JSONObject jsonMessage = (JSONObject) message;

                    String sender = (String) jsonMessage.get("sender");
                    String channel = (String) jsonMessage.get("channel");
                    String title = (String) jsonMessage.get("title");
                    String content = (String) jsonMessage.get("content");
                    String approved = (String) jsonMessage.get("approved");
                    LocalDateTime timestamp = LocalDateTime.parse((String) jsonMessage.get("timestamp"));

                    if (sender == null ||
                            channel == null ||
                            title == null ||
                            content == null ||
                            approved == null ||
                            timestamp == null) {
                        continue;
                    }

                    Message newMessage = new Message(sender, "", channel, title, content, approved, timestamp);

                    if (newMessage.getChannel().equals(nameOfChannel)) {
                        channelMessages.add(newMessage);
                    }
                }

                fileReader.close();

                return channelMessages;
            }

            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * Updates the message in the file with the instance's attributes
     */
    @SuppressWarnings("unchecked")
    public void UpdateEntryInFile() {
        try {
            Path path = Path.of(MessageThread.FILE_PATH);
            File file = new File(path.toString());

            long fileSize = Files.size(path);

            // Load messages from the file
            if (fileSize > 0) {
                // Read all messages from the file
                FileReader fileReader = new FileReader(file);
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(fileReader);
                JSONArray jsonMessages = (JSONArray) jsonObject.get("messages");

                for (int i = 0; i < jsonMessages.size(); i++) {
                    JSONObject jsonMessage = (JSONObject) jsonMessages.get(i);

                    String sender = (String) jsonMessage.get("sender");
                    String recipient = (String) jsonMessage.get("recipient");
                    String title = (String) jsonMessage.get("title");
                    String channel = (String) jsonMessage.get("channel");
                    String content = (String) jsonMessage.get("content");
                    LocalDateTime timestamp = LocalDateTime.parse((String) jsonMessage.get("timestamp"));

                    if (this.sender.equals(sender) && this.recipient.equals(recipient) && this.title.equals(title)
                            && this.channel.equals(channel)
                            && this.content.equals(content)
                            && this.timestamp.equals(timestamp)) {

                        // Create a new JSONObject with the updated message
                        JSONObject updatedMessage = new JSONObject();
                        updatedMessage.put("sender", this.sender);
                        updatedMessage.put("recipient", this.recipient);
                        updatedMessage.put("title", this.title);
                        updatedMessage.put("title", this.title);
                        updatedMessage.put("content", this.content);
                        updatedMessage.put("approved", this.approved);
                        updatedMessage.put("timestamp", this.timestamp.toString());

                        // Replace the old JSONObject with the new one in the JSONArray
                        jsonMessages.set(i, updatedMessage);

                        // Update the JSONObject with the new JSONArray
                        jsonObject.put("messages", jsonMessages);

                        // Beautify the JSON before writing it to the file
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        String prettyJson = gson.toJson(jsonObject);

                        // Append the JSON message to the file
                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.write(prettyJson);

                        fileReader.close();
                        fileWriter.close();
                    }
                }

            }
        } catch (Exception e) {
        }
    }

    @Override
    public String getName() {
        return this.title;
    }
}
