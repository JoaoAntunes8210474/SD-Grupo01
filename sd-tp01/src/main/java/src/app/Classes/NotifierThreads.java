package src.app.Classes;

import java.net.MulticastSocket;

import src.app.Interfaces.INotifierThreads;

/**
 * NotifierThreads
 */
public class NotifierThreads implements INotifierThreads {

    MulticastSocket socket;

    public NotifierThreads(MulticastSocket socket) {
        this.socket = socket;
    }

    /**
     * Notifies all the solicitations made to the server
     * to anyone who tunes in to the channel
     */
    public void notifyAllSolicitationsMade() {

    }

    /**
     * Notifies all the approvals made to the server
     * to anyone who tunes in to the channel
     */
    public void notifyAllApprovalsMade() {

    }

    /**
     * Notifies all the connections made to the server
     * to all the generals who tune in to the channel
     */
    public void notifyAllConnectionsMadeOnlyToGenerals() {
        
    }
}