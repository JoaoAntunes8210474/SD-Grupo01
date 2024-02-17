package src.app.Classes.Models;

/**
 * General
 * User with rank General
 */
public class General extends User {
    public General(String name, String password) {
        super(name, password, "General");
    }
}
