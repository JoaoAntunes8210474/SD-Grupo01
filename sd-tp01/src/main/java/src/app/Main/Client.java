package src.app.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            // MulticastSocket socket = new MulticastSocket(12322);
            // InetAddress address = InetAddress.getByName("230.0.0.1");
            // socket.joinGroup(address);

            Socket client = new Socket("localhost", 3000);

            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            // Create a thread to read from the multicast group
            Thread readThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Read from the multicast group
                        /**
                         * // and print the message to the terminal
                         * byte[] buffer = new byte[1024];
                         * DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                         * socket.receive(packet);
                         * 
                         * String message = new String(packet.getData(), packet.getOffset(),
                         * packet.getLength());
                         * System.out.println(message);
                         */
                        String serverMessage;
                        while ((serverMessage = in.readLine()) != null) {
                            System.out.println(serverMessage);
                        }

                        client.close();
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            // Create a thread to write to the multicast group
            // so that the server can know when a client wants to leave the group

            Thread writeThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Read from the terminal
                    // and send the message to the multicast group
                    Scanner scanner = new Scanner(System.in);

                    String userInput;

                    try {
                        while ((userInput = scanner.nextLine()) != null) {
                            /**
                             * String message = scanner.nextLine();
                             * byte[] buffer = message.getBytes();
                             * DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address,
                             * 12322);
                             * socket.send(packet);
                             */
                            out.println(userInput);
                        }

                        scanner.close();
                        client.close();
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            // Start the read thread
            readThread.start();

            // Start the write thread
            writeThread.start();
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                    + "the connection to: localhost.");
            System.exit(1);
        }
    }
}
