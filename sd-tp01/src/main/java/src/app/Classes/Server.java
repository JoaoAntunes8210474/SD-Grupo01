package src.app.Classes;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import src.app.Classes.Models.Channel;
import src.app.Classes.Models.User;
import src.app.Classes.Threads.HeartbeatReaderThread;
import src.app.Classes.Threads.NotifierThreads;
import src.app.Classes.Threads.ServerThreads;

public class Server {
    private List<User> users; // List to store logged in users
    private List<User> allUsers; // List to store all registered users
    private List<Channel> channels; // List to store all channels
    private List<HeartbeatReaderThread> heartbeatThreads; // List to store all heartbeat threads

    public Server() {
        this.users = new ArrayList<>();
        this.allUsers = AuthHandler.getAllRegisteredUsers();
        this.channels = ChannelHandler.getAllChannels();
        this.heartbeatThreads = new ArrayList<>();
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

            // Start the notifier threads
            NotifierThreads notifierThreads = new NotifierThreads();
            notifierThreads.start();

            while (listeningToUsers) {
                // Accept incoming client connections
                System.out.println("Waiting for client connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected.");

                // Handle each client connection in a separate thread
                ServerThreads serverThreads = new ServerThreads(clientSocket, this.users, this.allUsers, this.channels,
                        this.heartbeatThreads);
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
