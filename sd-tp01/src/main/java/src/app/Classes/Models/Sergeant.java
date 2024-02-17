package src.app.Classes.Models;

/**
 * Sergeant
 * User with rank Sergeant
 */
public class Sergeant extends User {
    public Sergeant(String name, String password) {
        super(name, password, "Sergeant");
    }
}
