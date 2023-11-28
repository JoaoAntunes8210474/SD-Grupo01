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
     * Method to login a user
     * 
     * @param username username of the user's possible account
     * @param password password of the user's possible account
     * @return true if the user can login, false otherwise
     */
    private boolean loginUser(String username, String password) {
        for (User user : users) {
            if (user.getName().equals(username) && user.getPassword().equals(password)) {
                this.out.println("User " + username + " logged in successfully.");
                return true;
            }
        }
        this.out.println("Invalid username or password.");
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

                        AuthHandler.registerUser(username, password, rank);

                        this.clearTerminal();
                        break;
                    case 2:
                        this.clearTerminal();

                        this.out.println("Enter your username:");
                        username = scanner.next();
                        this.out.println("Enter your password:");
                        password = scanner.next();
                        boolean isLoggedIn = AuthHandler.verifyLogin(username, password);

                        if (!isLoggedIn) {
                            this.out.println("Credentials are incorrect.");
                            break;
                        } else {
                            this.clearTerminal();
                            this.out.println("User logged in successfully.");
                            int option2 = getMenuOption(List.of(0, 1, 2), List.of("Exit", "Register", "Login"));
                            //metodo que recebe option2 e faz o que tem a fazer PARA N DAR CLUTTER NO CODIGO
                        }

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
