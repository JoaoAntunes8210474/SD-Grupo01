package src.app.Classes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import src.app.Interfaces.INotifierThreads;

/**
 * NotifierThreads
 */
public class NotifierThreads extends Thread implements INotifierThreads {
    private DatagramSocket socket;
    private BufferedReader in;
    private volatile boolean running = true;

    public NotifierThreads(String name) {
        super(name);

        try {
            this.socket = new DatagramSocket(12322);

            this.in = new BufferedReader(new FileReader("sd-tp01/src/main/java/src/app/Data/Stats.json"));
            // Json format:
            // {
            // "numberConnectedUsers": 0,
            // "numberSolicitations": 0,
            // "numberApprovals": 0,
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
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject obj = (JSONObject) jsonParser.parse(this.in);
            int numberSolicitations = Integer.parseInt(obj.get("numberSolicitations").toString());

            String message = "Number of solicitations: " + numberSolicitations;
            byte[] buffer = message.getBytes();

            InetAddress address = InetAddress.getByName("localhost");
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 12321);
            socket.send(packet);
        } catch (Exception e) {

        }
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
        while (running) {
            try {
                notifyAllSolicitationsMade();
                notifyAllApprovalsMade();
                notifyAllConnectionsMadeOnlyToGenerals();

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                running = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopRunning() {
        this.running = false;
        this.interrupt(); // If the thread is sleeping, wake it up so that it can stop running
    }
}