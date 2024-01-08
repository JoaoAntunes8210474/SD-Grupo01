package src.app.Classes.Threads;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import src.app.Classes.AuthHandler;
import src.app.Classes.Models.General;
import src.app.Classes.Models.Message;
import src.app.Classes.Models.ReplyObject;
import src.app.Classes.Models.User;

import src.app.Interfaces.IGetName;

/**
 * Class to handle the threads for the server
 */
public class ServerThreads extends Thread {
    //// Attributes

    // Socket to connect to the server
    private Socket clientSocket;

    // PrintWriter to write to the client's input stream
    private PrintWriter out;

    // List to store logged in users
    private List<User> loggedInUsers;

    // List to store all registered users
    private List<User> registeredUsers;

    // Scanner to read user input
    private Scanner scanner;

    // Variable to control the heartbeat
    private boolean isHeartbeatBeingRead;

    //// Constructor
    /**
     * Constructor for the ServerThreads class
     * 
     * @param clientSocket the socket to connect to the server
     * @param users        the list of logged in users
     */
    public ServerThreads(Socket clientSocket, List<User> LoggedInUsers, List<User> registeredUsers) {
        this.clientSocket = clientSocket;
        this.loggedInUsers = LoggedInUsers;
        this.registeredUsers = registeredUsers;
        this.isHeartbeatBeingRead = false;
        try {
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.scanner = new Scanner(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //// Methods
    /**
     * Clear the terminal
     */
    private void clearTerminal() {
        for (int i = 0; i < 30; i++) {
            this.out.println();
        }
    }

    /**
     * Method to get the option chosen by the user in the menu
     * 
     * @param options  list of options to choose from
     * @param messages list of messages to display to the user
     * @return the option chosen by the user
     */
    private int getMenuOption(List<Integer> options, List<String> messages) {
        int option = -1;
        boolean wasMenuAlreadyDisplayed = false;
        while (option < 0 || option > options.size()) {
            if (!wasMenuAlreadyDisplayed) {

                this.out.println("[Choose an option:]");
                this.out.println("-----------------------");
                for (int i = 0; i < options.size(); i++) {
                    this.out.println(options.get(i) + " - " + messages.get(i));
                }

                this.out.println("-----------------------");
            }

            try {
                String userInput = scanner.nextLine();

                if (userInput.contains("userInput:")) {
                    userInput = userInput.charAt("userInput:".length()) + "";
                    option = Integer.parseInt(userInput);
                } else {
                    wasMenuAlreadyDisplayed = true;
                }
            } catch (NumberFormatException e) {
            }
        }
        return option;
    }

    /**
     * Method to handle the user input
     * 
     * @param userInput the user input
     * @return the user input without the prefix
     */
    private String removeUserInputPrefix(String userInput) {
        return userInput.substring("userInput:".length());
    }

    /**
     * Find a user by name in the list of registered users
     * 
     * @param name the name of the user to find
     * @return the User object if found, null otherwise
     */
    private User findUserByName(String name) {
        for (User user : registeredUsers) {
            if (user.getName().equals(name)) {
                return user;
            }
        }

        return null;
    }

    /**
     * Method to listen to user input while in a channel
     * If the user sends the message "userInput:0", stop listening and display the
     * previous menu
     * 
     * @param listening
     */
    private void listeningToUserInputWhileInChannel(boolean listening) {
        String userInput;
        try {
            while (listening) {
                userInput = scanner.nextLine();

                if (userInput.equals("userInput:0")) {
                    listening = false;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to display the list of users
     */
    private void displayUserList(int listIndex, List<Integer> options, List<String> messages,
            List<? extends IGetName> targets) {
        this.out.println("[List of registered users to send a message to:]");
        this.out.println("-----------------------");

        options.add(0);
        messages.add("Back");

        int counter = 1;

        for (int i = 0; i < Math.min(targets.size() - (listIndex * 7), 7); i++) {
            options.add(counter);
            messages.add(targets.get((listIndex * 7) + i).getName());
            counter++;
        }

        options.add(8);
        options.add(9);
        messages.add("Previous Page");
        messages.add("Next Page");
    }

    /**
     * Method to send a message to a user
     */
    private ReplyObject sendMessage(User loggedInUser) {
        ReplyObject result = selectTarget(this.registeredUsers);
        String recipientName = result.getMessage();

        if (!recipientName.isEmpty()) {
            this.out.println("\n[Message Title:]");
            String title = scanner.nextLine();

            if (title.contains("userInput:")) {
                title = this.removeUserInputPrefix(title);
            }

            this.out.println("\n[Message Content:]");
            String content = scanner.nextLine();

            if (content.contains("userInput:")) {
                content = this.removeUserInputPrefix(content);
            }

            User recipient = findUserByName(recipientName);

            if (recipient != null) {
                recipient.registerMessage(title, content, loggedInUser.getName());
                return new ReplyObject(true, "[Message sent successfully.]");
            } else {
                this.out.println("[User not found.]");
                return new ReplyObject(false, "User not found.");
            }
        }

        return new ReplyObject(false, "Message not sent.");
    }

    /**
     * Method to select a target (message or user) from a list
     */
    private ReplyObject selectTarget(List<? extends IGetName> targets) {
        String target = "";
        int listIndex = 0;
        int selectedOption = -1;
        do {
            List<Integer> options = new ArrayList<Integer>();
            List<String> messages = new ArrayList<String>();

            // Display list of registered users
            displayUserList(listIndex, options, messages, targets);

            // Get user input to select recipient
            selectedOption = getMenuOption(options, messages);

            if (selectedOption == 8 && listIndex > 0) {
                listIndex--;
            } else if (selectedOption == 9 && selectedOption - 2 < targets.size() - (listIndex * 7)) {
                listIndex++;
            } else if (selectedOption > 0 && selectedOption <= Math.min(targets.size() - (listIndex * 7), 7)) {
                target = targets.get((listIndex * 7) + (selectedOption - 1)).getName();
            } else if (selectedOption == 0) {
                return new ReplyObject(false);
            }
        } while (target.isEmpty());

        return new ReplyObject(true, target);
    }

    /**
     * Method to handle the menu for the user authentication
     * 
     * @param option
     */
    private void authMenu() {
        int optionSelected = -1;
        while (optionSelected != 0) {
            optionSelected = getMenuOption(List.of(0, 1, 2), List.of("Exit", "Register", "Login"));
            switch (optionSelected) {
                case 1:
                    registerForm();
                    break;
                case 2:
                    loginForm();
                    break;
                default:
                    this.out.println("[Quitting...]");
                    break;
            }
        }

        scanner.close();
    }

    /**
     * Method to register a new user
     */
    private void registerForm() {
        this.clearTerminal();

        String username;
        String password;
        String rank;

        boolean isUsernameInvalid = false;
        boolean isPasswordInvalid = false;
        boolean isRankInvalid = false;

        boolean wasUsernameMessageDisplayed = false;
        boolean wasPasswordMessageDisplayed = false;
        boolean wasRankMessageDisplayed = false;

        do {
            if (!wasUsernameMessageDisplayed) {
                this.out.println("[Enter your username:]");
            }

            username = scanner.nextLine();

            if (username.contains("userInput:")) {
                username = this.removeUserInputPrefix(username);

                if (!AuthHandler.validateClientUsername(username)) {
                    isUsernameInvalid = true;
                    this.out.println("[Invalid username.]\n[Username must have between 3 to 20 characters.]");
                }
            } else {
                wasUsernameMessageDisplayed = true;
            }
        } while (isUsernameInvalid);

        do {
            if (!wasPasswordMessageDisplayed) {
                this.out.println("[Enter your password:]");
            }

            password = scanner.nextLine();

            if (password.contains("userInput:")) {
                password = this.removeUserInputPrefix(password);

                if (!AuthHandler.validateClientPassword(password)) {
                    isPasswordInvalid = true;
                    this.out.println("[Invalid password.]\n[Password must have between 6 to 12 characters.]");
                }
            } else {
                wasPasswordMessageDisplayed = true;
            }

        } while (isPasswordInvalid);

        do {
            if (!wasRankMessageDisplayed) {
                this.out.println("[Enter your rank:]");
            }

            rank = scanner.nextLine();
            rank = rank.toLowerCase();

            if (rank.contains("userInput:")) {
                rank = this.removeUserInputPrefix(rank);

                if (!AuthHandler.validateClientRank(rank)) {
                    isRankInvalid = true;
                    this.out.println("[Invalid rank.]\n[Existing ranks: Private, Sergeant or General.]");
                }
            } else {
                wasRankMessageDisplayed = true;
            }
        } while (isRankInvalid);

        this.clearTerminal();

        ReplyObject wasRegistered = AuthHandler.registerUser(username, password, rank);

        // If the user was registered successfully, add him to the list of registered
        if (wasRegistered.getWasOperationSuccessful()) {
            this.out.println("[User registered successfully.]");
            this.registeredUsers.add(wasRegistered.getUser());
        } else {
            this.out.println("[User already exists.]");
        }
    }

    /**
     * Method to login a user
     */
    private void loginForm() {
        this.clearTerminal();

        this.out.println("[Enter your username:]");
        String username = scanner.nextLine();

        if (username.contains("userInput:")) {
            username = this.removeUserInputPrefix(username);
        }

        this.out.println("[Enter your password:]");
        String password = scanner.nextLine();

        if (password.contains("userInput:")) {
            password = this.removeUserInputPrefix(password);
        }

        ReplyObject isLoggedIn = AuthHandler.verifyLogin(username, password);

        if (!isLoggedIn.getWasOperationSuccessful()) {
            this.out.println("[Credentials are incorrect.]");
        } else {
            // User logged in successfully
            this.clearTerminal();
            this.out.println("[User logged in successfully.]");
            // Create a thread to increment number of logged in users
            this.loggedInUsers.add(isLoggedIn.getUser());
            // new IncrementLoggedInUsersThread(this.users).start();
            userMenu(isLoggedIn.getUser());
        }
    }

    /**
     * Method to handle the menu for the user
     * 
     * @param username the username of the user
     */
    private void userMenu(User loggedInUser) {
        if (!isHeartbeatBeingRead) {
            isHeartbeatBeingRead = true;
            // new HeartbeatReaderThread(loggedInUser, scanner, loggedInUsers).start();
        }

        boolean listening = true;
        int optionSelected = -1;

        while (optionSelected != 0) {
            List<Integer> options;
            List<String> messages;

            if (loggedInUser instanceof General) {
                options = List.of(0, 1, 2, 3, 4, 5, 6);
                messages = List.of("Back", "Send Message", "Approve Messages", "See all messages",
                        "Solicitation notifications",
                        "Approval notifications", "Connection notifications");
            } else {
                options = List.of(0, 1, 2, 3, 4, 5);
                messages = List.of("Back", "Send Message", "Approve Messages", "See all messages",
                        "Solicitation notifications",
                        "Approval notifications");
            }

            optionSelected = getMenuOption(options, messages);

            this.clearTerminal();

            handleMenuOption(loggedInUser, optionSelected, listening);
        }
    }

    /**
     * Method to handle the menu option selected by the user
     * 
     * @param optionSelected the option selected by the user
     */
    private void handleMenuOption(User loggedInUser, int optionSelected, boolean listening) {
        ReplyObject result;
        String messageTitle;

        switch (optionSelected) {
            case 1:
                result = sendMessage(loggedInUser);

                this.clearTerminal();

                if (result.getWasOperationSuccessful()) {
                    this.out.println(result.getMessage());
                } else {
                    this.out.println(result.getMessage());
                }

                break;
            case 2:
                loggedInUser.receiveMessages(); // Loads messages into array

                result = selectTarget(loggedInUser.getMessages()); // Lists messages
                messageTitle = result.getMessage();

                this.clearTerminal();

                if (!messageTitle.isEmpty()) {
                    Message message = loggedInUser.findMessageByTitle(messageTitle);
                    // I want to change the approved field to "Approved by <username>"

                    if (message != null) {
                        this.out.println("===============Message_Information===============");
                        this.out.println("Title: " + message.getTitle());
                        this.out.println("Content: " + message.getContent());
                        this.out.println("=================================================\n");
                        this.out.println("[Do you want to approve this message?]");

                        int selectedOption = getMenuOption(List.of(0, 1, 2), List.of("Back", "Yes", "No"));

                        if (selectedOption == 1) {
                            message.setApproved("Approved by " + loggedInUser.getName());
                            message.UpdateEntryInFile();
                            this.out.println("[Message approved successfully.]");
                        } else if (selectedOption == 2) {
                            this.out.println("[Message not approved.]");
                        }
                    }
                }

                break;
            case 3:
                loggedInUser.receiveMessages(); // Loads messages into array

                if (loggedInUser.getMessages().isEmpty()) {
                    this.clearTerminal();
                    this.out.println("[No messages to display.]");
                    break;
                }

                result = selectTarget(loggedInUser.getMessages()); // Lists messages
                messageTitle = result.getMessage();

                this.clearTerminal();

                if (!messageTitle.isEmpty()) {
                    Message message = loggedInUser.findMessageByTitle(messageTitle);
                    // I want to print message information

                    if (message != null) {

                        this.out.println("===============Message_Information===============");
                        this.out.println("Title: " + message.getTitle());
                        this.out.println("Content: " + message.getContent());
                        this.out.println("Sender: " + message.getSender());
                        this.out.println("Recipient: " + message.getRecipient());
                        this.out.println("Approved: " + message.getApproved());
                        this.out.println("=================================================");
                    } else {
                        this.out.println("[Message not found...]");
                    }
                }
                break;
            case 4:
                this.out.println("JOIN_CHANNEL:" + NotifierThreads.SOLICITATIONS_MADE_CHANNELADDR);

                // Listen for inputs from the user
                listeningToUserInputWhileInChannel(listening);
                break;
            case 5:
                this.out.println("JOIN_CHANNEL:" + NotifierThreads.APPROVALS_MADE_CHANNELADDR);

                // Listen for inputs from the user
                listeningToUserInputWhileInChannel(listening);
                break;
            case 6:
                if (loggedInUser instanceof General) {
                    this.out.println("JOIN_CHANNEL:" + NotifierThreads.CONNECTIONS_MADE_CHANNELADDR);

                    // Listen for inputs from the user
                    listeningToUserInputWhileInChannel(listening);
                }

                break;
            default:
                break;
        }
    }

    /**
     * Method to run the thread
     */
    public void run() {
        try {
            authMenu();
        } finally {
            try {
                clientSocket.close();
                scanner.close();

                // Create a thread to decrement number of logged in users
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
