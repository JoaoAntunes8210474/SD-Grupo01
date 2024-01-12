package src.app.Classes.Threads;

import java.util.List;
import java.util.Scanner;

import src.app.Classes.Models.User;

public class HeartbeatReaderThread extends Thread {

    // Attributes
    private User loggedInUser;
    private Scanner userInput;
    private List<User> listOfConnectedUsers;

    // Constructor
    public HeartbeatReaderThread(User loggedInUser, Scanner userInput, List<User> listOfConnectedUsers) {
        super("[HeartbeatReaderThread]");
        this.loggedInUser = loggedInUser;
        this.userInput = userInput;
        this.listOfConnectedUsers = listOfConnectedUsers;
    }

    // Methods
    public void run() {
        while (true) {
            try {
                // Read the user input
                String input = userInput.nextLine();

                if (input.equals("hearbeat")) {
                    Thread.sleep(1000);
                } else {
                    System.out.println(this.getName() + " - " + "User " + loggedInUser.getUsername()
                            + " disconnected due to inactivity.");
                    listOfConnectedUsers.remove(loggedInUser);
                    //new IncreaseNotificationParametersThread(listOfConnectedUsers, "ConnectionsMade", false)
                      //      .start();
                    userInput.close();
                    break;
                }
            } catch (Exception e) {
            }
        }
    }
}
