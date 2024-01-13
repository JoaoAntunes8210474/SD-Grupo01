package src.app.Classes.Threads;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class IncreaseNotificationParametersThread extends Thread {
    private static final String FILE_PATH = "sd-tp01/src/main/java/src/app/Data/Stats.json";

    // Variable to check which item to increment
    private String itemToIncrement;

    // Variable to check if it is an increment or a decrement
    private boolean isItAnIncrement;

    public IncreaseNotificationParametersThread(String itemToIncrement,
            boolean isItAnIncrement) {
        super("[IncreaseNotificationParametersThread]");

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

            FileReader fileReader = new FileReader(file);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonStat = new JSONObject();

            // Convert the message to JSON
            if (fileSize == 0) {
                jsonObject = new JSONObject();

                jsonStat.put("numberSolicitations", 1);
                jsonStat.put("numberApprovals", 0);
                jsonStat.put("numberConnections", 0);
            } else {
                jsonObject = (JSONObject) jsonParser.parse(fileReader);
                JSONObject fileStats = (JSONObject) jsonObject.get("stats");

                String numberOfSolicitationsMadeString = fileStats.get(this.itemToIncrement).toString();
                String numberOfApprovalsMadeString = fileStats.get("numberApprovals").toString();
                String numberOfConnectedUsersString = fileStats.get("numberConnections").toString();

                if (numberOfSolicitationsMadeString == null) {
                    numberOfSolicitationsMadeString = "0";
                }

                if (numberOfApprovalsMadeString == null) {
                    numberOfApprovalsMadeString = "0";
                }

                if (numberOfConnectedUsersString == null) {
                    numberOfConnectedUsersString = "0";
                }

                int numberOfSolicitationsMade = Integer.parseInt(numberOfSolicitationsMadeString);
                int numberOfApprovalsMade = Integer.parseInt(numberOfApprovalsMadeString);
                int numberOfConnectedUsers = Integer.parseInt(numberOfConnectedUsersString);

                if (this.isItAnIncrement) {
                    jsonStat.put(this.itemToIncrement, (numberOfSolicitationsMade + 1));
                } else {
                    jsonStat.put(this.itemToIncrement, (numberOfSolicitationsMade - 1));
                }

                jsonStat.put("numberApprovals", numberOfApprovalsMade);
                jsonStat.put("numberConnections", numberOfConnectedUsers);
            }

            jsonObject.put("stats", jsonStat);

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

            FileReader fileReader = new FileReader(file);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonStat = new JSONObject();

            // Convert the message to JSON
            if (fileSize == 0) {
                jsonObject = new JSONObject();

                jsonStat.put("numberSolicitations", 0);
                jsonStat.put("numberApprovals", 1);
                jsonStat.put("numberConnections", 0);
            } else {
                jsonObject = (JSONObject) jsonParser.parse(fileReader);
                JSONObject fileStats = (JSONObject) jsonObject.get("stats");

                String numberOfSolicitationsMadeString = fileStats.get("numberSolicitations").toString();
                String numberOfApprovalsMadeString = fileStats.get(this.itemToIncrement).toString();
                String numberOfConnectedUsersString = fileStats.get("numberConnections").toString();

                if (numberOfSolicitationsMadeString == null) {
                    numberOfSolicitationsMadeString = "0";
                }

                if (numberOfApprovalsMadeString == null) {
                    numberOfApprovalsMadeString = "0";
                }

                if (numberOfConnectedUsersString == null) {
                    numberOfConnectedUsersString = "0";
                }

                int numberOfSolicitationsMade = Integer.parseInt(numberOfSolicitationsMadeString);
                int numberOfApprovalsMade = Integer.parseInt(numberOfApprovalsMadeString);
                int numberOfConnectedUsers = Integer.parseInt(numberOfConnectedUsersString);

                if (this.isItAnIncrement) {
                    jsonStat.put(this.itemToIncrement, (numberOfApprovalsMade + 1));
                } else {
                    jsonStat.put(this.itemToIncrement, (numberOfApprovalsMade - 1));
                }

                jsonStat.put("numberSolicitations", numberOfSolicitationsMade);
                jsonStat.put("numberConnections", numberOfConnectedUsers);
            }

            jsonObject.put("stats", jsonStat);

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

            FileReader fileReader = new FileReader(file);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonStat = new JSONObject();

            // Convert the message to JSON
            if (fileSize == 0) {
                jsonObject = new JSONObject();

                jsonStat.put("numberSolicitations", 0);
                jsonStat.put("numberApprovals", 0);
                jsonStat.put("numberConnections", 1);
            } else {
                jsonObject = (JSONObject) jsonParser.parse(fileReader);
                JSONObject fileStats = (JSONObject) jsonObject.get("stats");

                Object numberOfSolicitationsMadeObj = fileStats.get("numberSolicitations");
                String numberOfSolicitationsMadeString = numberOfSolicitationsMadeObj != null
                        ? numberOfSolicitationsMadeObj.toString()
                        : null;

                Object numberOfApprovalsMadeObj = fileStats.get("numberApprovals");
                String numberOfApprovalsMadeString = numberOfApprovalsMadeObj != null
                        ? numberOfApprovalsMadeObj.toString()
                        : null;

                Object numberOfConnectedUsersObj = fileStats.get(this.itemToIncrement);
                String numberOfConnectedUsersString = numberOfConnectedUsersObj != null
                        ? numberOfConnectedUsersObj.toString()
                        : null;

                if (numberOfSolicitationsMadeString == null) {
                    numberOfSolicitationsMadeString = "0";
                }

                if (numberOfApprovalsMadeString == null) {
                    numberOfApprovalsMadeString = "0";
                }

                if (numberOfConnectedUsersString == null) {
                    numberOfConnectedUsersString = "0";
                }

                int numberOfSolicitationsMade = Integer.parseInt(numberOfSolicitationsMadeString);
                int numberOfApprovalsMade = Integer.parseInt(numberOfApprovalsMadeString);
                int numberOfConnectedUsers = Integer.parseInt(numberOfConnectedUsersString);

                if (this.isItAnIncrement) {
                    jsonStat.put(this.itemToIncrement, (numberOfConnectedUsers + 1));
                } else {
                    jsonStat.put(this.itemToIncrement, (numberOfConnectedUsers - 1));
                }

                jsonStat.put("numberSolicitations", numberOfSolicitationsMade);
                jsonStat.put("numberApprovals", numberOfApprovalsMade);
            }

            jsonObject.put("stats", jsonStat);

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
        if (itemToIncrement.equals("numberSolicitations")) {
            changeSolicitationsMade();
        } else if (itemToIncrement.equals("numberApprovals")) {
            changeApprovalsMade();
        } else if (itemToIncrement.equals("numberConnections")) {
            changeConnectionsMade();
        } else {
            System.out.println("[Invalid item to increment]");
        }
    }
}
