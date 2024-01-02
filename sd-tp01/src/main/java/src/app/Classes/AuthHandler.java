package src.app.Classes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import src.app.Classes.Models.General;
import src.app.Classes.Models.Private;
import src.app.Classes.Models.ReplyObject;
import src.app.Classes.Models.Sergeant;
import src.app.Classes.Models.User;

public class AuthHandler {

    /**
     * Verifies if the user is registered in the system.
     * 
     * @param user The user to be verified.
     * @return True if the user is registered, false otherwise.
     */
    public static ReplyObject verifyLogin(String username, String password) {
        try {
            FileReader fileReader = new FileReader("sd-tp01/src/main/java/src/app/Data/Users.json");

            JSONParser jsonParser = new JSONParser();

            JSONObject jsonObject = (JSONObject) jsonParser.parse(fileReader);

            JSONArray jsonUsers = (JSONArray) jsonObject.get("users");

            for (Object object : jsonUsers) {
                JSONObject jsonUser = (JSONObject) object;

                String jsonUsername = (String) jsonUser.get("name");
                String jsonPassword = (String) jsonUser.get("password");
                String jsonRank = (String) jsonUser.get("rank");

                if (jsonUsername.equals(username) && jsonPassword.equals(password)) {
                    System.out.println("User " + jsonUsername + " logged in successfully.");

                    // Create a new user object
                    User user = null;

                    switch (jsonRank) {
                        case "Private":
                            user = new Private(jsonUsername, jsonPassword);
                            break;
                        case "Sergeant":
                            user = new Sergeant(jsonUsername, jsonPassword);
                            break;
                        case "General":
                            user = new General(jsonUsername, jsonPassword);
                            break;
                    }

                    return new ReplyObject(true, user);
                }
            }

            // Close the file reader
            fileReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found - " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error reading file - " + e.getMessage());
        } catch (ParseException e) {
            System.out.println("Parse error - " + e.getMessage());
        }

        return new ReplyObject(false);
    }

    /**
     * Registers a new user in the system.
     * 
     * @param name     The name of the user.
     * @param password The password of the user.
     */
    public static ReplyObject registerUser(String name, String password, String rank) {
        if (verifyCredentials(name)) {
            System.out.println("Username already exists!");
            return new ReplyObject(false);
        } else {
            try {
                Path path = Path.of("sd-tp01/src/main/java/src/app/Data/Users.json");
                File file = new File(path.toString());

                // Check if the file is empty
                long fileSize = Files.size(path);

                JSONObject jsonObject;
                JSONArray jsonUsers;

                // Use try-with-resources to automatically close the FileReader
                FileReader fileReader = new FileReader(file);
                JSONParser jsonParser = new JSONParser();

                // Check if the file is empty
                if (fileSize == 0) {
                    // File is empty, initialize a new JSON structure
                    jsonObject = new JSONObject();
                    jsonUsers = new JSONArray();
                } else {
                    // File is not empty, parse the existing JSON content
                    jsonObject = (JSONObject) jsonParser.parse(fileReader);
                    jsonUsers = (JSONArray) jsonObject.get("users");
                }

                // Sanitize the input, removing non-printable characters and backspaces
                String sanitizedName = name.replaceAll("[^a-zA-Z0-9]", "");
                String sanitizedPassword = password.replaceAll("[^a-zA-Z0-9]", "");

                JSONObject newUser = new JSONObject();
                newUser.put("name", sanitizedName);
                newUser.put("password", sanitizedPassword);
                newUser.put("rank", rank);

                jsonUsers.add(newUser);

                jsonObject.put("users", jsonUsers);

                // Use GsonBuilder to set formatting options for better indentation
                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                // Beautify the JSON before writing it to the file
                // by adding newlines and indentation
                String prettyJson = gson.toJson(jsonObject);

                // Use try-with-resources to automatically close the FileWriter
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(prettyJson);

                fileWriter.close();
                fileReader.close();

            } catch (FileNotFoundException e) {
                System.out.println("File not found - " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Error reading/writing file - " + e.getMessage());
            } catch (ParseException e) {
                System.out.println("Parse error - " + e.getMessage());
            }
            return new ReplyObject(true);
        }
    }

    private static boolean verifyCredentials(String name) {
        try {
            FileReader fileReader = new FileReader("sd-tp01/src/main/java/src/app/Data/Users.json");

            JSONParser jsonParser = new JSONParser();

            JSONObject jsonObject = (JSONObject) jsonParser.parse(fileReader);

            JSONArray jsonUsers = (JSONArray) jsonObject.get("users");

            for (Object object : jsonUsers) {
                JSONObject jsonUser = (JSONObject) object;

                String jsonUsername = (String) jsonUser.get("name");

                if (jsonUsername.equals(name)) {
                    return true;
                }
            }

            // Close the file reader
            fileReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found - " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error reading file - " + e.getMessage());
        } catch (ParseException e) {
            System.out.println("Parse error - " + e.getMessage());
        }

        return false;
    }

}
