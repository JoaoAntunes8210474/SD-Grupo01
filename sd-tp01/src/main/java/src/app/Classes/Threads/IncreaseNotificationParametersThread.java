package src.app.Classes.Threads;

import src.app.Classes.Models.User;

import java.util.List;

public class IncreaseNotificationParametersThread extends Thread {
    private static final String FILE_PATH = "sd-tp01/src/main/java/src/app/Data/Stats.json";

    // Logged in user
    private User loggedInUser;

    // List of all logged in users
    private List<User> loggedInUsers;

    // Variable to check which item to increment
    private String itemToIncrement;

    // Variable to check if it is an increment or a decrement
    private boolean isItAnIncrement;

    public IncreaseNotificationParametersThread(User loggedInUser, List<User> loggedInUsers, String itemToIncrement, boolean isItAnIncrement) {
        super("[IncreaseNotificationParametersThread]");
        
        this.loggedInUser = loggedInUser;
        this.loggedInUsers = loggedInUsers;
        this.itemToIncrement = itemToIncrement;
    }

    /**
     * Change the number of solicitations made based on the boolean isItAnIncrement
     */
    private void changeSolicitationsMade() {
        
    }

    /**
     * Change the number of approvals made based on the boolean isItAnIncrement
     */
    private void changeApprovalsMade() {
        
    }

    /**
     * Change the number of connections made based on the boolean isItAnIncrement
     */
    private void changeConnectionsMade() {
        
    }

    @Override
    public void run() {
        if (itemToIncrement.equals("SolicitationsMade")) {
            changeSolicitationsMade();
        } else if (itemToIncrement.equals("ApprovalsMade")) {
            changeApprovalsMade();
        } else if (itemToIncrement.equals("ConnectionsMade")) {
            changeConnectionsMade();
        } else {
            System.out.println("[Invalid item to increment]");
        }
    }
}
