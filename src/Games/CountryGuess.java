package Games;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CountryGuess {

	// make constructor in accordancce to the client name, if client name is Ahmad, di it like this
	// CountryGuess client1 = new CountryGuess("Ahmad");

	private static String clientName;
	private String input = "";
	private ArrayList<String> countries = null;
	private ArrayList<String> capitals = null;
	private int repeatGame = 0;
	private int secondsToWait = 0;
	private int correct = 0;
	private int difficulty = 0;
	private final Scanner scan = new Scanner(System.in);


	public CountryGuess(String clientName) {
		CountryGuess.clientName = clientName;
	}

	public CountryGuess() {

	}

	// public static void main(String[] args) {

	//     CountryGuess countryGuess = new CountryGuess();

	//     countryGuess.readCountriesFromFile();
	//     countryGuess.setGameDifficulty();
	//     countryGuess.guessCountry();
	//     // your result
	//     countryGuess.displayResult();
	//     countryGuess.setLeaderboard();
	//     countryGuess.displayLeaderBoard();
	// }
	public static void handleGame(String username) {
		CountryGuess user = new CountryGuess(username);
		user.startGame();
	}
	public void startGame() {
		readCountriesFromFile();
		setGameDifficulty();
		guessCountry();
		displayResult();
		setLeaderboard();
		displayLeaderBoard();
	}

	public String result() {
		return clientName + " got " + correct + "/" + repeatGame + " points on " + getSystemDateAndTime();
	}

	private void displayResult() {
		System.out.println("-------------------------------------------");
		System.out.println(result());
		System.out.println("-------------------------------------------");
	}


	private void readCountriesFromFile() {
		String countriesFile = "src/Games/capitals/countries.txt";

		String capitalsFile = "src/Games/capitals/capitals.txt";

		String countrriesarray[] = new String[135];
		String capitalssarray[] = new String[135];

		try(BufferedReader reader = new BufferedReader(new FileReader(countriesFile))) {
			String line = reader.readLine();
			countrriesarray = line.split(",");

		} catch (IOException e) {
			e.printStackTrace();
		}

		try(BufferedReader reader1 = new BufferedReader(new FileReader(capitalsFile))) {
			String line = reader1.readLine();
			capitalssarray = line.split(",");

		} catch (IOException e) {
			e.printStackTrace();
		}
		countries = new ArrayList<>(Arrays.asList(countrriesarray));
		capitals = new ArrayList<>(Arrays.asList(capitalssarray));
	}


	private void setGameDifficulty() {

		System.out.println("Enter the difficulty level: ");
		System.out.println("\n1 for easy\n2 for medium\n3 for hard\n4 for ultra");
		input = scan.nextLine();
		while (true) {
			if (input.equalsIgnoreCase("1")) {
				secondsToWait = 9;   // 14 seconds to think of an answer
				this.difficulty = 50; // top 50 populous countries will be asked
				this.repeatGame = 3;  // game repeats 3 times
				break;
			} else if (input.equalsIgnoreCase("2")) {
				secondsToWait = 8;    // 12 seconds to think of an answer
				this.difficulty = 90;  // top 90 populous countries will be asked
				this.repeatGame = 4;   // game repeat 4 times
				break;
			} else if (input.equalsIgnoreCase("3")) {
				secondsToWait = 7;    // 10 seconds to think of an answer
				this.difficulty = 135; // top 135 populous countries will be asked
				this.repeatGame = 5;   // game repeat 5 times
				break;
			} else if (input.equalsIgnoreCase("4")) {
				secondsToWait = 5;    // 10 seconds to think of an answer
				this.difficulty = 135; // top 135 populous countries will be asked
				this.repeatGame = 6;   // game repeat 5 times
				break;
			} else {
				System.out.println("Invalid input, Enter again: ");
				input = scan.nextLine();
			}
		}
	}


	private void displayTimer(String str, int seconds) {
		String line = "";
		for (int i = seconds; i > 0; i--) {
			line = i + str;
			System.out.print(line);
			stopExecFor(1000);
			// Clear the line by moving the cursor to the beginning and clearing from there
			System.out.print("\r\033[K");
			System.out.flush();
		}
	}


	private String getSystemDateAndTime() {
		// returns dateand time at that instant in format dd/MM/yyyy HH:mm:ss
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		return now.format(formatter);
	}


	private void stopExecFor(int ms) {
		try {
			Thread.sleep(ms);
		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
		}
	}

	private int randomCountryGenerator() {
		return (int) Math.round(Math.random() * difficulty);
	}

	private void guessCountry() {

		int timesToRepeat =  this.repeatGame;
		for (int i = 0; i < timesToRepeat; i++) {
			int randomCountry = randomCountryGenerator();
			System.out.println(capitals.get(randomCountry));
			System.out.println("Guess the capital of: " + countries.get(randomCountry));
			// Displays the timer according to the difficulty
			displayTimer(" seconds to guess...", secondsToWait);
			// user input validation is called
			String capitalGuessed = timedUserInput(10);
			// checks the country
			if (capitalGuessed != null && capitalGuessed.equalsIgnoreCase(capitals.get(randomCountry))){
				correct++;
			}
		}
	}

	private String timedUserInput(int timeToWait) {

		System.out.println("You have 8 seconds to answer...");
		long startTime = System.currentTimeMillis(); // Get the start time

		// Wait for user input
		String userInput = scan.nextLine();

		long endTime = System.currentTimeMillis(); // Get the end time
		long elapsedTime = endTime - startTime; // Calculate elapsed time

		// seconds * 1000 = milliseconds (because System.currentTimeMillis()
		// gives result in milliseconds
		if (elapsedTime > (timeToWait * 1000)) {
			System.out.println("You failed to answer in " + timeToWait + " seconds");
			return null;
		}
		return userInput;
	}


	private void setLeaderboard() {
		try (BufferedWriter result = new BufferedWriter(new FileWriter("src/Games/capitals/leaderboard.txt", true))) {
			result.append(result());
			result.newLine();
			result.append(" ");
			result.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void displayLeaderBoard() {
		try (BufferedReader reader = new BufferedReader (new FileReader("src/Games/capitals/leaderboard.txt"))) {

			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
			System.out.println("------------------------------------------");
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}