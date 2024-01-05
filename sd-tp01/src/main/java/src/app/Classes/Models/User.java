package src.app.Classes.Models;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import src.app.Classes.Threads.MessageThread;
import src.app.Interfaces.IUser;

/**
 * User class
 * 
 * @implements IUser
 */
public abstract class User implements IUser {

    // File path for storing all messages
    private static final String MESSAGES_FILE_PATH = "sd-tp01/src/main/java/src/app/Data/Messages.json";

    // Attributes
    private String name;
    private String password;
    private String rank;
    private List<Message> messages; // List to store messages

    // Constructor Method
    public User(String name, String password, String rank) {
        this.name = name;
        setPassword(password); // Enforce password constraints in the constructor
        this.rank = rank;
        loadMessagesFromFile(); // Load messages from file
    }

    // Getters and Setters Methods

    /**
     * Get the name of the client
     * 
     * @return the name of the client
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the password of the client
     * 
     * @return the password of the client
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Get the rank of the client
     * 
     * @return the rank of the client
     */
    public String getRank() {
        return this.rank;
    }

    /**
     * Get the messages of the client
     * 
     * @return the messages of the client
     */
    public List<Message> getMessages() {
        return this.messages;
    }

    /**
     * Set the name of the client
     * 
     * @param name the new name of the client
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the password of the client
     * 
     * @param password the new password of the client
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Set the rank of the client
     * 
     * @param rank the new rank of the client
     */
    public void setRank(String rank) {
        this.rank = rank;
    }

    // Methods
    /**
     * Request a complex task (e.g., missile launch)
     * 
     * @param approver the user who will approve the task
     * @throws Exception if the approver is not a valid user
     * 
     */
    public void requestComplexTask(User approver) {
        System.out.println(this.name + " has requested approval for a complex task from " + approver.getName());
        // Add logic for handling the approval process
    }

    /**
     * Method to print the user's information
     * 
     * @return the user's information
     */
    public String toString() {
        return "Name: " + this.name + "\nPassword: " + this.password + "\nRank: " + this.rank + "\n";
    }

    /**
     * Send a message to another user
     * 
     * @param content            the content of the message to be sent
     * @param senderOfTheMessage the user who will send the message to this user
     * @throws Exception if the recipient is not a valid user
     * @throws Exception if the message is empty or too long
     */
    public void registerMessage(String title, String content, String senderOfTheMessage) {
        // Write to json file the message, the sender and the recipient
        MessageThread messageThread = new MessageThread(
                new Message(senderOfTheMessage, this.name, title, content));
        messageThread.start();
    }

    // Method to receive a message
    public void receiveMessages() {
        this.messages = Message.readMessagesFromFileForUser(this.name);
    }

    // Method to load messages for the authenticated user from the global
    // Messages.json file
    public void loadMessagesFromFile() {
        try {
            Path filePath = Path.of(MESSAGES_FILE_PATH);

            if (Files.exists(filePath)) {
                // Read all messages from the file
                FileReader fileReader = new FileReader(filePath.toFile());
                List<Message> allMessages = readMessagesFromJson(fileReader);
                fileReader.close();

                // Filter messages based on the authenticated user being the recipient
                // List<Message> userMessages = allMessages.stream()
                // .filter(message -> message.getRecipient().equals(this.name))
                // .collect(Collectors.toList());

                // Alternative way to filter messages based on the authenticated user being
                List<Message> userMessages = new ArrayList<>();
                for (Message message : allMessages) {
                    if (message.getRecipient().equals(this.name)) {
                        userMessages.add(message);
                    }
                }

                // Update the user's messages
                this.messages.addAll(userMessages);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to read messages from JSON
    private List<Message> readMessagesFromJson(FileReader fileReader) {
        List<Message> loadedMessages = new ArrayList<>();

        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(fileReader);
            JSONArray jsonMessages = (JSONArray) jsonObject.get("messages");

            for (Object obj : jsonMessages) {
                JSONObject jsonMessage = (JSONObject) obj;
                String sender = (String) jsonMessage.get("sender");
                String recipient = (String) jsonMessage.get("recipient");
                String title = (String) jsonMessage.get("title");
                String content = (String) jsonMessage.get("content");

                // You may need to parse the timestamp string into LocalDateTime
                // LocalDateTime timestamp = LocalDateTime.parse((String)
                // jsonMessage.get("timestamp"));

                Message message = new Message(sender, recipient, title, content);
                // message.setTimestamp(timestamp); // Set the timestamp if needed
                loadedMessages.add(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return loadedMessages;
    }

}
