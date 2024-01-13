package src.app.Classes.Threads;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import src.app.Classes.AuthHandler;
import src.app.Classes.ChannelHandler;
import src.app.Classes.Models.Channel;
import src.app.Classes.Models.General;
import src.app.Classes.Models.Message;
import src.app.Classes.Models.Private;
import src.app.Classes.Models.Reply;
import src.app.Classes.Models.ReplyObject;
import src.app.Classes.Models.Sergeant;
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

    // List to store all channels
    private List<Channel> createdChannels;

    // List to store all heartbeat threads
    private List<HeartbeatReaderThread> heartbeatThreads;

    // Scanner to read user input
    private Scanner scanner;

    // Variable to control the heartbeat
    private boolean isHeartbeatBeingRead;

    // Variable to store the user input
    private String userInput;

    //// Constructor
    /**
     * Constructor for the ServerThreads class
     * 
     * @param clientSocket the socket to connect to the server
     * @param users        the list of logged in users
     */
    public ServerThreads(Socket clientSocket, List<User> LoggedInUsers, List<User> registeredUsers,
            List<Channel> createdChannels, List<HeartbeatReaderThread> heartbeatThreads) {
        this.clientSocket = clientSocket;
        this.loggedInUsers = LoggedInUsers;
        this.registeredUsers = registeredUsers;
        this.createdChannels = createdChannels;
        this.heartbeatThreads = heartbeatThreads;

        this.isHeartbeatBeingRead = false;

        try {
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.scanner = new Scanner(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
                this.userInput = scanner.nextLine();

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
     * Method to get the user input
     * 
     * @param label the label to display to the user
     * @return the user input
     */
    private String getUserInput(String label) {
        boolean wasLabelDisplayed = false;

        do {
            if (!wasLabelDisplayed) {
                this.out.println(label);
            }

            userInput = scanner.nextLine();

            if (userInput.contains("userInput:")) {
                userInput = this.removeUserInputPrefix(userInput);
            } else {
                userInput = "";
                wasLabelDisplayed = true;
            }
        } while (userInput.isEmpty());

        return userInput;
    }

    private boolean checkIfUserKnowsSendProtocol(User loggedInUser, User recipient) {
        if (loggedInUser instanceof Private && recipient instanceof Private) {
            userInput = getUserInput("[Capuchin, this is Tamarin. Solicitation from Capuchin to Tamarin.]");

            if (!userInput.equals("Tamarin, this is Capuchin. Affirmative.")) {
                return false;
            }

            this.out.println("[Details and options, over.]");

        } else if (loggedInUser instanceof Private && recipient instanceof Sergeant) {
            userInput = getUserInput("[Capuchin calling Clownfish. Solicitation from Capuchin to Clownfish.]");

            if (!userInput.equals("Clownfish responding to Capuchin. Confirmed.")) {
                return false;
            }

            this.out.println("[Details and options, over.]");

        } else if (loggedInUser instanceof Private && recipient instanceof General) {
            userInput = getUserInput("[Capuchin reaching out to Eagle. Solicitation from Capuchin to Eagle.]");

            if (!userInput.equals("Eagle here, Capuchin. Affirmative.")) {
                return false;
            }

            this.out.println("[Details and options, over.]");

        } else if (loggedInUser instanceof Sergeant && recipient instanceof Private) {
            userInput = getUserInput("[Clownfish, this is Capuchin. Solicitation from Clownfish to Capuchin.]");

            if (!userInput.equals("Capuchin, this is Clownfish. Positive.")) {
                return false;
            }

            userInput = getUserInput("[Alias]");

            if (!userInput.equals("Yellow")) {
                return false;
            }

            this.out.println("[Details and options, over.]");

        } else if (loggedInUser instanceof Sergeant && recipient instanceof Sergeant) {
            userInput = getUserInput("[Clownfish to Tamarin. Solicitation from Clownfish to Tamarin.]");

            if (!userInput.equals("Tamarin to Clownfish. Positive.")) {
                return false;
            }

            userInput = getUserInput("[Alias]");

            if (!userInput.equals("Chain")) {
                return false;
            }

            this.out.println("[Details and options, over.]");

        } else if (loggedInUser instanceof Sergeant && recipient instanceof General) {
            userInput = getUserInput("[Clownfish to Eagle. Solicitation from Clownfish to Eagle.]");

            if (!userInput.equals("Eagle, this is Clownfish. Positive.")) {
                return false;
            }

            userInput = getUserInput("[Alias]");

            if (!userInput.equals("Tire")) {
                return false;
            }

            this.out.println("[Details and options, over.]");

        } else if (loggedInUser instanceof General && recipient instanceof Private) {
            userInput = getUserInput("[Eagle to Capuchin. Solicitation from Eagle to Capuchin.]");

            if (!userInput.equals("Capuchin, this is Eagle. Acknowledge.")) {
                return false;
            }

            userInput = getUserInput("[Alias]");

            if (!userInput.equals("Magenta")) {
                return false;
            }

            userInput = getUserInput("[Key]");

            if (!userInput.equals("Code 130")) {
                return false;
            }

            this.out.println("[Details and options, over.]");

        } else if (loggedInUser instanceof General && recipient instanceof Sergeant) {
            userInput = getUserInput("[Eagle reaching out to Clownfish. Solicitation from Eagle to Clownfish.]");

            if (!userInput.equals("Clownfish, this is Eagle. Acknowledge.")) {
                return false;
            }

            userInput = getUserInput("[Alias]");

            if (!userInput.equals("Pink")) {
                return false;
            }

            userInput = getUserInput("[Key]");

            if (!userInput.equals("Code 1054")) {
                return false;
            }

            this.out.println("[Details and options, over.]");

        } else if (loggedInUser instanceof General && recipient instanceof General) {
            userInput = getUserInput("[Eagle to Tamarin. Solicitation from Eagle to Tamarin.]");

            if (!userInput.equals("Tamarin, this is Eagle. Acknowledge.")) {
                return false;
            }

            userInput = getUserInput("[Alias]");

            if (!userInput.equals("Truck")) {
                return false;
            }

            userInput = getUserInput("[Key]");

            if (!userInput.equals("Code 34")) {
                return false;
            }

            this.out.println("[Details and options, over.]");

        }

        return true;
    }

    /**
     * Method to check if the user knows the protocol to approve a message
     * 
     * @param loggedInUser    Logged in user
     * @param senderOfMessage Recipient of the message
     * @return true if the user knows the protocol, false otherwise
     */
    private boolean checkIfUserKnowsApproveProtocol(User loggedInUser, User senderOfMessage) {
        if (loggedInUser instanceof Private && senderOfMessage instanceof Private) {
            userInput = getUserInput("[Tamarin to Capuchin. Approval of Capuchin-to-Tamarin solicitation.]");

            if (!userInput.equals("Capuchin, this is Tamarin. Affirmative. ")) {
                return false;
            }

            this.out.println("[Details and options, over.]");

        } else if (loggedInUser instanceof Private && senderOfMessage instanceof Sergeant) {
            userInput = getUserInput("[Tamarin to Clownfish. Approval of Tamarin-to-Clownfish solicitation.]");

            if (!userInput.equals("Clownfish, this is Tamarin. Affirmative.")) {
                return false;
            }

            this.out.println("[Details and options, over.]");

        } else if (loggedInUser instanceof Private && senderOfMessage instanceof General) {
            userInput = getUserInput("[Tamarin to Eagle. Approval of Tamarin-to-Eagle solicitation. ]");

            if (!userInput.equals("Eagle, this is Tamarin. Affirmative.")) {
                return false;
            }

            this.out.println("[Details and options, over.]");

        } else if (loggedInUser instanceof Sergeant && senderOfMessage instanceof Private) {
            userInput = getUserInput("[Clownfish to Tamarin. Approval of Clownfish-to-Tamarin solicitation.]");

            if (!userInput.equals("Tamarin, this is Clownfish. Affirmative.")) {
                return false;
            }

            userInput = getUserInput("[Alias]");

            if (!userInput.equals("Bicycle")) {
                return false;
            }

            this.out.println("[Details and options, over.]");

        } else if (loggedInUser instanceof Sergeant && senderOfMessage instanceof Sergeant) {
            userInput = getUserInput("[Clownfish to Capuchin. Approval of Clownfish-to-Capuchin solicitation.]");

            if (!userInput.equals("Capuchin, this is Clownfish. Affirmative.")) {
                return false;
            }

            userInput = getUserInput("[Alias]");

            if (!userInput.equals("Ice")) {
                return false;
            }

            this.out.println("[Details and options, over.]");

        } else if (loggedInUser instanceof Sergeant && senderOfMessage instanceof General) {
            userInput = getUserInput("[Clownfish to Eagle. Approval of Clownfish-to-Eagle solicitation.]");

            if (!userInput.equals("Eagle, this is Clownfish. Affirmative.")) {
                return false;
            }

            userInput = getUserInput("[Alias]");

            if (!userInput.equals("Brick")) {
                return false;
            }

            this.out.println("[Details and options, over.]");

        } else if (loggedInUser instanceof General && senderOfMessage instanceof Private) {
            userInput = getUserInput("[Eagle to Tamarin. Approval of Eagle-to-Tamarin solicitation.]");

            if (!userInput.equals("Tamarin, this is Eagle. Affirmative.")) {
                return false;
            }

            userInput = getUserInput("[Alias]");

            if (!userInput.equals("Rock")) {
                return false;
            }

            userInput = getUserInput("[Key]");

            if (!userInput.equals("Code 754")) {
                return false;
            }

            this.out.println("[Details and options, over.]");

        } else if (loggedInUser instanceof General && senderOfMessage instanceof Sergeant) {
            userInput = getUserInput("[Eagle to Clownfish. Approval of Eagle-to-Clownfish solicitation.]");

            if (!userInput.equals("Clownfish, this is Eagle. Affirmative.")) {
                return false;
            }

            userInput = getUserInput("[Alias]");

            if (!userInput.equals("Hut")) {
                return false;
            }

            userInput = getUserInput("[Key]");

            if (!userInput.equals("Code 93")) {
                return false;
            }

            this.out.println("[Details and options, over.]");

        } else if (loggedInUser instanceof General && senderOfMessage instanceof General) {
            userInput = getUserInput("[Eagle to Capuchin. Approval of Eagle-to-Capuchin solicitation. ]");

            if (!userInput.equals("Capuchin, this is Eagle. Affirmative.")) {
                return false;
            }

            userInput = getUserInput("[Alias]");

            if (!userInput.equals("Bank")) {
                return false;
            }

            userInput = getUserInput("[Key]");

            if (!userInput.equals("Code 568")) {
                return false;
            }

            this.out.println("[Details and options, over.]");
        }

        return true;
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
     * Find a user by username in the list of registered users
     * 
     * @param username the username of the user to find
     * @return the User object if found, null otherwise
     */
    private User findUserByUsername(String username) {
        for (User user : registeredUsers) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }

        return null;
    }

    /**
     * Find a channel by name in the list of created channels
     * 
     * @param channelName the name of the channel to find
     * @return the Channel object if found, null otherwise
     */
    private Channel findChannelByName(String channelName) {
        for (Channel channel : this.createdChannels) {
            if (channel.getName().equals(channelName)) {
                return channel;
            }
        }

        return null;
    }

    /**
     * Method to listen to user input while in a channel
     * If the user sends the message "userInput:0", stop listening and display the
     * previous menu
     * 
     * @param listening boolean to control the loop
     */
    private void listeningToUserInputWhileInChannel(boolean listening) {
        try {
            while (listening) {
                this.userInput = scanner.nextLine();

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
        this.out.println("[List of targets to choose from:]");
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
     * Method to send a message to a user or a channel
     */
    private ReplyObject sendMessage(User loggedInUser, boolean isItPersonalMessage, String channelName) {
        ReplyObject result;

        if (isItPersonalMessage) {
            // List of registered users minus the logged in user
            List<User> registeredUsersMinusLoggedInUser = new ArrayList<User>(this.registeredUsers);
            for (User user : registeredUsersMinusLoggedInUser) {
                if (user.getUsername().equals(loggedInUser.getUsername())) {
                    registeredUsersMinusLoggedInUser.remove(user);
                    break;
                }
            }

            result = selectTarget(registeredUsersMinusLoggedInUser);
        } else {
            result = new ReplyObject(true, channelName);
        }

        String recipientName = result.getMessage();

        if (!recipientName.isEmpty()) {
            String title = getUserInput("[Enter the title of the message:]");

            String content = getUserInput("[Enter the content of the message:]");

            if (isItPersonalMessage) {
                User recipient = findUserByName(recipientName);

                if (recipient != null) {

                    ///// PROTOCOLS START /////
                    boolean doesLoggedInUserKnowSendProtocol = checkIfUserKnowsSendProtocol(loggedInUser, recipient);
                    ///// PROTOCOLS END /////

                    if (!doesLoggedInUserKnowSendProtocol) {
                        return new ReplyObject(false, "[Message not sent. User does not know the protocol.]");
                    } else {

                        recipient.registerPersonalMessage(title, content, loggedInUser.getUsername());

                        new IncreaseNotificationParametersThread("numberSolicitations", true)
                                .start();

                        return new ReplyObject(true, "[Message sent successfully.]");
                    }
                } else {
                    return new ReplyObject(false, "[User not found.]");
                }
            } else {
                Channel channel = findChannelByName(recipientName);

                if (channel != null) {
                    loggedInUser.registerChannelMessage(title, content, channel.getName());

                    new IncreaseNotificationParametersThread("numberSolicitations", true)
                            .start();

                    return new ReplyObject(true, "[Message sent successfully.]");
                } else {
                    return new ReplyObject(false, "[Channel not found.]");
                }
            }
        }

        return new ReplyObject(false, "[Message not sent.]");
    }

    /**
     * Method to send a reply to a message
     */
    private ReplyObject sendReply(User loggedInUser, String channelName, Message message) {
        if (message != null) {

            String replyContent = getUserInput("[Enter the content of the reply:]");

            this.out.println("[Do you want to ping the sender of the message?]");

            int pingSenderInt = getMenuOption(List.of(0, 1, 2), List.of("Back", "Yes", "No"));

            boolean pingSender = pingSenderInt == 1 ? true : false;

            loggedInUser.sendReply(replyContent, message.getTitle(), message.getSender(), message.getChannel(),
                    pingSender);

            return new ReplyObject(true, "[Reply sent successfully.]");

        } else {
            return new ReplyObject(false, "[Reply not sent.]");
        }
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
                    loginForm(optionSelected);
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

        boolean isUsernameInvalid = false;
        boolean isPasswordInvalid = false;
        boolean isRankInvalid = false;

        String username;
        String password;
        String rank;

        do {
            username = getUserInput("[Enter your username:]");

            if (AuthHandler.validateClientUsername(username)) {
                isUsernameInvalid = false;
            } else {
                this.out.println("[Invalid username.]\n[Username must have between 3 to 20 characters.]");
                isUsernameInvalid = true;
            }
        } while (isUsernameInvalid);

        do {
            password = getUserInput("[Enter your password:]");

            if (AuthHandler.validateClientPassword(password)) {
                isPasswordInvalid = false;
            } else {
                this.out.println("[Invalid password.]\n[Password must have between 6 to 12 characters.]");
                isPasswordInvalid = true;
            }
        } while (isPasswordInvalid);

        do {
            rank = getUserInput("[Enter your rank:]");

            if (AuthHandler.validateClientRank(rank)) {
                isRankInvalid = false;
            } else {
                this.out.println("[Invalid rank.]\n[Rank must be either General or Soldier.]");
                isRankInvalid = true;
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
    private void loginForm(int optionSelected) {
        this.clearTerminal();

        String username = getUserInput("[Enter your username:]");

        String password = getUserInput("[Enter your password:]");

        ReplyObject isLoggedIn = AuthHandler.verifyLogin(username, password);

        if (!isLoggedIn.getWasOperationSuccessful()) {
            this.out.println("[Credentials are incorrect.]");
        } else {
            for (User user : this.loggedInUsers) {
                if (user.getUsername().equals(isLoggedIn.getUser().getUsername())) {
                    this.clearTerminal();

                    this.out.println("[User already logged in.]");
                    return;
                }
            }

            // User logged in successfully
            this.clearTerminal();
            // Create a thread to increment number of logged in users
            this.loggedInUsers.add(isLoggedIn.getUser());
            this.out.println("[User logged in successfully.]");
            new IncreaseNotificationParametersThread("numberConnections",
                    true)
                    .start();
            userMenu(isLoggedIn.getUser(), optionSelected);
        }
    }

    /**
     * Method to handle the menu for the user
     * 
     * @param username the username of the user
     */
    private void userMenu(User loggedInUser, int optionSelected) {
        if (!isHeartbeatBeingRead) {
            isHeartbeatBeingRead = true;
            HeartbeatReaderThread heartbeatReaderThread = new HeartbeatReaderThread(loggedInUser,
                    loggedInUsers);
            heartbeatReaderThread.start();

            this.heartbeatThreads.add(heartbeatReaderThread);
        }

        boolean listening = true;
        optionSelected = -1;

        List<Integer> options;
        List<String> messages;

        options = List.of(0, 1, 2, 3, 4);
        messages = List.of("Back", "Send Message", "Approve Messages", "See all messages",
                "View Channels");

        while (optionSelected != 0) {
            optionSelected = getMenuOption(options, messages);

            this.clearTerminal();

            if (optionSelected == 0) {
                if (isHeartbeatBeingRead) {
                    isHeartbeatBeingRead = false;

                    for (HeartbeatReaderThread heartbeatReaderThread : this.heartbeatThreads) {
                        if (heartbeatReaderThread.getLoggedInUser().getUsername()
                                .equals(loggedInUser.getUsername())) {
                            heartbeatReaderThread.interrupt();
                            this.heartbeatThreads.remove(heartbeatReaderThread);
                            break;
                        }
                    }
                }
                break;
            }

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
                // Começa a pedir o titulo e o conteudo da mensagem
                result = this.sendMessage(loggedInUser, true, "");

                this.clearTerminal();

                if (result.getWasOperationSuccessful()) {
                    this.out.println(result.getMessage());
                } else {
                    this.out.println(result.getMessage());
                }

                break;
            case 2:
                loggedInUser.receivePersonalMessages();

                result = selectTarget(loggedInUser.getMessages());
                messageTitle = result.getMessage();

                this.clearTerminal();

                if (!messageTitle.isEmpty()) {
                    Message message = loggedInUser.findMessageByTitle(messageTitle);
                    // I want to change the approved field to "Approved by <username>"

                    if (message != null) {
                        System.out.println("Message: " + message);

                        User senderOfTheMessage = findUserByUsername(message.getSender());

                        this.out.println("===============Message_Information===============");
                        this.out.println("Title: " + message.getTitle());
                        this.out.println("Content: " + message.getContent());
                        this.out.println("=================================================\n");

                        ///// PROTOCOLS START /////
                        boolean doesLoggedInUserKnowApproveProtocol = checkIfUserKnowsApproveProtocol(loggedInUser,
                                senderOfTheMessage);
                        ///// PROTOCOLS END /////

                        if (!doesLoggedInUserKnowApproveProtocol) {
                            this.out.println("[Message not approved. User does not know the protocol.]");
                        } else {
                            this.out.println("[Do you want to approve this message?]");

                            int selectedOption = getMenuOption(List.of(0, 1, 2), List.of("Back", "Yes", "No"));

                            if (selectedOption == 1) {
                                message.setApproved("Approved by " + loggedInUser.getUsername());
                                message.UpdateEntryInFile();
                                this.out.println("[Message approved successfully.]");
                                new IncreaseNotificationParametersThread("numberApprovals", true)
                                        .start();
                            } else if (selectedOption == 2) {
                                message.setApproved("Not approved by " + loggedInUser.getUsername());
                                message.UpdateEntryInFile();
                                this.out.println("[Message not approved.]");
                            }
                        }
                    }
                }

                break;
            case 3:
                loggedInUser.receivePersonalMessages();

                if (loggedInUser.getMessages().isEmpty()) {
                    this.clearTerminal();
                    this.out.println("[No messages to display.]");
                    break;
                }

                result = selectTarget(loggedInUser.getMessages());
                messageTitle = result.getMessage();

                this.clearTerminal();

                if (!messageTitle.isEmpty()) {
                    Message message = loggedInUser.findMessageByTitle(messageTitle);

                    if (message != null) {

                        this.out.println("===============Message_Information===============");
                        this.out.println("Title: " + message.getTitle());
                        this.out.println("Content: " + message.getContent());
                        this.out.println("Sender: " + message.getSender());
                        this.out.println("Recipient: " + message.getRecipient());
                        this.out.println("Approved: " + message.getApproved());

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String formattedDateTime = message.getTimestamp().format(formatter);

                        this.out.println("Messages sent at: " + formattedDateTime);
                        this.out.println("=================================================");
                    } else {
                        this.out.println("[Message not found...]");
                    }
                }
                break;
            case 4:
                optionSelected = -1;

                List<String> messages = new ArrayList<String>();
                List<Integer> options = new ArrayList<Integer>();

                if (loggedInUser instanceof General) {
                    messages = List.of("Back", "Join Approvals Notification Channel",
                            "Join Connections Notification Channel",
                            "Join Solicitations Notification Channel", "Join a user created channel",
                            "Create a channel", "Delete a channel");
                    options = List.of(0, 1, 2, 3, 4, 5, 6);
                } else {
                    messages = List.of("Back", "Join Approvals Notification Channel",
                            "Join Solicitations Notification Channel", "Join a user created channel");
                    options = List.of(0, 1, 2, 3);
                }

                while (optionSelected != 0) {
                    optionSelected = getMenuOption(options, messages);

                    this.clearTerminal();

                    communicationChannelsMenu(loggedInUser, optionSelected);
                }

                break;
            default:
                break;
        }
    }

    /**
     * Method to handle the communication channels menu
     * 
     * @param loggedInUser   The logged in user
     * @param optionSelected The option selected by the user
     */
    private void communicationChannelsMenu(User loggedInUser, int optionSelected) {

        ReplyObject result;
        String nameOfTheChannel;
        boolean doesChannelExist;

        switch (optionSelected) {
            case 1:
                // Join Approvals Notification Channel
                this.out.println("JOIN_CHANNEL:" + NotifierThreads.APPROVALS_MADE_CHANNELADDR);

                listeningToUserInputWhileInChannel(true);

                System.out.println("Returned from listening to user input");
                System.out.println(this.out.checkError());

                this.out.flush();
                this.out.println("Returned from listening to user input");
                break;
            case 2:
                // Join Connections Notification Channel
                if (loggedInUser instanceof General) {
                    this.out.println("JOIN_CHANNEL:" + NotifierThreads.CONNECTIONS_MADE_CHANNELADDR);

                    listeningToUserInputWhileInChannel(true);
                } else {
                    this.out.println("JOIN_CHANNEL:" + NotifierThreads.SOLICITATIONS_MADE_CHANNELADDR);

                    listeningToUserInputWhileInChannel(true);
                }

                break;
            case 3:
                // Join Solicitations Notification Channel
                if (loggedInUser instanceof General) {
                    this.out.println("JOIN_CHANNEL:" + NotifierThreads.SOLICITATIONS_MADE_CHANNELADDR);

                    listeningToUserInputWhileInChannel(true);
                } else {
                    result = selectTarget(this.createdChannels);

                    nameOfTheChannel = result.getMessage();

                    doesChannelExist = ChannelHandler.checkIfChannelExists(nameOfTheChannel);

                    if (doesChannelExist) {
                        optionSelected = -1;

                        List<String> messages = List.of("Back", "Send Message", "See all messages",
                                "Reply to a message",
                                "Leave the channel");
                        List<Integer> options = List.of(0, 1, 2, 3, 4);

                        while (optionSelected != 0) {
                            optionSelected = getMenuOption(options, messages);

                            this.clearTerminal();

                            clientJoinedCommunicationChannelMenu(nameOfTheChannel, loggedInUser, optionSelected);
                        }
                    } else {
                        this.out.println("[Channel not found.]");
                    }
                }

                break;
            case 4:
                // Join a user created channel
                result = selectTarget(this.createdChannels);

                nameOfTheChannel = result.getMessage();

                doesChannelExist = ChannelHandler.checkIfChannelExists(nameOfTheChannel);

                if (doesChannelExist) {
                    optionSelected = -1;

                    while (optionSelected != 0) {
                        List<String> messages = List.of("Leave the channel", "Send Message", "See all messages",
                                "Reply to a message");
                        List<Integer> options = List.of(0, 1, 2, 3);

                        optionSelected = getMenuOption(options, messages);
                        clientJoinedCommunicationChannelMenu(nameOfTheChannel, loggedInUser, optionSelected);
                    }
                } else {
                    this.out.println("[Channel not found.]");
                }

                break;
            case 5:
                if (loggedInUser instanceof General) {
                    // Create a channel
                    nameOfTheChannel = getUserInput("[Enter the name of the channel:]");

                    result = ChannelHandler.addChannel(nameOfTheChannel, loggedInUser.getUsername());

                    this.out.println(result.getMessage());

                    if (result.getWasOperationSuccessful()) {
                        this.createdChannels.add(result.getChannel());
                    }
                }

                break;
            case 6:
                if (loggedInUser instanceof General) {

                    // Delete a channel
                    result = selectTarget(this.createdChannels);

                    nameOfTheChannel = result.getMessage();

                    result = ChannelHandler.removeChannel(nameOfTheChannel);

                    if (result.getWasOperationSuccessful()) {
                        this.out.println("[Channel deleted successfully.]");
                        for (Channel channel : this.createdChannels) {
                            if (channel.getName().equals(nameOfTheChannel)) {
                                this.createdChannels.remove(channel);
                                break;
                            }
                        }
                    } else {
                        this.out.println("[Channel not found.]");
                    }
                }

                break;
            default:
                break;
        }

    }

    /**
     * Menu that shows the options a user sees upon joining a user created channel
     * 
     * @param nameOfTheChannel The name of the channel
     */
    private void clientJoinedCommunicationChannelMenu(String nameOfTheChannel, User loggedInUser, int optionSelected) {
        ReplyObject result;

        switch (optionSelected) {
            case 1:
                // Send Message
                result = this.sendMessage(loggedInUser, false, nameOfTheChannel);

                this.clearTerminal();

                if (result.getWasOperationSuccessful()) {
                    this.out.println(result.getMessage());
                } else {
                    this.out.println(result.getMessage());
                }

                break;
            case 2:
                // See all messages
                loggedInUser.receiveChannelMessages(nameOfTheChannel); // Loads messages into array

                if (loggedInUser.getMessages().isEmpty()) {
                    this.clearTerminal();
                    this.out.println("[No messages to display.]");
                    break;
                }

                result = selectTarget(loggedInUser.getMessages()); // Lists messages

                String messageTitle = result.getMessage();

                this.clearTerminal();

                if (!messageTitle.isEmpty()) {
                    Message message = loggedInUser.findMessageByTitle(messageTitle);

                    if (message != null) {
                        List<Reply> replies = Reply.readRepliesFromFileForMessage(message.getTitle());

                        this.out.println("===============Message_Information===============");
                        this.out.println("Channel: " + message.getChannel());
                        this.out.println("Title: " + message.getTitle());
                        this.out.println("Content: " + message.getContent());
                        this.out.println("Sender: " + message.getSender());

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String formattedDateTime = message.getTimestamp().format(formatter);

                        this.out.println("Messages sent at: " + formattedDateTime);
                        this.out.println("=================================================");

                        this.out.println("=====================Replies=====================");
                        for (Reply reply : replies) {
                            this.out.println("User: " + reply.getOriginalMessageSender());

                            if (reply.getPingOriginalMessageSender()) {
                                this.out.println("Content: @" + reply.getSender() + " " + reply.getContent());
                            } else {
                                this.out.println("Content: " + reply.getContent());
                            }

                            String formattedDateTimeReply = reply.getDate().format(formatter);

                            this.out.println("Replied at:" + formattedDateTimeReply);
                            this.out.println("=================================================");
                        }

                    } else {
                        this.out.println("[Message not found...]");
                    }
                } else {
                    this.out.println("[Message not found...]");
                }

                break;
            case 3:
                // Reply to a message
                loggedInUser.receiveChannelMessages(nameOfTheChannel); // Loads messages into array

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

                    if (message != null) {
                        this.out.println("===============Message_Information===============");
                        this.out.println("Title: " + message.getTitle());
                        this.out.println("Content: " + message.getContent());
                        this.out.println("=================================================\n");

                        result = this.sendReply(loggedInUser, nameOfTheChannel, message);
                    }
                }
                break;
            case 4:
                // Leave the channel
                this.out.println("LEAVE_CHANNEL:" + nameOfTheChannel);
                break;
        }

    }

    /**
     * Method to run the thread
     */
    public void run() {
        try {
            authMenu();

        } catch (Exception e) {
            for (HeartbeatReaderThread heartbeatReaderThread : this.heartbeatThreads) {
                for (User user : this.loggedInUsers) {
                    if (heartbeatReaderThread.getLoggedInUser().getUsername().equals(user.getUsername())) {
                        heartbeatReaderThread.interrupt();
                        this.heartbeatThreads.remove(heartbeatReaderThread);
                        this.loggedInUsers.remove(user);
                        break;
                    }
                }
            }
        } finally {
            try {
                clientSocket.close();
                scanner.close();

                // Create a thread to decrement number of logged in users
            } catch (IOException e) {
            }
        }
    }
}
