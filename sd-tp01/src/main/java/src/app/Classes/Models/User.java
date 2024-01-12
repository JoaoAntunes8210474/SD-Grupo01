package src.app.Classes.Models;

import java.util.ArrayList;
import java.util.List;

import src.app.Classes.Threads.MessageThread;
import src.app.Classes.Threads.ReplyThread;
import src.app.Interfaces.IUser;

/**
 * User class
 * 
 * @implements IUser
 */
public abstract class User implements IUser {
    // Attributes
    private String username;
    private String password;
    private String rank;
    private List<Message> messages; // List to store messages

    // Constructor Method
    public User(String name, String password, String rank) {
        this.username = name;
        this.password = password;
        this.rank = rank;
        this.messages = new ArrayList<>();
    }

    // Getters and Setters Methods

    /**
     * Get the name of the client
     * 
     * @return the name of the client
     */
    @Override
    public String getName() {
        return this.username + " ---- " + this.rank;
    }

    /**
     * Get the name of the client
     * 
     * @return the name of the client
     */
    public String getUsername() {
        return this.username;
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
    public void setUsername(String name) {
        this.username = name;
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
        System.out.println(this.username + " has requested approval for a complex task from " + approver.getUsername());
        // Add logic for handling the approval process
    }

    /**
     * Method to print the user's information
     * 
     * @return the user's information
     */
    public String toString() {
        return "Name: " + this.username + "\nPassword: " + this.password + "\nRank: " + this.rank + "\n";
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
                new Message(senderOfTheMessage, this.username, "", title, content));
        messageThread.start();
    }

    /**
     * Send a reply to a message
     * @return ReplyObject
     */
    public void sendReply(String content, String originalMessageTitle, String recipient,
            String nameOfTheChannel, boolean pingRecipient) {
        ReplyThread replyThread = new ReplyThread(new Reply(content, this.username, originalMessageTitle, recipient,
                nameOfTheChannel, pingRecipient));
        replyThread.start();
    }

    // Method to receive a message
    public void receivePersonalMessages() {
        this.messages = Message.readMessagesFromFileForUser(this.username);
    }

    public void receiveChannelMessages(String channel) {
        this.messages = Message.readMessagesFromFileForChannel(channel);
    }

    /**
     * Method to find a message by its title
     * @param title the title of the message to be found
     * @return the message with the given title
     */
    public Message findMessageByTitle(String title) {
        for (Message message : this.messages) {
            if (message.getTitle().equals(title)) {
                return message;
            }
        }

        return null;
    }

}
