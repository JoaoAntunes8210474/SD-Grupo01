package src.app.Classes;

import java.io.IOException;
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

    // List to store registered users
    private List<User> users;

    // Scanner to read user input
    private Scanner scanner;

    // Constructor
    public ServerThreads(Socket clientSocket, List<User> users) {
        this.clientSocket = clientSocket;
        this.users = users;
        try {
            this.scanner = new Scanner(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to clear the terminal
     */
    private static void clearTerminal() {
        for (int i = 0; i < 30; i++) {
            System.out.println();
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
            System.out.println("Choose an option:");
            for (int i = 0; i < options.size(); i++) {
                System.out.println(options.get(i) + " - " + messages.get(i));
            }
            option = scanner.nextInt();
        }
        return option;
    }

    /**
     * Method to register a user
     * 
     * @param user user to be registered
     */
    private void registerUser(User user) {
        this.users.add(user);
        System.out.println("User " + user.getName() + " registered successfully.");
    }

    /**
     * Method to login a user
     * 
     * @param username username of the user's possible account
     * @param password password of the user's possible account
     * @return true if the user can login, false otherwise
     */
    private boolean loginUser(String username, String password) {
        for (User user : users) {
            if (user.getName().equals(username) && user.getPassword().equals(password)) {
                System.out.println("User " + username + " logged in successfully.");
                return true;
            }
        }
        System.out.println("Invalid username or password.");
        return false;
    }

    /**
     * Method to run the thread
     */
    public void run() {
        try {
            int option = -1;
            while (option != 0) {
                option = getMenuOption(List.of(0, 1, 2), List.of("Exit", "Register", "Login"));
                switch (option) {
                    case 1:
                        clearTerminal();

                        System.out.println("Enter your username:");
                        String username = scanner.next();
                        System.out.println("Enter your password:");
                        String password = scanner.next();
                        registerUser(new User(username, password, "Private"));

                        clearTerminal();

                        System.out.println("User array:\n {\n" + this.users.toString() + "\n}");
                        break;
                    case 2:
                        clearTerminal();

                        System.out.println("Enter your username:");
                        username = scanner.next();
                        System.out.println("Enter your password:");
                        password = scanner.next();
                        loginUser(username, password);

                        clearTerminal();

                        System.out.println("User logged in successfully.");
                        break;
                    default:
                        break;
                }
            }

            scanner.close();
        } finally {
            try {
                clientSocket.close();
                scanner.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
