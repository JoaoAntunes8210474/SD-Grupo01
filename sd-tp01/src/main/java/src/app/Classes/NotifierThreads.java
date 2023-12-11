package src.app.Classes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import src.app.Interfaces.INotifierThreads;

/**
 * NotifierThreads
 */
public class NotifierThreads extends Thread implements INotifierThreads {

    private DatagramSocket socket;
    private BufferedReader in;

    public NotifierThreads(String name) {
        super(name);

        try {
            this.socket = new DatagramSocket(12322);

            this.in = new BufferedReader(new FileReader("sd-tp01/src/main/java/src/app/Data/Stats.json"));
            // Json format:
            // {
            //   "numberConnectedUsers": 0,
            //   "numberSolicitations": 0,
            //   "numberApprovals": 0,  
            // }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Notifies all the solicitations made to the server
     * to anyone who tunes in to the channel
     */
    public void notifyAllSolicitationsMade() {
        // Read number of solicitations
        // and send a message to the channel
        // with the number of solicitations

    }

    /**
     * Notifies all the approvals made to the server
     * to anyone who tunes in to the channel
     */
    public void notifyAllApprovalsMade() {
        // Read number of approvals
        // and send a message to the channel
        // with the number of approvals
    }

    /**
     * Notifies all the connections made to the server
     * to all the generals who tune in to the channel
     */
    public void notifyAllConnectionsMadeOnlyToGenerals() {
        // Read number of connections
        // and send a message to the channel
        // with the number of connections
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] buffer = new byte[1024];

                InetAddress group = InetAddress.getByName("230.0.0.1");
                DatagramPacket messageToSend = new DatagramPacket(buffer, buffer.length, group, 12322);
                socket.send(messageToSend);
                String message = new String(messageToSend.getData(), 0, messageToSend.getLength());
                System.out.println(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}