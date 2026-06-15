/*40/40
 * Noor Alharam
 * GAME SHOW Project
 * 
 * THE ULTIMATE GAME OF JEOPARDY
 * April 15, 2024
 * 
 * Challenge Features:
 *     ▶ Background Music and sound effects for right and wrong answer
 *     ▶ Time limit of 20 seconds for each question
 *     
 *     
 * ✨✨ ENJOY! ✨✨
 *
 */

import java.util.Timer;
import java.util.TimerTask;
import java.util.Arrays;
import java.util.Scanner;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.util.Random;

public class GameShow {

	// TIMER
	static Timer timer;

	private static boolean timeIsUp;

	// SOUND FILES
	static String filePathCorrect = "correct.wav";
	static String filePathWrong = "wrong.wav";
	static String filePathTryAgain = "tryAgain.wav";
	static String filePathBackground = "background.wav";

	static Random random = new Random();

	static Scanner in = new Scanner(System.in);

	static boolean[] historyAsked = new boolean[4];
	static boolean[] geographyAsked = new boolean[4];
	static boolean[] scienceAsked = new boolean[4];
	static boolean[] foodAsked = new boolean[4];

	static String[] randomAnswers = {};

	static int totalMoney = 700;
	static int winnings = 0;

	// HISTORY QUESTIONS AND ANSWERS
	static String[] historyQuestions = { "What is considered the largest empire in history?",
			"What year did India gain independence from Britain?", "What is the world’s oldest recorded civilization?",
			"Modern-day Istanbul, Turkey, used to be called what when it was the capital of the Byzantine Empire?" };
	static String[] historyAnswers = { "b", "d", "a", "c" };

	// GEOGRAPHY QUESTIONS AND ANSWERS
	static String[] geographyQuestions = { "Which Asian country is known as the \"Land of the Thunder Dragon\"?",
			"What is the second-largest island in the world and is part of Indonesia?",
			"How many time zones does Russia have?",
			"The city of Constantinople became the capital of the Islamic Caliphate after what Empire took control?" };
	static String[] geographyAnswers = { "b", "d", "a", "b" };

	// SCIENCE QUESTIONS AND ANSWERS
	static String[] scienceQuestions = { "Which is the most abundant element in the universe?",
			"What part of the human body serves the purpose of maintaining balance?", "What does DNA stand for?",
			"Roughly how long does it take for the sun’s light to reach Earth?" };
	static String[] scienceAnswers = { "a", "c", "d", "a" };

	// FOOD QUESTIONS AND ANSWERS
	static String[] foodQuestions = { "Which is the only US state that grows and produces coffee?",
			"What is Pakistan's national food?", "Which country's popular dish is mopane striped worm?",
			"In Newark, New Jersey it’s illegal to sell this food item after 6 pm without a doctor’s note. What food is it?" };
	static String[] foodAnswers = { "b", "a", "d", "a" };

	static String topicChoice = " ";

	public static void main(String[] args) {

		String prompt;

		// Playing background music
		playBackgroundMusicStart(filePathBackground);

		prompt = " ";

		mainMenu();

		while (true) {// using a while loop to run the main program until the user enters "Q"

			System.out.print("▶ ");
			prompt = in.nextLine();

			// using a while loop to force the user to enter a valid input
			while (!prompt.trim().equalsIgnoreCase("Q") && !prompt.trim().equalsIgnoreCase("S")
					&& !prompt.trim().equalsIgnoreCase("I")) {
				playTryAgainMusic(filePathTryAgain);
				System.out.print("▶ ");
				prompt = in.nextLine();
			}

			// using an if statement to check if the user enters the letter "Q"
			if (prompt.trim().equalsIgnoreCase("Q")) {
				break;
			}
			// using an if statement to check if the user enters the letter "I"
			if (prompt.trim().equalsIgnoreCase("I")) {
				instructions();
			}
			// using an else if statement to check if the user enters the letter "S"
			else if (prompt.trim().equalsIgnoreCase("S")) {
				startGame(); // run the start game method
			}
		}
		in.close();
	}

	// METHOD FOR STARTING THE GAME
	public static void startGame() {
		cancelTimer();

		// using an if statement to check if the user's total winnings is the the amount
		// of money all the questions are worth combined which is 27000
		if (winnings == 27000) {
			gameEndWon(winnings);// end the game (display to user)
			return;
		}

		System.out.println(" ");

		System.out.println("""

				               SELECT A TOPIC (enter a letter)
				______________________________________________________________
				|                                                             |
				|    _             _              _              _            |
				|    H - HISTORY   G - GEOGRAPHY  S - SCIENCE    F - FOOD     |
				|                                                             |
				|_____________________________________________________________|

				""");

		topicChoice = getInput("▶ ");

		// using a while loop to check if the user entered a valid topic choice and
		// force them to enter a valid choice
		while (!isValidTopic(topicChoice)) {
			playTryAgainMusic(filePathTryAgain);
			System.out.println("Enter a VALID topic: ");
			topicChoice = getInput("▶ ");
		}
		selectQuestion(topicChoice); // method for selecting a question based on the topic choice
	}

	// SELECTING QUESTIONS METHOD
	public static void selectQuestion(String topicChoice) {

		cancelTimer();

		int amount = 0;
		String correctAnswer;
		int index;
		boolean allQuestionsAsked = true;
		int money;
		String moneyAmountChoice, topicName;

		boolean[] askedArray;

		// Using an if statement to check the user chose the history topic
		if (topicChoice.trim().equalsIgnoreCase("H")) {
			askedArray = historyAsked;
			topicName = "HISTORY";
		} else if (topicChoice.trim().equalsIgnoreCase("G")) {// Using an if statement to check the user chose the
																// geography topic
			askedArray = geographyAsked;
			topicName = "GEOGRAPHY";
		} else if (topicChoice.trim().equalsIgnoreCase("S")) {// Using an if statement to check the user chose the
																// science topic
			askedArray = scienceAsked;
			topicName = "SCIENCE";
		} else if (topicChoice.trim().equalsIgnoreCase("F")) {// Using an if statement to check the user chose the
																// food topic
			askedArray = foodAsked;
			topicName = "FOOD";
		} else
			return;

		for (boolean asked : askedArray) { // using a for loop to check if any element is false in askedArray
			if (!asked) {
				allQuestionsAsked = false;
				break;
			}
		}

		if (allQuestionsAsked) {// using an if statement to determine if the user answered all questions for the
								// topic
			playTryAgainMusic(filePathTryAgain);
			System.out.println();
			System.out.println("You have already answered all questions for " + topicName + ".");
			startGame();// return to the start of the game through the method
			return;
		}

		System.out.println(" ");
		System.out.println("""

				              SELECT AN AMOUNT (enter a number)
				____________________________________________________________________
				|                                                                   |
				|                                                                   |
				| [    $500    ]  [    $1000    ]  [    $2500    ]  [    $5000    ] |
				|                                                                   |
				|___________________________________________________________________|
				""");

		moneyAmountChoice = getInput("▶ ");

		// using a while loop to check and force the user to enter a valid amount
		while (!isValidMoneyAmount(moneyAmountChoice)) {
			playTryAgainMusic(filePathTryAgain);
			System.out.println("Please enter a valid amount: ");
			moneyAmountChoice = getInput("▶ ");
		}

		money = Integer.parseInt(moneyAmountChoice);

		index = money == 500 ? 0 : money == 1000 ? 1 : money == 2500 ? 2 : money == 5000 ? 3 : -1;

		// Check if the selected amount corresponds to a question that has already been
		// asked
		if (askedArray[index]) {
			playTryAgainMusic(filePathTryAgain);
			System.out.println();
			System.out.println("You have already answered a question worth $" + money + " for this topic.");
			selectQuestion(topicChoice);
			return;
		}

		askedArray[index] = true;

		amount = askQuestion(topicChoice, money); // printing the questions that return an amount depending on whether
													// the user successfully
													// answered the question correctly

		System.out.println(" ");
		if (amount == money) {// using an if statement to check if the user answered question correctly by
								// comparing the 2 values
			playCorrectMusic(filePathCorrect);
			winnings += money;
			if (money == 5000) {
				totalMoney *= 2;
			}
			System.out.println("""
					      CORRECT! ✔

					""");
			System.out.println("   ~ MONEY BANK: $" + winnings + " ~");
			System.out.println();
			System.out.println(" ~ NET SAFETY AMOUNT: $" + totalMoney + " ~");
		} else if (amount != -1) {
			playWrongMusic(filePathWrong);
			System.out.println("""


					    INCORRECT! ✗

					""");

			correctAnswer = displayCorrectAnswer(topicChoice, money);
			System.out.print("The correct answer was: " + correctAnswer);
			System.out.println();
			System.out.println();
			System.out.println();
			gameEndLost(totalMoney);
			mainMenu();
			return;
		}

		if (amount != -1) {
			startGame();
		}
	}

	// ASKING THE USER QUESTIONS
	private static int askQuestion(String topic, int money) {
		
		String correctAnswer;
		String[] questions;
		String[] answers;
		String[] options = {};
		int index;
		String topicName = " ";
		String userAnswer;
		boolean[] askedArray = new boolean[4];

		index = money == 500 ? 0 : money == 1000 ? 1 : money == 2500 ? 2 : money == 5000 ? 3 : -1;

		if (topic.trim().equalsIgnoreCase("H")) {// using an if statement to check if the user chose the history topic

			askedArray = historyAsked;
			topicName = "HISTORY";
			questions = historyQuestions;
			answers = historyAnswers;
			if (money == 500) {// using an if statement to check if the user chose the question worth $500
				options = getHistoryOptions1(index); // retrieve the options for that question
			} else if (money == 1000) {
				options = getHistoryOptions2(index);
			} else if (money == 2500) {
				options = getHistoryOptions3(index);
			} else if (money == 5000) {
				options = getHistoryOptions4(index);
			}
		} else if (topic.trim().equalsIgnoreCase("G")) {// using an if statement to check if the user chose the
														// geography topic

			askedArray = geographyAsked;
			topicName = "GEOGRAPHY";
			questions = geographyQuestions;
			answers = geographyAnswers;

			if (money == 500) {
				options = getGeoOptions1(index);
			} else if (money == 1000) {
				options = getGeoOptions2(index);
			} else if (money == 2500) {
				options = getGeoOptions3(index);
			} else if (money == 5000) {
				options = getGeoOptions4(index);
			}

		} else if (topic.trim().equalsIgnoreCase("S")) {// using an if statement to check if the user chose the science
														// topic

			askedArray = scienceAsked;
			topicName = "SCIENCE";
			questions = scienceQuestions;
			answers = scienceAnswers;

			if (money == 500) {
				options = getSciOptions1(index);
			} else if (money == 1000) {
				options = getSciOptions2(index);
			} else if (money == 2500) {
				options = getSciOptions3(index);
			} else if (money == 5000) {
				options = getSciOptions4(index);
			}
		}

		else if (topic.trim().equalsIgnoreCase("F")) {// using an if statement to check if the user chose the food topic

			askedArray = foodAsked;
			topicName = "FOOD";
			questions = foodQuestions;
			answers = foodAnswers;

			if (money == 500) {
				options = getFoodOptions1(index);
			} else if (money == 1000) {
				options = getFoodOptions2(index);
			} else if (money == 2500) {
				options = getFoodOptions3(index);
			} else if (money == 5000) {
				options = getFoodOptions4(index);
			}

		} else {
			return 0; // No questions found
		}

		// START TIMER
		startTimer(askedArray, index);

		System.out.println(" ");
		System.out.println("       Jeopardy: " + topicName + "  ($" + money + ")");
		System.out.println();
		System.out.println("  ENTER THE LETTER BESIDE THE ANSWER");
		System.out.println(" ");
		System.out.println(questions[index]);

		for (int i = 0; i < options.length; i++) {
			System.out.println((char) ('a' + i) + ") " + options[i]);
		}

		System.out.println();
		userAnswer = getInput("▶ ");

		

		if (timeIsUp) {//using an if statement to check if the time is up
			cancelTimer();
			correctAnswer = displayCorrectAnswer(topicChoice, money);
			System.out.print("The correct answer was: " + correctAnswer);
			System.out.println();
			System.out.println();
			System.out.println();
			gameEndLost(totalMoney);
			mainMenu();
			return -1;
		}

		else {//otherwise check if user's input is valid
			while (!isValidOption(userAnswer) && timeIsUp == false) {
				playTryAgainMusic(filePathTryAgain);
				System.out.println("Please enter a valid option: ");
				userAnswer = getInput("▶ ");
			}
		}

		if (userAnswer.trim().equalsIgnoreCase(answers[index]) && timeIsUp == false) {// using an if statement to check
																						// if the user got the answer
																						// right
			cancelTimer();
			return money;
		} else if (money != -1 && !userAnswer.trim().equalsIgnoreCase(answers[index])) {
			cancelTimer();
			return 0;
		} else
			return -1;

	}

	public static boolean startTimer(boolean[] askedArray, int index) {// method for timer

		timeIsUp = false;

		timer = new Timer();

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				timeIsUp = true;
				playWrongMusic(filePathWrong);
				System.out.println("TIME'S UP! PRESS ENTER TO CONTINUE . . .");
				System.out.print(" ");
				// Mark the question as answered
				askedArray[index] = true;
			}
		}, 20000); // Set the time limit here

		return timeIsUp;
	}

	// CHECKING IF THE USER AMOUNT IS VALID
	public static boolean isValidMoneyAmount(String moneyAmountChoice) {
		return moneyAmountChoice.trim().matches("500|1000|2500|5000");
	}

	// CHECKING IF THE USER TOPIC IS VLAID
	public static boolean isValidTopic(String topicChoice) {
		return topicChoice.trim().equalsIgnoreCase("H") || topicChoice.trim().equalsIgnoreCase("G")
				|| topicChoice.trim().equalsIgnoreCase("S") || topicChoice.trim().equalsIgnoreCase("F");
	}

	// CHECKING IF USER INPUT IS VALID
	public static boolean isValidOption(String userAnswer) {
		return userAnswer.trim().equalsIgnoreCase("A") || userAnswer.trim().equalsIgnoreCase("B")
				|| userAnswer.trim().equalsIgnoreCase("C") || userAnswer.trim().equalsIgnoreCase("D");
	}

	// GETTING USER INPUT
	public static String getInput(String prompt) {
		System.out.print(prompt);
		return in.nextLine().trim();
	}

	// SETTING THE OPTIONS FOR HISTORY QUESTION 1
	public static String[] getHistoryOptions1(int index) {
		String[] options = { "Mongol", "British", "Ottoman", "Qing" };
		return options;
	}

	// SETTING THE OPTIONS FOR HISTORY QUESTION 2
	public static String[] getHistoryOptions2(int index) {
		String[] options = { "1949", "1932", "1920", "1947" };
		return options;
	}

	// SETTING THE OPTIONS FOR HISTORY QUESTION 3
	public static String[] getHistoryOptions3(int index) {
		String[] options = { "Mesopotamia", "Indus Valley", "Achaemenid", "Rome" };
		return options;
	}

	// SETTING THE OPTIONS FOR HISTORY QUESTION 4
	public static String[] getHistoryOptions4(int index) {
		String[] options = { "Perge", "Eskişehir", "Constantinople", "Konya" };
		return options;
	}

	// SETTING THE OPTIONS FOR GEOGRAPHY QUESTION 1
	public static String[] getGeoOptions1(int index) {
		String[] options = { "Japan", "Bhutan", "Malaysia", "Kuwait" };
		return options;
	}

	public static String[] getGeoOptions2(int index) {
		String[] options = { "Borneo", "Sulawesi", "Baffin", "New Guinea" };
		return options;
	}

	public static String[] getGeoOptions3(int index) {
		String[] options = { "11", "5", "8", "15" };
		return options;
	}

	public static String[] getGeoOptions4(int index) {
		String[] options = { "Byzantine Empire", "Ottoman Empire", "Mughal Empire", "Timurid Empire" };
		return options;
	}

	// DECLARING THE OPTIONS FOR SCIENCE QUESTION 1
	public static String[] getSciOptions1(int index) {
		String[] options = { "Hydrogen", "Oxygen", "Lithium", "Silicon" };
		return options;
	}

	public static String[] getSciOptions2(int index) {
		String[] options = { "Brain", "Eyes", "Ear", "Hands" };
		return options;
	}

	public static String[] getSciOptions3(int index) {
		String[] options = { "Dopamine Neurotransmitter", "Deoxyribonucleic Acid Polymerization",
				"Deoxyribonucleic Acid Repair", "Deoxyribonucleic Acid" };
		return options;
	}

	public static String[] getSciOptions4(int index) {
		String[] options = { "8 minutes and 20 seconds", "7 minutes and 45 seconds", "1 hour and 24 minutes",
				"26 minutes and 18 seconds" };
		return options;
	}

	// SETTING THE OPTIONS FOR FOOD QUESTION 1
	public static String[] getFoodOptions1(int index) {
		String[] options = { "Texas", "Hawaii", "Florida", "Louisiana" };
		return options;
	}

	public static String[] getFoodOptions2(int index) {
		String[] options = { "Nihari", "Biryani", "Haleem", "Tarka daal" };
		return options;
	}

	public static String[] getFoodOptions3(int index) {
		String[] options = { "Syria", "Mexico", "Lebanon", "Zimbabwe" };
		return options;
	}

	public static String[] getFoodOptions4(int index) {
		String[] options = { "Ice-cream", "Pizza", "Pastries", "Cheese Burger" };
		return options;
	}

	public static String displayCorrectAnswer(String topic, int money) { // method for displaying the right answer to
																			// user

		String[] correctAnswers;
		int index;

		if (topic.trim().equalsIgnoreCase("H")) {
			correctAnswers = historyAnswers;
		} else if (topic.trim().equalsIgnoreCase("G")) {
			correctAnswers = geographyAnswers;
		} else if (topic.trim().equalsIgnoreCase("S")) {
			correctAnswers = scienceAnswers;
		} else if (topic.trim().equalsIgnoreCase("F")) {
			correctAnswers = foodAnswers;
		} else
			correctAnswers = randomAnswers;

		index = money == 500 ? 0 : money == 1000 ? 1 : money == 2500 ? 2 : money == 5000 ? 3 : -1;

		return correctAnswers[index];

	}

	// method for canceling the timer
	public static void cancelTimer() {
		if (timer != null) {
			timer.cancel(); // Cancel the timer if it's not null
		}
	}

	// method for playing the sound for correct answers
	public static void playCorrectMusic(String filepath) {
		try {
			File correctMusicPath = new File(filepath);
			if (correctMusicPath.exists()) {
				AudioInputStream audioInput = AudioSystem.getAudioInputStream(correctMusicPath);
				Clip music = AudioSystem.getClip();
				music.open(audioInput);
				music.start();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// method for playing the sound for wrong answers
	public static void playWrongMusic(String filepath) {
		try {
			File wrongMusicPath = new File(filepath);
			if (wrongMusicPath.exists()) {
				AudioInputStream audioInput = AudioSystem.getAudioInputStream(wrongMusicPath);
				Clip music = AudioSystem.getClip();
				music.open(audioInput);
				music.start();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// method for playing the sound for entering input again
	public static void playTryAgainMusic(String filepath) {
		try {
			File tryAgainMusicPath = new File(filepath);
			if (tryAgainMusicPath.exists()) {
				AudioInputStream audioInput = AudioSystem.getAudioInputStream(tryAgainMusicPath);
				Clip music = AudioSystem.getClip();
				music.open(audioInput);
				music.start();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// method for playing the sound for background music
	public static void playBackgroundMusicStart(String filepath) {
		try {
			File backgroundMusicPath = new File(filepath);
			if (backgroundMusicPath.exists()) {
				AudioInputStream audioInput = AudioSystem.getAudioInputStream(backgroundMusicPath);
				Clip music = AudioSystem.getClip();
				music.open(audioInput);
				music.start();
				music.loop(Clip.LOOP_CONTINUOUSLY);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// USER ISNTRUCTIONS
	public static void instructions() {

		System.out.println(" ");

		System.out.println(
				"""




						                                                    ~ USER INSTRUCTIONS~
						 			                                  JEOPARDY
						      _____________________________________________________________________________________________________________________

								Welcome to Jeopardy!

								In this game, you'll you will encounter a total of 5 topics, each with 4 questions.
								The questions vary in difficulty level, with the easiest questions being worth the least
								amount of money, and the hardest questions being worth the most.

								You start the game with a net safety amount of $700. With every correct answer to a $5000 question,
								this safety net doubles, ensuring you won't leave empty-handed if things go awry.
								However, if you answer every question of the game correctly, you get to keep the amount that
								all the question were worth.

								For example, if you correctly answer a $2000 question, then a $5000 question, but answer a $1000 question
								incorrectly afterward, you will only leave with $700 (initial net safety amount) + another $700 from the $5000
								that was answered correctly, so in total you leave with $1400 from your net safety amount

								You also have 20 seconds to answer each question, if you answer within the time limit, you can move on to the next
								question, however, if you are unable to answer within the time limit, the game ends and you leave with
								your safety net amount.

								But beware! Any wrong answer ends the game, leaving you with your net safety amount to pocket. The answer
								to the incorrect question will be given at the end.
								So, aim high, think wisely, and let's see how much you can win!

							_____________________________________________________________________________________________________________________


						""");

	}

	public static void gameEndWon(int total) {
		System.out.println();
		System.out.println("" + "          *********************************************************\n"
				+ "          *                                                       * \n"
				+ "          *                        GAME OVER!                     *  \n"
				+ "          *               CONGRATULATIONS, YOU WON!               *\n"
				+ "          *                                                       * \n"
				+ "          *********************************************************\n"
				+ "                       __________________________  \n"
				+ "                                                \n" + " 	" + "       ~~    YOU LEAVE WITH: $"
				+ (winnings) + " IN WINNINGS!    ~~    \n" + "       	               _________________________\n" + ""
				+ "");

		totalMoney = 700;
		winnings = 0;
	}

	public static void gameEndLost(int total) {

		cancelTimer();

		System.out.println();

		System.out.println("" + "          *********************************************************\n"
				+ "          *                                                       * \n"
				+ "          *                        GAME OVER!                     *  \n"
				+ "          *                 THANK YOU FOR PLAYING!                *\n"
				+ "          *                                                       * \n"
				+ "          *********************************************************\n"
				+ "                       __________________________  \n"
				+ "                                                \n" + " 	" + "       ~~    YOU LEAVE WITH: $"
				+ totalMoney + " IN WINNINGS!    ~~    \n" + "       	               _________________________\n" + ""
				+ "");

		Arrays.fill(historyAsked, false);
		Arrays.fill(geographyAsked, false);
		Arrays.fill(scienceAsked, false);
		Arrays.fill(foodAsked, false);

		totalMoney = 700;
		winnings = 0;
	}

	public static void mainMenu() {

		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();

		System.out.println(
				"" + "                    _____________________________________________________________________ \n"
						+ "                   |_____________________________________________________________________|\n"
						+ "                   |*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*|\n"
						+ "                   |.                                                                   .|\n"
						+ "                   |*                            -Welcome to-                           *|\n"
						+ "                   |.                     THE ULTIMATE JEOPARDY GAME!                   .|\n"
						+ "                   |*                           -Get ready-                             *|\n"
						+ "                   |.                                                                   .|\n"
						+ "                   |*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*|\n"
						+ "                   |---------------------------------------------------------------------|\n"
						+ "                   |_____________________________________________________________________|\n"
						+ "\n" + "                                                    ***\n" + "\n"
						+ "                                          MAIN MENU (choose one):\n"
						+ "                                       Select 'S' to Choose a topic\n"
						+ "                                        Select 'Q' to Quit program\n"
						+ "                                       Select 'I' for Instructions\n"

		);

	}

}
