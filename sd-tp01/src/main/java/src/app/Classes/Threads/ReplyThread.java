package src.app.Classes.Threads;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import src.app.Classes.Models.Reply;

public class ReplyThread extends Thread {
    public static final String FILE_PATH = "sd-tp01/src/main/java/src/app/Data/Replies.json";

    private Reply reply;

    public ReplyThread(Reply reply) {
        super("[ReplyThread]");

        this.reply = reply;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        try {
            Path path = Path.of(FILE_PATH);
            File file = new File(path.toString());

            long fileSize = Files.size(path);

            JSONObject jsonObject;
            JSONArray jsonMessages;

            // Use try-with-resources to automatically close the FileReader
            FileReader fileReader = new FileReader(file);
            JSONParser jsonParser = new JSONParser();

            // Load messages from the file
            if (fileSize > 0) {
                // Create first reply
                jsonObject = new JSONObject();
                jsonMessages = new JSONArray();
            } else {
                jsonObject = (JSONObject) jsonParser.parse(fileReader);
                jsonMessages = (JSONArray) jsonObject.get("replies");
            }

            JSONObject jsonReply = new JSONObject();
            
            jsonReply.put("content", this.reply.getContent());
            jsonReply.put("sender", this.reply.getSender());
            jsonReply.put("originalMessageTitle", this.reply.getOriginalMessageTitle());
            jsonReply.put("recipient", this.reply.getRecipient());
            jsonReply.put("nameOfTheChannel", this.reply.getNameOfTheChannel());
            jsonReply.put("pingRecipient", this.reply.getPingRecipient());

            jsonMessages.add(jsonReply);

            jsonObject.put("replies", jsonMessages);

            // Write the new message to the file
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            // Beautify the JSON before writing it to the file
            // by adding newlines and indentation
            String prettyJson = gson.toJson(jsonObject);

            // Append the JSON message to the file
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(prettyJson);

            fileReader.close();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
