package src.app.Classes;

import src.app.Interfaces.IUser;

/**
 * User class
 * @implements IUser
 */
public class User implements IUser{
    // Attributes
    private String name;

    private String password;

    private String rank;

    // Constructor Method
    public User(String name, String password, String rank) {
        this.name = name;
        setPassword(password);  // Enforce password constraints in the constructor
        this.rank = rank;
    }

    // Getters and Setters Methods

    /**
     * Get the name of the client
     * @return the name of the client
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the password of the client
     * @return the password of the client
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Get the rank of the client
     * @return the rank of the client
     */
    public String getRank() {
        return this.rank;
    }

    /**
     * Set the name of the client
     * @param name the new name of the client
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the password of the client
     * @param password the new password of the client
     */
    public void setPassword(String password) {
        if (password.length() >= 8) {
            this.password = password;
        } else {
            System.out.println("Password must have at least 8 characters.");
        }
    }

    /**
     * Set the rank of the client
     * @param rank the new rank of the client
     */
    public void setRank(String rank) {
        this.rank = rank;
    }

    // Methods
    /**
     * Send a message to another user
     * @param recipient the user who will receive the message
     * @param message the message to be sent
     * @return the message sent
     * @throws Exception if the recipient is not a valid user
     * @throws Exception if the message is empty or too long
     * 
     */
    public void sendMessage(User recipient, String message) {
        System.out.println("Message sent from " + this.name + " to " + recipient.getName() + ": " + message);
    }
    
    /**
     * Request a complex task (e.g., missile launch)
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
     * @return the user's information
     */
    public String toString () {
        return "Name: " + this.name + "\nPassword: " + this.password + "\nRank: " + this.rank + "\n";
    }
}
