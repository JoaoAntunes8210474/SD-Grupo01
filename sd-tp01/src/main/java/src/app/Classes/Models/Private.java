package src.app.Classes.Models;

/**
 * Private
 * User with rank Private
 */
public class Private extends User {
    public Private(String name, String password) {
        super(name, password, "Private");
    }
}
