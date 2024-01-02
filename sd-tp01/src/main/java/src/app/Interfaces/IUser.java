package src.app.Interfaces;

import src.app.Classes.Models.User;

/**
 * IUser
 * Contains the methods that must be implemented by the User class.
 * These methods are access methods to the User's attributes.
 */
public interface IUser {
    // Access methods

    // Getters
    public String getName();
    public String getPassword();
    public String getRank();

    // Setters
    public void setName(String name);
    public void setPassword(String password);
    public void setRank(String rank);

    // Methods
    public void registerMessage(String message, String senderOfTheMessage);
    public void requestComplexTask(User approver);
}
