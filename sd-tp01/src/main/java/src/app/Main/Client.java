package src.app.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
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
            InetSocketAddress groupAddress = new InetSocketAddress(channelName, 12322);
            multicastSocket.joinGroup(groupAddress, null);
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
            InetSocketAddress groupAddress = new InetSocketAddress(channelName, 12322);
            multicastSocket.leaveGroup(groupAddress, null);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            final Socket client = new Socket("localhost", 3000);

            final MulticastSocket multicastSocket = new MulticastSocket(12322);

            final BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            final PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            List<Thread> threads = new ArrayList<Thread>();

            // Create a thread to read from the multicast group
            Thread readThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String serverMessage;
                        while (keepRunning) {
                            if (!joinedNotificationGroup.isEmpty()) {
                                // Read from the multicast group
                                // and print the message to the terminal
                                byte[] buffer = new byte[1024];
                                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                                multicastSocket.receive(packet);

                                String message = new String(packet.getData(), packet.getOffset(),
                                        packet.getLength());

                                // Give serverMessage the value of message
                                serverMessage = message;
                            }

                            serverMessage = in.readLine();

                            System.out.println(serverMessage);

                            if (serverMessage.startsWith("JOIN_CHANNEL:")) {
                                // Store what comes after the JOIN_CHANNEL: prefix in a variable
                                String channelName = serverMessage.substring("JOIN_CHANNEL:".length());

                                // Inform the user that he is joining the channel
                                System.out.println("You are now joining channel " + channelName);

                                // Join a channel group for the given channel name
                                joinChannel(channelName, multicastSocket);

                                // Store the channel name in a variable
                                joinedNotificationGroup = channelName;

                                return;
                            } else if (serverMessage.equals("[Quitting...]")) {
                                // User selected option 0 in the first menu
                                keepRunning = false;
                                client.close();
                                multicastSocket.close();
                                in.close();
                                System.exit(0);
                            }
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
            // Create a thread to send heartbeats to the server
            Thread heartbeatThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        while (keepRunning) {
                            Thread.sleep(1000);
                            out.println("heartbeat");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            threads.add(readThread);
            threads.add(writeThread);
            threads.add(heartbeatThread);

            // Start the read thread
            readThread.start();

            // Start the write thread
            writeThread.start();

            // Start the heartbeat thread
            heartbeatThread.start();
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                    + "the connection to: localhost.");
            System.exit(1);
        }
    }
}
