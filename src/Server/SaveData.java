package Server;

import java.io.*;
import java.util.*;

public class SaveData {
    
    /// modify this function to work for you aswell
    public static int save (HashMap<String, String> userData) {
        System.out.println(System.getProperty("user.dir"));
        File file = new File(System.getProperty("user.dir")+"/src/Server/userData/typeracer/leaderboard.txt");
        List<String[]> leaderboard = new ArrayList<>();
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    leaderboard.add(line.split(";"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        
        int wpm = Integer.parseInt(userData.get("wpm"));
        int accuracy = Integer.parseInt(userData.get("accuracy"));
        String username = userData.get("id");
        int userScore = wpm + accuracy;
        String[] userEntry = new String[]{username, String.valueOf(wpm), String.valueOf(accuracy), String.valueOf(userScore)};
        int index = 0;

        if (leaderboard.isEmpty()) {
            leaderboard.add(userEntry);
        } else {
            for (; index < leaderboard.size(); index++) {
                int currentScore = Integer.parseInt(leaderboard.get(index)[3]);
                if (userScore > currentScore) {
                    break;
                }
            }
            leaderboard.add(index, userEntry);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String[] data : leaderboard) {
                writer.write(String.join(";", data));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return index;

    }



    public static String getLeaderboardString(int highlightIndex) {
        File file = new File(System.getProperty("user.dir")+"/src/Server/userData/typeracer/leaderboard.txt");

        StringBuilder leaderboardString = new StringBuilder();
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    leaderboardString.append(line).append("-n-");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return leaderboardString.toString();
    }

    public static ArrayList<String> saveDataCapitals(String messageFromClients) {

        ArrayList <String> resultData = new ArrayList<>();
        if (messageFromClients.contains("points on")) {
            String filepath = "src/Server/userData/capitals/leaderboard.txt";
            try {
                BufferedWriter resultWriter = new BufferedWriter(new FileWriter(filepath, true));
                resultWriter.append(messageFromClients);
                resultWriter.close();

                BufferedReader reader = new BufferedReader(new FileReader(filepath));
                String line;
                while ((line = reader.readLine()) != null) {
                    resultData.add(line);
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return resultData;
    }
}

