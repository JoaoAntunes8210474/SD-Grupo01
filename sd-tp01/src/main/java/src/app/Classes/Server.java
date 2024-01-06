package src.app.Classes;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import src.app.Classes.Models.User;
import src.app.Classes.Threads.ServerThreads;

public class Server {
    private List<User> users; // List to store logged in users
    private List<User> allUsers; // List to store all registered users

    public Server() {
        this.users = new ArrayList<>();
        this.allUsers = AuthHandler.getAllRegisteredUsers();
    }

    /**
     * Get a list of all registered users
     * 
     * @return a list of all registered users
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(this.users);
    }

    /**
     * Get a list of all logged in users
     * 
     * @return a list of all logged in users
     */
    public List<User> getLoggedInUsers() {
        return new ArrayList<>(this.users);
    }

    /**
     * Method that starts the server
     */
    public void startServer() {

        // Server loop
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(3000);
            System.out.println("Server started. Listening for connections...");

            // Variable to control the server loop
            boolean listeningToUsers = true;

            while (listeningToUsers) {
                // Accept incoming client connections
                Socket clientSocket = serverSocket.accept();

                // Handle each client connection in a separate thread
                ServerThreads serverThreads = new ServerThreads(clientSocket, this.users, this.allUsers);
                serverThreads.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close the server socket in the 'finally' block to ensure proper cleanup
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
