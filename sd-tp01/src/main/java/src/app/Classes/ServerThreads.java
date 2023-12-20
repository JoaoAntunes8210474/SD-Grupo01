package src.app.Classes;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

/**
 * ServerThreads
 * Thread to handle server functionalities
 */
public class ServerThreads extends Thread {
    // Attributes

    // Socket to connect to the server
    private Socket clientSocket;

    // PrintWriter to write to the client's input stream
    private PrintWriter out;

    // List to store registered users
    private List<User> users;

    // Scanner to read user input
    private Scanner scanner;

    // Constructor
    public ServerThreads(Socket clientSocket, List<User> users) {
        this.clientSocket = clientSocket;
        this.users = users;
        try {
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.scanner = new Scanner(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to clear the terminal
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
            this.out.println("Choose an option:");
            for (int i = 0; i < options.size(); i++) {
                this.out.println(options.get(i) + " - " + messages.get(i));
            }
            option = scanner.nextInt();
        }
        return option;
    }

    /**
     * Method to register a new user
     */
    private void registerForm() {
        this.clearTerminal();

        this.out.println("Enter your username:");
        String username = scanner.next();

        this.out.println("Enter your password:");
        String password = scanner.next();

        String rank;
        boolean isValidRank = false;
        do {
            this.out.println("Enter your rank:");
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
                this.out.println("Invalid rank.\nExisting ranks: Private, Sergeant or General.");
            }

        } while (!isValidRank);

        this.clearTerminal();
        if (AuthHandler.registerUser(username, password, rank)) {
            this.out.println("User registered successfully.");
        } else {
            this.out.println("User already exists.");
        }
    }

    /**
     * Method to login a user
     */
    private void loginForm() {
        this.clearTerminal();

        this.out.println("Enter your username:");
        String username = scanner.next();
        this.out.println("Enter your password:");
        String password = scanner.next();
        boolean isLoggedIn = AuthHandler.verifyLogin(username, password);

        if (!isLoggedIn) {
            this.out.println("Credentials are incorrect.");
        } else {
            // User logged in successfully
            this.clearTerminal();
            this.out.println("User logged in successfully.");
            userMenu(username);
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
    private void userMenu(String username) {
        int optionSelected = -1;
        while (optionSelected != 0) {
            optionSelected = getMenuOption(List.of(0, 1, 2),
                    List.of("Exit", "Send Message", "List Users"));
            switch (optionSelected) {
                case 1:
                    this.out.println("Message:\n");
                    // sendMessage();
                    break;
                case 2:
                    this.out.println("Users:\n");
                    // listAllUsers();
                    break;
                default:
                    break;
            }
        }
    }

}
