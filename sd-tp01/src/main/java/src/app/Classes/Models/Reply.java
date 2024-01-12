package src.app.Classes.Models;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import src.app.Classes.Threads.MessageThread;
import src.app.Classes.Threads.ReplyThread;

public class Reply {
    // Attributes
    private String content;
    private String sender;
    private String originalMessageTitle;
    private String recipient;
    private String nameOfTheChannel;
    private boolean pingRecipient;

    // Constructor
    public Reply(String content, String sender, String originalMessageTitle, String recipient,
            String nameOfTheChannel, boolean pingRecipient) {
        this.content = content;
        this.sender = sender;
        this.originalMessageTitle = originalMessageTitle;
        this.recipient = recipient;
        this.nameOfTheChannel = nameOfTheChannel;
        this.pingRecipient = pingRecipient;
    }

    // Getters and Setters Methods

    /**
     * Get the content of the reply
     * 
     * @return the content of the reply
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Get the sender of the reply
     * 
     * @return the sender of the reply
     */
    public String getSender() {
        return this.sender;
    }

    /**
     * Get the original message title of the reply
     * 
     * @return the original message title of the reply
     */
    public String getOriginalMessageTitle() {
        return this.originalMessageTitle;
    }

    /**
     * Get the recipient of the reply
     * 
     * @return the recipient of the reply
     */
    public String getRecipient() {
        return this.recipient;
    }

    /**
     * Get the name of the channel of the reply
     * 
     * @return the name of the channel of the reply
     */
    public String getNameOfTheChannel() {
        return this.nameOfTheChannel;
    }

    /**
     * Get the ping recipient of the reply
     * 
     * @return the ping recipient of the reply
     */
    public boolean getPingRecipient() {
        return this.pingRecipient;
    }

    /**
     * Set the content of the reply
     * 
     * @param content the content of the reply
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Set the sender of the reply
     * 
     * @param sender the sender of the reply
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Set the original message title of the reply
     * 
     * @param originalMessageTitle the original message title of the reply
     */
    public void setOriginalMessageTitle(String originalMessageTitle) {
        this.originalMessageTitle = originalMessageTitle;
    }

    /**
     * Set the recipient of the reply
     * 
     * @param recipient the recipient of the reply
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    /**
     * Set the name of the channel of the reply
     * 
     * @param nameOfTheChannel the name of the channel of the reply
     */
    public void setNameOfTheChannel(String nameOfTheChannel) {
        this.nameOfTheChannel = nameOfTheChannel;
    }

    /**
     * Set the ping recipient of the reply
     * 
     * @param pingRecipient the ping recipient of the reply
     */
    public void setPingRecipient(boolean pingRecipient) {
        this.pingRecipient = pingRecipient;
    }

    // Methods

    public static List<Reply> readRepliesFromFileForMessage(String title) {
        try {
            Path path = Path.of(ReplyThread.FILE_PATH);
            File file = new File(path.toString());

            long fileSize = Files.size(path);

            // Load messages from the file
            if (fileSize > 0) {
                // Read all messages from the file
                FileReader fileReader = new FileReader(file);
                JSONParser jsonParser = new JSONParser();

                JSONObject jsonObject = (JSONObject) jsonParser.parse(fileReader);
                JSONArray jsonReplies = (JSONArray) jsonObject.get("messages");

                List<Reply> userReplies = new ArrayList<>();

                for (Object reply : jsonReplies) {
                    JSONObject jsonReply = (JSONObject) reply;

                    String content = jsonReply.get("content").toString();
                    String sender = jsonReply.get("sender").toString();
                    String originalMessageTitle = jsonReply.get("originalMessageTitle").toString();
                    String recipient = jsonReply.get("recipient").toString();
                    String nameOfTheChannel = jsonReply.get("nameOfTheChannel").toString();
                    boolean pingRecipient = Boolean.parseBoolean(jsonReply.get("pingRecipient").toString());

                    Reply newReply = new Reply(content, sender, originalMessageTitle, recipient, nameOfTheChannel,
                            pingRecipient);
                    

                    if (newReply.getNameOfTheChannel().equals(nameOfTheChannel)) {
                        userReplies.add(newReply);
                    }
                }

                fileReader.close();

                return userReplies;
            }

            return new ArrayList<>();
        } catch (Exception e) {

        }

        return new ArrayList<>();
    }
}
