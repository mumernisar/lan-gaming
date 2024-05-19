import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class Games {
    public static void typeracer_handleData( String[][] data , HashMap<String, String> parsedMessage , String id){
        boolean rowIsEmpty = data[0][0] == null;  
        for (int i = 0; i < data.length; i++) {
            rowIsEmpty = data[i][0] == null;
            if (data[i][0] != null && id.equals(data[i][0])) { 
                data[i][1] = parsedMessage.get("progress");
                data[i][2] = parsedMessage.get("wpm");
                data[i][3] = parsedMessage.get("accuracy");
                break;
            } else if (rowIsEmpty) {
                data[i][0] = id;
                data[i][1] = parsedMessage.get("progress");
                data[i][2] = parsedMessage.get("wpm");
                data[i][3] = parsedMessage.get("accuracy");
                break;
            }
        }
        typeracer_printUsers(data);
     }

     private static void typeracer_printUsers(String[][] data) {
        System.out.format("Initializing GUI %s" , "🔮🎉😂🎊");

        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        System.out.println("   Name                 |       Progress         |  Words Per Minute(WPM) |     Accuracy    ");
        System.out.println("--------------------------------------------------------------------------------------------");
        for (String[] row : data) {
                if (row[0] != null){
                    String name = row[0];
                    String progress = row[1];
                    String wpm = row[2];
                    String accuracy = row[3];
                    System.out.format("%-24s|%-24s|%-24s|%-24s\n", name, (progress), (wpm), accuracy);
            }
        }
    }
    public static void typeracer( PrintWriter out , String id){
        try {
            String command = "start powershell.exe -NoExit -Command \"cd ../games/typeracer ; python dependencies.py ; python Typeracer.py\"";
            
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", command);
            Process p = pb.start();
            
            p.waitFor();

            String filePath = "../games/typeracer/convo.txt";
            boolean stop = false;

            while (true) {
                if (stop == true) {
                    break;
                }
            Thread.sleep(5000);
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                String wpm = "0";
                String percentile = "0";
                String end = "false";
                String accuracy = "100";
                    while ((line = reader.readLine()) != null) { 
                        System.out.println("Check line " + line);
                        if (line.contains("wpm")) {
                            wpm = line.split("=")[1];
                        }
                        if (line.contains("progress")) {
                            percentile = line.split("=")[1];
                        }
                        if (line.contains("accuracy")) {
                            accuracy = line.split("=")[1];
                        }
                        if (line.equals("stop")){
                            stop = true;
                            end = "true";
                            break;
                        }
                }
                if (wpm != "0" || percentile != "0") {
                    HashMap<String,String> smap = new HashMap<>();
                    smap.put("type", "game_data");
                    smap.put("game", "typeracer");
                    smap.put("wpm", wpm);
                    smap.put("progress", percentile);
                    smap.put("accuracy", accuracy);
                    smap.put("id" , id);
                    smap.put("end" , end);
                    String ds = ParseMap.unparse(smap);
                    System.out.println("Sending ds" + ds);
                    out.println(ds);
                }


            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
        }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Game ended");

    }

}
