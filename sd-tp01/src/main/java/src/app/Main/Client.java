package src.app.Main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            MulticastSocket socket = new MulticastSocket(12322);
            InetAddress address = InetAddress.getByName("230.0.0.1");
            socket.joinGroup(address);

            // Create a thread to read from the multicast group
            Thread readThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Read from the multicast group
                        // and print the message to the terminal
                        byte[] buffer = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);

                        String message = new String(packet.getData(), packet.getOffset(), packet.getLength());
                        System.out.println(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            // Start the read thread
            readThread.start();

            // Create a thread to write to the multicast group
            // so that the server can know when a client wants to leave the group
            
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Read from the terminal
                    // and send the message to the multicast group
                    Scanner scanner = new Scanner(System.in);
                    while (true) {
                        String message = scanner.nextLine();
                        byte[] buffer = message.getBytes();
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 12322);
                        socket.send(packet);
                    }
                }
            }
        } catch (IOException e) {}
    }
}
