import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class Games {
    public static void main (String[] args){
        
    }
    
    public static void typeracer( PrintWriter out , String id){
        try {
            String command = "start powershell.exe -NoExit -Command \"cd ../games/typeracer ; python dependencies.py ; python SpeedTyping.py\"";
            
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
                    while ((line = reader.readLine()) != null) { 
                        System.out.println("Check line " + line);
                        if (line.contains("wpm")) {
                            wpm = line.split("=")[1];
                        }
                        if (line.contains("progress")) {
                            percentile = line.split("=")[1];
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
    }
}
