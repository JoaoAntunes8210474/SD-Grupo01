package src.app.Classes.Threads;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

import src.app.Classes.AuthHandler;
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
    public ServerThreads(Socket clientSocket, List<User> users) {
        this.clientSocket = clientSocket;
        this.loggedInUsers = users;
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
        while (option < 0 || option > options.size()) {
            this.out.println("[Choose an option:]");
            this.out.println("-----------------------");
            for (int i = 0; i < options.size(); i++) {
                this.out.println(options.get(i) + " - " + messages.get(i));
            }
            this.out.println("-----------------------");

            try {
                String userInput = scanner.nextLine();
                if (userInput.contains("userInput:")) {
                    userInput = userInput.charAt("userInput:".length()) + "";
                    option = Integer.parseInt(userInput);
                }
            } catch (NumberFormatException e) {
            }
        }
        return option;
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
     * Method to register a new user
     */
    private void registerForm() {
        this.clearTerminal();

        this.out.println("[Enter your username:]");
        String username = scanner.next();

        this.out.println("[Enter your password:]");
        String password = scanner.next();

        String rank;
        boolean isValidRank = false;
        do {
            this.out.println("[Enter your rank:]");
            rank = scanner.next();
            rank = rank.toLowerCase();

            switch (rank) {
                case "private":
                    isValidRank = true;
                    break;
                case "sergeant":
                    isValidRank = true;
                    break;
                case "general":
                    isValidRank = true;
                    break;
                default:
                    isValidRank = false;
                    break;
            }

            if (!isValidRank) {
                this.out.println("[Invalid rank.]\n[Existing ranks: Private, Sergeant or General.]");
            }

        } while (!isValidRank);

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
        String username = scanner.next();
        this.out.println("[Enter your password:]");
        String password = scanner.next();
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
                    break;
            }
        }

        scanner.close();
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
            if (loggedInUser.getRank().equals("General")) {
                optionSelected = showGeneralMenu();
            } else {
                optionSelected = showRegularUserMenu();
            }

            handleMenuOption(loggedInUser, optionSelected, listening);
        }
    }

    private int showGeneralMenu() {
        return getMenuOption(List.of(0, 1, 2, 3, 4, 5, 6),
                List.of("Back", "Send Message", "Reply to messages", "Received messages", "Solicitations", "Approvals",
                        "Connections"));
    }

    private int showRegularUserMenu() {
        return getMenuOption(List.of(0, 1, 2, 3, 4, 5),
                List.of("Back", "Send Message", "Reply to messages", "Received messages", "Solicitations",
                        "Approvals"));
    }

    private void handleMenuOption(User loggedInUser, int optionSelected, boolean listening) {
        switch (optionSelected) {
            case 1:
                sendMessage(loggedInUser);
                break;
            case 2:
                loggedInUser.receiveMessages();
                // CARREGOU MESSAGES AGR FALTA LISTAR (selectTarget) PARA VER e depois mandar
                break;
            case 3:
                loggedInUser.receiveMessages();
                // Listar mensagens recebidas

                String messageTitle = selectTarget(loggedInUser.getMessages()); // vai listar titles das msg

                if (!messageTitle.isEmpty()) {
                    Message message = loggedInUser.findMessageByTitle(messageTitle);

                    if (message != null) {
                        this.out.println(message.getContent());
                    } else {
                        this.out.println("Message not found.");
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
                if (loggedInUser.getRank().equals("General")) {
                    this.out.println("JOIN_CHANNEL:" + NotifierThreads.CONNECTIONS_MADE_CHANNELADDR);

                    // Listen for inputs from the user
                    listeningToUserInputWhileInChannel(listening);
                }

                break;
            default:
                break;
        }
    }

    private void sendMessage(User loggedInUser) {
        String recipientName = selectTarget(this.registeredUsers);
        if (!recipientName.isEmpty()) {
            this.out.println("\nMessage Title:");
            String title = scanner.next();
            this.out.println("\nMessage Content:");
            String content = scanner.next();
            User recipient = findUserByName(recipientName);

            if (recipient != null) {
                recipient.registerMessage(title, content, loggedInUser.getName());
            } else {
                this.out.println("User not found.");
            }
        }
    }

    private String selectTarget(List<? extends IGetName> targets) {
        String target = "";
        do {
            int listIndex = 0;
            int selectedOption = -1;
            List<Integer> options = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
            List<String> messages = List.of("Back");

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
            }
        } while (target.isEmpty());

        return target;
    }

    private void displayUserList(int listIndex, List<Integer> options, List<String> messages,
            List<? extends IGetName> targets) {
        this.out.println("[List of registered users to send a message to:]");
        this.out.println("-----------------------");

        int counter = 1;
        for (int i = 0; i < Math.min(targets.size() - (listIndex * 7), 7); i++) {
            messages.add(counter + " - " + targets.get((listIndex * 7) + i).getName());
            counter++;
        }

        messages.add("Next Page");
        messages.add("Previous Page");
    }

    /**
     * Find a user by name in the list of registered users
     * 
     * @param name the name of the user to find
     * @return the User object if found, null otherwise
     */
    private User findUserByName(String name) {
        for (User user : loggedInUsers) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null;
    }

}
