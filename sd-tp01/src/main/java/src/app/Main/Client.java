package src.app.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {

    // Name of the last channel the user joined
    private static String joinedNotificationGroup = "";

    // Boolean that indicates if the threads should keep running or not
    private static volatile boolean keepRunning = true;

    /**
     * Function that joins a channel group for the given channel name
     * 
     * @param channelName Name of the channel
     */
    private static void joinChannel(String channelName, MulticastSocket multicastSocket) {
        try {
            InetAddress groupAddress = InetAddress.getByName(channelName);
            multicastSocket.joinGroup(groupAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function that leaves a channel group for the given channel name
     * 
     * @param channelName Name of the channel
     */
    private static void leaveChannel(String channelName, MulticastSocket multicastSocket) {
        try {
            InetAddress groupAddress = InetAddress.getByName(channelName);
            multicastSocket.leaveGroup(groupAddress);

            System.out.println("You left channel " + channelName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            final Socket client = new Socket("localhost", 3000);

            final BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            final MulticastSocket multicastSocket = new MulticastSocket(12323);

            final PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            List<Thread> threads = new ArrayList<Thread>();

            // Create a thread to read from the multicast group
            Thread readThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String serverMessage = "";
                        while (keepRunning) {
                            if (joinedNotificationGroup.isEmpty()) {
                                serverMessage = in.readLine();
                            }

                            if (!joinedNotificationGroup.isEmpty()) {
                                byte[] buffer = new byte[1024];
                                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                                multicastSocket.receive(packet);

                                String message = new String(packet.getData(), packet.getOffset(),
                                        packet.getLength());

                                // Give serverMessage the value of message
                                serverMessage = message;
                            }

                            if (serverMessage.startsWith("JOIN_CHANNEL:")) {
                                // Store what comes after the JOIN_CHANNEL: prefix in a variable
                                String channelName = serverMessage.substring("JOIN_CHANNEL:".length());

                                // Join a channel group for the given channel name
                                joinChannel(channelName, multicastSocket);

                                System.out.println("You joined channel " + channelName);

                                // Store the channel name in a variable
                                joinedNotificationGroup = channelName;
                            } else if (serverMessage.equals("[Quitting...]")) {
                                System.out.println("Quitting...");
                                // User selected option 0 in the first menu
                                keepRunning = false;
                                client.close();
                                multicastSocket.close();
                                in.close();
                                System.exit(0);
                            }

                            System.out.println(serverMessage);
                        }
                    } catch (IOException e) {
                        if (!keepRunning) {

                        } else {
                            e.printStackTrace();
                        }
                    }
                }
            });

            // Create a thread to write to the server
            Thread writeThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Read from the terminal
                    // and send the message to the multicast group
                    Scanner scanner = new Scanner(System.in);

                    String userInput;

                    try {
                        while (keepRunning) {
                            userInput = scanner.nextLine();

                            String messageToBeSent = "userInput:" + userInput;
                            out.println(messageToBeSent);

                            if (messageToBeSent.equals("userInput:0") && !joinedNotificationGroup.isEmpty()) {
                                // Leave the channel group
                                leaveChannel(joinedNotificationGroup, multicastSocket);

                                System.out.println("You left channel " + joinedNotificationGroup);

                                // Reset the joinedNotificationGroup variable
                                joinedNotificationGroup = "";
                            }
                        }

                        scanner.close();
                        client.close();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        scanner.close();
                        out.close();
                    }
                }
            });

            threads.add(readThread);
            threads.add(writeThread);

            // Start the read thread
            readThread.start();

            // Start the write thread
            writeThread.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Couldn't get I/O for "
                    + "the connection to: localhost.");
            System.exit(1);
        }
    }
}
