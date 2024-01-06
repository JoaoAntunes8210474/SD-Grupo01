package src.app.Classes.Threads;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import src.app.Classes.Models.Message;

public class MessageThread extends Thread {
    private Message message;

    // File path for storing messages
    public static final String MESSAGE_FILE_PATH = "sd-tp01/src/main/java/src/app/Data/Messages.json";

    public MessageThread(Message message) {
        this.message = message;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        // Synchronize access to the file to ensure thread safety
        synchronized (MessageThread.class) {
            try {
                Path path = Path.of(MESSAGE_FILE_PATH);
                File file = new File(path.toString());

                // Check if the file is empty
                long fileSize = Files.size(path);

                JSONObject jsonObject;
                JSONArray jsonMessages;

                // Use try-with-resources to automatically close the FileReader
                FileReader fileReader = new FileReader(file);
                JSONParser jsonParser = new JSONParser();

                // Convert the message to JSON
                if (fileSize == 0) {
                    jsonObject = new JSONObject();
                    jsonMessages = new JSONArray();
                } else {
                    jsonObject = (JSONObject) jsonParser.parse(fileReader);
                    jsonMessages = (JSONArray) jsonObject.get("messages");
                }

                JSONObject jsonMessage = new JSONObject();
                jsonMessage.put("sender", this.message.getSender());
                jsonMessage.put("recipient", this.message.getRecipient());
                jsonMessage.put("title", this.message.getTitle());
                jsonMessage.put("content", this.message.getContent());
                jsonMessage.put("timestamp", this.message.getTimestamp().toString());

                jsonMessages.add(jsonMessage);

                jsonObject.put("messages", jsonMessages);

                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                // Beautify the JSON before writing it to the file
                // by adding newlines and indentation
                String prettyJson = gson.toJson(jsonObject);

                // Append the JSON message to the file
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(prettyJson);

                fileReader.close();
                fileWriter.close();

            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
