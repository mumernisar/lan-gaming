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


	/**
	 * Starts the game by reading countries from a file, setting game difficulty, and allowing the user to guess a country.
	 *
	 * @return the result of the game
	 * @throws IOException if an I/O error occurs while reading the countries from the file
 	*/
	public String startGame() {

		readCountriesFromFile();
		setGameDifficulty();
		guessCountry();

		return result();

	}


	/**
	 * Returns the result of the game for the client.
	 *
	 * @return A string representing the result in the format "clientName: got correct/repeatGame points on systemDateAndTime"
	 *
	 * @throws NullPointerException if clientName is null
 	*/
	public String result() {
		return clientName + ":  got " + correct + "/" + repeatGame + " points on " + getSystemDateAndTime();
	}


	/**
	 * Reads the countries and capitals from the files "countries.txt" and "capitals.txt" respectively
	 * and populates the countries and capitals ArrayLists.
	 *
	 * @throws IOException if an I/O error occurs while reading the files
 	*/
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
	/**
	 * Sets the game difficulty based on user input.
	 *
	 * This method prompts the user to enter the difficulty level and sets the game parameters accordingly.
	 *
	 * @throws InputMismatchException if the input is not a valid integer
	 * @throws NoSuchElementException if no line is found
 	*/
	private void setGameDifficulty() {

		System.out.println("Enter the difficulty level: ");
		System.out.println("\n1 for easy\n2 for medium\n3 for hard\n4 for ultra");
		input = scan.nextLine();
		while (true) {
			if (input.equals("1")) {
				secondsToWait = 9;   // 9 seconds to think of an answer
				this.difficulty = 50; // top 50 populous countries will be asked
				this.repeatGame = 3;  // game repeats 3 times
				break;
			} else if (input.equals("2")) {
				secondsToWait = 8;    // 8 seconds to think of an answer
				this.difficulty = 90;  // top 90 populous countries will be asked
				this.repeatGame = 4;   // game repeat 4 times
				break;
			} else if (input.equals("3")) {
				secondsToWait = 7;    // 7 seconds to think of an answer
				this.difficulty = 135; // top 135 populous countries will be asked
				this.repeatGame = 5;   // game repeat 5 times
				break;
			} else if (input.equals("4")) {
				secondsToWait = 5;    // 5 seconds to think of an answer
				this.difficulty = 135; // top 135 populous countries will be asked
				this.repeatGame = 6;   // game repeat 5 times
				break;
			} else {
				System.out.println("Invalid input, Enter again: ");
				input = scan.nextLine();
			}
		}
	}


	/**
	 * Displays a timer with the given string for the specified number of seconds.
	 *
	 * @param str the string to display with the timer
	 * @param seconds the number of seconds for the timer
	 * @throws InterruptedException if the thread is interrupted while sleeping
 	*/
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


	/**
	 * Returns the current system date and time in the format dd/MM/yyyy HH:mm:ss.
	 *
	 * @return a string representing the current system date and time
 	*/
	private String getSystemDateAndTime() {
		// returns dateand time at that instant in format dd/MM/yyyy HH:mm:ss
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		return now.format(formatter);
	}


	/**
	 * Pauses the execution of the current thread for a specified number of milliseconds.
	 *
	 * @param ms the number of milliseconds to pause the execution
	 * @throws InterruptedException if any thread has interrupted the current thread
 	*/
	private void stopExecFor(int ms) {
		try {
			Thread.sleep(ms);
		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
		}
	}

	/**
	 * Generates a random number representing a country based on the difficulty level.
	 *
	 * @return An integer representing a randomly generated country.
	 * @throws IllegalArgumentException if the difficulty level is negative.
 	*/
	private int randomCountryGenerator() {
		return (int) Math.round(Math.random() * difficulty);
	}

	/**
	 * This method allows the user to guess the capital of a randomly selected country.
	 * It repeats the game for a specified number of times and prompts the user to guess the capital.
	 *
	 * @throws NoSuchElementException if the randomCountryGenerator() method returns an invalid index
 	*/
	private void guessCountry() {

		int timesToRepeat =  this.repeatGame;
		for (int i = 0; i < timesToRepeat; i++) {

			int randomCountry = randomCountryGenerator();

			System.out.println(capitals.get(randomCountry));
			System.out.println("Guess the capital of: " + countries.get(randomCountry));

			// Displays the timer according to the difficulty
			displayTimer(" seconds to guess...", secondsToWait);
			// user input validation is called
			String capitalGuessed = timedUserInput(9);

			// checks the country
			if (capitalGuessed.equalsIgnoreCase(capitals.get(randomCountry))) {
				correct++;
			}
		}
	}

	/**
	 * This method waits for user input for a specified amount of time and returns the input if received within the time limit.
	 * If the user fails to input within the time limit, it returns "false".
	 *
	 * @param timeToWait the time limit in seconds for user input
	 * @return the user input if received within the time limit, or "false" if the time limit is exceeded
	 * @throws NoSuchElementException if no line was found
 	*/
	private String timedUserInput(int timeToWait) {

		System.out.println("You have " + timeToWait + " seconds to answer...");
		long startTime = System.currentTimeMillis(); // Get the start time

		// Wait for user input
		String userInput = scan.nextLine();

		long endTime = System.currentTimeMillis(); // Get the end time
		long elapsedTime = endTime - startTime; // Calculate elapsed time

		// seconds * 1000 = milliseconds (because System.currentTimeMillis()
		// gives result in milliseconds
		if (elapsedTime > (timeToWait * 1000)) {
			System.out.println("You failed to answer in " + timeToWait + " seconds");
			return "false";
		}
		return userInput;
	}
}