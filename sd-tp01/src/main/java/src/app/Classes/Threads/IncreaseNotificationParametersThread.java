package src.app.Classes.Threads;

import src.app.Classes.Models.User;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class IncreaseNotificationParametersThread extends Thread {
    private static final String FILE_PATH = "sd-tp01/src/main/java/src/app/Data/Stats.json";

    // List of all logged in users
    private List<User> loggedInUsers;

    // Variable to check which item to increment
    private String itemToIncrement;

    // Variable to check if it is an increment or a decrement
    private boolean isItAnIncrement;

    public IncreaseNotificationParametersThread(List<User> loggedInUsers, String itemToIncrement,
            boolean isItAnIncrement) {
        super("[IncreaseNotificationParametersThread]");

        this.loggedInUsers = loggedInUsers;
        this.itemToIncrement = itemToIncrement;
        this.isItAnIncrement = isItAnIncrement;
    }

    /**
     * Change the number of solicitations made based on the boolean isItAnIncrement
     */
    @SuppressWarnings("unchecked")
    private void changeSolicitationsMade() {
        try {
            // Read the file
            Path path = Path.of(FILE_PATH);
            File file = new File(path.toString());

            long fileSize = Files.size(path);

            JSONObject jsonObject;
            JSONArray jsonStats;

            FileReader fileReader = new FileReader(file);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonStat = new JSONObject();

            // Convert the message to JSON
            if (fileSize == 0) {
                jsonObject = new JSONObject();
                jsonStats = new JSONArray();

                jsonStat.put("SolicitationsMade", 1);
                jsonStat.put("ApprovalsMade", 0);
                jsonStat.put("ConnectionsMade", 0);
            } else {
                jsonObject = (JSONObject) jsonParser.parse(fileReader);
                jsonStats = (JSONArray) jsonObject.get("stats");

                String numberOfSolicitationsMadeString = jsonStat.get(this.itemToIncrement).toString();

                if (numberOfSolicitationsMadeString == null) {
                    numberOfSolicitationsMadeString = "0";
                }

                int numberOfSolicitationsMade = Integer.parseInt(numberOfSolicitationsMadeString);

                if (this.isItAnIncrement) {
                    jsonStat.put(this.itemToIncrement, (numberOfSolicitationsMade + 1));
                } else {
                    jsonStat.put(this.itemToIncrement, (numberOfSolicitationsMade - 1));
                }

                jsonStat.put("ApprovalsMade", jsonStat.get("ApprovalsMade"));
                jsonStat.put("ConnectionsMade", jsonStat.get("ConnectionsMade"));
            }

            jsonStats.add(jsonStat);

            jsonObject.put("stats", jsonStats);

            // Beautify the JSON before writing it to the file
            // by adding newlines and indentation
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

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

    /**
     * Change the number of approvals made based on the boolean isItAnIncrement
     */
    @SuppressWarnings("unchecked")
    private void changeApprovalsMade() {
        try {
            // Read the file
            Path path = Path.of(FILE_PATH);
            File file = new File(path.toString());

            long fileSize = Files.size(path);

            JSONObject jsonObject;
            JSONArray jsonStats;

            FileReader fileReader = new FileReader(file);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonStat = new JSONObject();

            // Convert the message to JSON
            if (fileSize == 0) {
                jsonObject = new JSONObject();
                jsonStats = new JSONArray();

                jsonStat.put("SolicitationsMade", 0);
                jsonStat.put("ApprovalsMade", 1);
                jsonStat.put("ConnectionsMade", 0);
            } else {
                jsonObject = (JSONObject) jsonParser.parse(fileReader);
                jsonStats = (JSONArray) jsonObject.get("stats");

                jsonStat.put("SolicitationsMade", jsonStat.get("SolicitationsMade"));

                String numberOfApprovalsMadeString = jsonStat.get(this.itemToIncrement).toString();

                if (numberOfApprovalsMadeString == null) {
                    numberOfApprovalsMadeString = "0";
                }

                int numberOfApprovalsMade = Integer.parseInt(numberOfApprovalsMadeString);

                if (this.isItAnIncrement) {
                    jsonStat.put(this.itemToIncrement, (numberOfApprovalsMade + 1));
                } else {
                    jsonStat.put(this.itemToIncrement, (numberOfApprovalsMade - 1));
                }

                jsonStat.put("ConnectionsMade", jsonStat.get("ConnectionsMade"));
            }

            jsonStats.add(jsonStat);

            jsonObject.put("stats", jsonStats);

            // Beautify the JSON before writing it to the file
            // by adding newlines and indentation
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

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

    /**
     * Change the number of connections made based on the boolean isItAnIncrement
     */
    @SuppressWarnings("unchecked")
    private void changeConnectionsMade() {
        try {
            // Read the file
            Path path = Path.of(FILE_PATH);
            File file = new File(path.toString());

            long fileSize = Files.size(path);

            JSONObject jsonObject;
            JSONArray jsonStats;

            FileReader fileReader = new FileReader(file);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonStat = new JSONObject();

            // Convert the message to JSON
            if (fileSize == 0) {
                jsonObject = new JSONObject();
                jsonStats = new JSONArray();

                jsonStat.put("SolicitationsMade", 0);
                jsonStat.put("ApprovalsMade", 0);
                jsonStat.put("ConnectionsMade", 1);
            } else {
                jsonObject = (JSONObject) jsonParser.parse(fileReader);
                jsonStats = (JSONArray) jsonObject.get("stats");

                jsonStat.put("ApprovalsMade", jsonStat.get("ApprovalsMade"));
                jsonStat.put("SolicitationsMade", jsonStat.get("SolicitationsMade"));

                int numberOfConnectionsMade = this.loggedInUsers.size();

                if (this.isItAnIncrement) {
                    jsonStat.put(this.itemToIncrement, (numberOfConnectionsMade + 1));
                } else {
                    jsonStat.put(this.itemToIncrement, (numberOfConnectionsMade - 1));
                }
            }

            jsonStats.add(jsonStat);

            jsonObject.put("stats", jsonStats);

            // Beautify the JSON before writing it to the file
            // by adding newlines and indentation
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

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

    @Override
    public void run() {
        if (itemToIncrement.equals("SolicitationsMade")) {
            changeSolicitationsMade();
        } else if (itemToIncrement.equals("ApprovalsMade")) {
            changeApprovalsMade();
        } else if (itemToIncrement.equals("ConnectionsMade")) {
            changeConnectionsMade();
        } else {
            System.out.println("[Invalid item to increment]");
        }
    }
}
