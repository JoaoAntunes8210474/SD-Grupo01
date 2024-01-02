package src.app.Classes.Threads;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import src.app.Interfaces.INotifierThreads;

/**
 * NotifierThreads
 */
public class NotifierThreads extends Thread implements INotifierThreads {
    protected static final String SOLICITATIONS_MADE_CHANNELADDR = "230.0.0.1";
    protected static final String APPROVALS_MADE_CHANNELADDR = "230.0.0.2";
    protected static final String CONNECTIONS_MADE_CHANNELADDR = "230.0.0.3";

    private DatagramSocket socket;
    private BufferedReader in;
    private volatile boolean running = true;
    private Map<String, InetAddress> channelGroups = new HashMap<>();

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
            addChannel("SolicitationsMade", SOLICITATIONS_MADE_CHANNELADDR);
            addChannel("ApprovalsMade", APPROVALS_MADE_CHANNELADDR);
            addChannel("ConnectionsMade", CONNECTIONS_MADE_CHANNELADDR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a channel to the channelGroups map
     * @param channelName Name of the channel
     * @param channelAddress Address of the channel
     */
    private void addChannel(String channelName, String channelAddress) {
        try {
            InetAddress group = InetAddress.getByName(channelAddress);
            channelGroups.put(channelName, group);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops the thread from running
     */
    private void stopRunning() {
        this.running = false;
        this.interrupt(); // If the thread is sleeping, wake it up so that it can stop running
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

            InetAddress group = channelGroups.get("SolicitationsMade");
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, 12321);
            socket.send(packet);
        } catch (Exception e) {
            System.out.println("Error notifying all solicitations made");
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
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject obj = (JSONObject) jsonParser.parse(this.in);
            int numberApprovals = Integer.parseInt(obj.get("numberApprovals").toString());

            String message = "Number of approvals: " + numberApprovals;
            byte[] buffer = message.getBytes();

            InetAddress group = channelGroups.get("ApprovalsMade");
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, 12321);
            socket.send(packet);
        } catch (Exception e) {
            System.out.println("Error notifying all approvals made");
        }
    }

    /**
     * Notifies all the connections made to the server
     * to all the generals who tune in to the channel
     */
    public void notifyAllConnectionsMadeOnlyToGenerals() {
        // Read number of connections
        // and send a message to the channel
        // with the number of connections
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject obj = (JSONObject) jsonParser.parse(this.in);
            int numberConnections = Integer.parseInt(obj.get("numberConnectedUsers").toString());

            String message = "Number of connections: " + numberConnections;
            byte[] buffer = message.getBytes();

            InetAddress group = channelGroups.get("ConnectionsMade");
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, 12321);
            socket.send(packet);
        } catch (Exception e) {
            System.out.println("Error notifying all connections made");
        }
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
                this.stopRunning();
                System.out.println("Notifier thread interrupted");
            } catch (Exception e) {
                this.stopRunning();
                System.out.println("Error notifying all stats");
            }
        }
    }
}