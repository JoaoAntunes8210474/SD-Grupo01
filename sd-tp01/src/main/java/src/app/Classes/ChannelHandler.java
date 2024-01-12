package src.app.Classes;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import src.app.Classes.Models.Channel;
import src.app.Classes.Models.ReplyObject;

public class ChannelHandler {
    private static final String FILE_PATH = "sd-tp01/src/main/java/src/app/Data/Channels.json";

    /**
     * Function that checks if a channel exists in the channels file
     * 
     * @param channelName Name of the channel
     * @return True if the channel exists, false otherwise
     */
    public static boolean checkIfChannelExists(String channelName) {
        try {
            // Read the file
            Path path = Path.of(FILE_PATH);
            File file = new File(path.toString());

            long fileSize = Files.size(path);

            JSONObject jsonObject;
            JSONArray jsonChannels;

            FileReader fileReader = new FileReader(file);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonChannel = new JSONObject();

            // Convert the message to JSON
            if (fileSize != 0) {
                jsonObject = (JSONObject) jsonParser.parse(fileReader);
                jsonChannels = (JSONArray) jsonObject.get("channels");

                for (int i = 0; i < jsonChannels.size(); i++) {
                    jsonChannel = (JSONObject) jsonChannels.get(i);

                    if (jsonChannel.get("name").equals(channelName)) {
                        return true;
                    }
                }
            }

            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Function that adds a channel to the channels file
     * 
     * @param channelName Name of the channel
     */
    @SuppressWarnings("unchecked")
    public static ReplyObject addChannel(String channelName, String channelCreator) {
        if (checkIfChannelExists(channelName)) {
            return new ReplyObject(false, "[Channel already exists]");
        }

        try {
            // Read the file
            Path path = Path.of(FILE_PATH);
            File file = new File(path.toString());

            long fileSize = Files.size(path);

            JSONObject jsonObject;
            JSONArray jsonChannels;

            FileReader fileReader = new FileReader(file);
            JSONParser jsonParser = new JSONParser();

            // Convert the message to JSON
            if (fileSize == 0) {
                jsonObject = new JSONObject();
                jsonChannels = new JSONArray();
            } else {
                jsonObject = (JSONObject) jsonParser.parse(fileReader);
                jsonChannels = (JSONArray) jsonObject.get("channels");
            }
            
            JSONObject jsonChannel = new JSONObject();
            jsonChannel.put("name", channelName);
            jsonChannel.put("creator", channelCreator);

            jsonChannels.add(jsonChannel);

            jsonObject.put("channels", jsonChannels);

            // Write the new JSON to the file
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            String prettyJson = gson.toJson(jsonObject);

            // Write the new JSON to the file
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(prettyJson);

            fileWriter.close();
            fileReader.close();

            return new ReplyObject(true, "[Channel added]", new Channel(channelName, channelCreator));
        } catch (Exception e) {
            e.printStackTrace();

            return new ReplyObject(false, "[Error adding channel]");
        }
    }

    /**
     * Function that removes a channel from the channels file
     * 
     * @param channelName Name of the channel
     */
    @SuppressWarnings("unchecked")
    public static ReplyObject removeChannel(String channelName) {
        if (!checkIfChannelExists(channelName)) {
            return new ReplyObject(false, "Channel does not exist");
        }

        try {
            // Read the file
            Path path = Path.of(FILE_PATH);
            File file = new File(path.toString());

            long fileSize = Files.size(path);

            JSONObject jsonObject;
            JSONArray jsonChannels;

            FileReader fileReader = new FileReader(file);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonChannel = new JSONObject();

            // Convert the message to JSON
            if (fileSize != 0) {
                jsonObject = (JSONObject) jsonParser.parse(fileReader);
                jsonChannels = (JSONArray) jsonObject.get("channels");

                for (int i = 0; i < jsonChannels.size(); i++) {
                    jsonChannel = (JSONObject) jsonChannels.get(i);

                    if (jsonChannel.get("name").equals(channelName)) {
                        jsonChannels.remove(i);
                        break;
                    }
                }

                jsonObject.put("channels", jsonChannels);

                // Write the new JSON to the file
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(jsonObject.toJSONString());
                fileWriter.flush();
                fileWriter.close();

            }

            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();

            return new ReplyObject(false, "Error removing channel");
        }

        return new ReplyObject(true, "Channel removed");
    }

    /**
     * Function that gets all channels from the channels file
     * 
     * @return A list of all channels
     */
    public static List<Channel> getAllChannels() {
        List<Channel> channels = new ArrayList<>();

        try {
            // Read the file
            Path path = Path.of(FILE_PATH);
            File file = new File(path.toString());

            long fileSize = Files.size(path);

            JSONObject jsonObject;
            JSONArray jsonChannels;

            FileReader fileReader = new FileReader(file);
            JSONParser jsonParser = new JSONParser();

            // Convert the message to JSON
            if (fileSize != 0) {
                jsonObject = (JSONObject) jsonParser.parse(fileReader);
                jsonChannels = (JSONArray) jsonObject.get("channels");

                for (int i = 0; i < jsonChannels.size(); i++) {
                    JSONObject jsonChannel = (JSONObject) jsonChannels.get(i);

                    String channelName = jsonChannel.get("name").toString();
                    String channelCreator = jsonChannel.get("creator").toString();

                    Channel channel = new Channel(channelName, channelCreator);

                    channels.add(channel);
                }
            }

            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return channels;
    }
}
