package src.app.Classes;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;

public class AuthHandler {
    /**
     * Handles the authorization request from the requester to the approver.
     * 
     * @param requester The user requesting authorization.
     * @param approver  The user approving the authorization.
     * @return True if the authorization request was successful, false otherwise.
     * 
     */
    public void handleAuthorizationRequest(User requester, User approver) {
        // approver.processAuthorizationRequest(requester);
    }

    public void handleAuthorizationResponse(User requester, User approver) {
        // requester.processAuthorizationResponse(approver);
    }

    /**
     * Verifies if the user is registered in the system.
     * 
     * @param user The user to be verified.
     * @return True if the user is registered, false otherwise.
     */
    public static boolean verifyLogin(User user) {
        try {
            FileReader fileReader = new FileReader("src/app/Data/Users.json");

            JSONParser jsonParser = new JSONParser();

            JSONObject jsonObject = (JSONObject) jsonParser.parse(fileReader);

            JSONArray jsonUsers = (JSONArray) jsonObject.get("users");

            for (Object object : jsonUsers) {
                JSONObject jsonUser = (JSONObject) object;

                String name = (String) jsonUser.get("name");
                String password = (String) jsonUser.get("password");

                if (name.equals(user.getName()) && password.equals(user.getPassword())) {
                    System.out.println("User " + name + " logged in successfully.");
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

    /**
     * Registers a new user in the system.
     * 
     * @param name     The name of the user.
     * @param password The password of the user.
     */
    public static void registerUser(String name, String password) {
        try {
            FileReader fileReader = new FileReader("src/app/Data/Users.json");

            FileWriter fileWriter = new FileWriter("src/app/Data/Users.json");

            JSONParser jsonParser = new JSONParser();

            JSONObject jsonObject = (JSONObject) jsonParser.parse(fileReader);

            JSONArray jsonUsers = (JSONArray) jsonObject.get("users");

            JSONObject newUser = new JSONObject();
            newUser.put("name", name);
            newUser.put("password", password);
            newUser.put("type", "Private");

            jsonUsers.add(newUser);

            jsonObject.put("users", jsonUsers);

            Gson gson = new Gson();

            // Beautify the JSON before writing it to the file
            // by adding newlines and indentation
            String prettyJson = gson.toJson(jsonObject);

            fileWriter.write(prettyJson);

            // Close the file reader and writer
            fileReader.close();
            fileWriter.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found - " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error reading file - " + e.getMessage());
        } catch (ParseException e) {
            System.out.println("Parse error - " + e.getMessage());
        }
    }
}
