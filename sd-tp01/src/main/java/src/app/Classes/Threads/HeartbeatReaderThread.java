package src.app.Classes.Threads;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import src.app.Classes.Models.User;

public class HeartbeatReaderThread extends Thread {
    private static final int MIN_MESSAGES = 5;

    private static final long TIME_PERIOD = 2000;

    // Attributes
    private User loggedInUser;
    private List<User> listOfConnectedUsers;
    private boolean listening;
    private int numberOfTimesUserWasInactive;
    private Queue<Long> messageTimestamps;
    private long firstMessageTimestamp;

    // Constructor
    public HeartbeatReaderThread(User loggedInUser, List<User> listOfConnectedUsers) {
        super("[HeartbeatReaderThread]");
        this.loggedInUser = loggedInUser;
        this.listOfConnectedUsers = listOfConnectedUsers;
        this.listening = true;
        this.numberOfTimesUserWasInactive = 0;
        this.messageTimestamps = new LinkedList<>();
        this.firstMessageTimestamp = System.currentTimeMillis();
    }

    // Methods
    public void run() {
        while (this.listening) {
            try {
                // Record the timestamp of the message
                messageTimestamps.add(System.currentTimeMillis());

                if (this.firstMessageTimestamp < System.currentTimeMillis() - TIME_PERIOD) {
                    // Check if less than 5 messages were sent in a 2-second span
                    if (messageTimestamps.size() < MIN_MESSAGES) {
                        // Less than 5 messages were sent in a 2-second span
                        numberOfTimesUserWasInactive++;
                    }

                    if (numberOfTimesUserWasInactive == 5) {
                        this.interrupt();
                    }

                    this.firstMessageTimestamp = System.currentTimeMillis();
                    messageTimestamps.clear();
                }

                Thread.sleep(200);
            } catch (Exception e) {
                System.out.println(this.getName() + " - " + "User " + loggedInUser.getUsername()
                        + " disconnected due to inactivity.");

                new IncreaseNotificationParametersThread("numberConnections", false)
                        .start();

                for (User user : listOfConnectedUsers) {
                    if (user.getUsername().equals(loggedInUser.getUsername())) {
                        listOfConnectedUsers.remove(user);
                        break;
                    }
                }

                System.out.println(this.getName() + " - " + "Number of connected users: "
                        + listOfConnectedUsers.size());

                this.listening = false;
                break;
            }
        }
    }

    public User getLoggedInUser() {
        return this.loggedInUser;
    }
}
