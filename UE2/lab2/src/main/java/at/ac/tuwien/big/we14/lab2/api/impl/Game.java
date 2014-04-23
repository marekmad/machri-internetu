package at.ac.tuwien.big.we14.lab2.api.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletContext;

import at.ac.tuwien.big.we14.lab2.api.Category;
import at.ac.tuwien.big.we14.lab2.api.Choice;
import at.ac.tuwien.big.we14.lab2.api.Question;
import at.ac.tuwien.big.we14.lab2.api.QuestionDataProvider;
import at.ac.tuwien.big.we14.lab2.api.QuizFactory;
import at.ac.tuwien.big.we14.lab2.api.impl.Player.QuestionState;

public class Game {

	private int roundNumber = 0;
	private int questionNumber = 0;
	
	private String requestDispatcherAction = "";

	private Player player1;
	private Player player2;

	private List<SimpleCategory> categories = new ArrayList<SimpleCategory>();
	private List<SimpleQuestion> questionForCategory = new ArrayList<SimpleQuestion>();

	private AskedQuestion acctuallQuestion;
	
	public static final String QUESTION_JSP = "question";
	public static final String ROUND_COMPLETE_JSP = "roundcomplete";
	public static final String FINISH_JSP = "finish";

	public Game() {
		player1 = new Player("Spieler1");
		player2 = new Player("Spieler2");
	}

	public void validateAnswer(String[] selected) {

		System.out.println("______Questionnumber" + questionNumber);

		List<Choice> allcChoises = new ArrayList<Choice>();
		List<Choice> cChoises = new ArrayList<Choice>();
		allcChoises.addAll(questionForCategory.get(questionNumber - 1)
				.getCorrectChoices());

		List<Choice> allChoicesAsked = acctuallQuestion.getAllChoices();
		System.out.println("askedQuestion: " + acctuallQuestion.getText());

		for (Choice allC : allChoicesAsked) {
			for (Choice c : allcChoises) {
				if (c.equals(allC)) {
					cChoises.add(c);
//					System.out.println("adding" + c.getId());
				}

			}
		}

		List<Choice> copyOfChoises = new ArrayList<Choice>(cChoises);

		boolean wasNotCorrect = false;
		boolean wasIncluded = false;
		int timeNeeded = (int)((new Date().getTime() - acctuallQuestion.getAskedTime().getTime())/ 1000 % 60);
		System.out.println("calculated time: " + timeNeeded);
		if (timeNeeded >= acctuallQuestion.getMaxTime())timeNeeded = (int)acctuallQuestion.getMaxTime();
		
		
		if (selected != null) {

			if (!cChoises.isEmpty()) {
				for (String s : selected) {
//					System.out.println(s);

					wasIncluded = false;
					for (Choice c : cChoises) {
//						System.out.println("ceking for choise" + c.getId());
						if (String.valueOf(c.getId()).equals(s)) {
							copyOfChoises.remove(c);
							wasIncluded = true;
//							System.out.println("Removing: " + s);
						}
					}
					if (!wasIncluded)
						wasNotCorrect = true;
				}

			}
		}

		System.out.println("was not Correct: " + wasNotCorrect);
		System.out.println("Choises: " + copyOfChoises);
		System.out.println("Is empty:" + copyOfChoises.isEmpty());
		System.out.println("____________________________");
		// cChoises.

		if (copyOfChoises.isEmpty() && !wasNotCorrect) {
			player1.incrementScoreInRound();
			player1.incrementTotalScore();
			player1.setStateOfQuestion(questionNumber - 1,
					QuestionState.CORRECT);
			player1.setTime(player1.getTime() + timeNeeded);
		} else {
			player1.setStateOfQuestion(questionNumber - 1,
					QuestionState.INCORRECT);
		}

		// Simulate PC Player
		this.simulateTaktik();
	}

	public void nextQuestion() {
		SimpleQuestion question = questionForCategory.get(questionNumber++);

		// save to asked question
		acctuallQuestion = new AskedQuestion(question);
	}

	public String getRoundWinerName() {
		return this.getRoundWinner().getName();
	}
	public String getTotalWinnerName(){
		return this.getTotalWinner().getName();
	}

	public void startQuiz(List<SimpleCategory> categories) {
		
		player1.resetBeforeGame();
		player2.resetBeforeGame();
		
		roundNumber = 0;
		this.categories = orderCategories(categories);
		System.out.println(categories);
		startNewRound();

	}

	public void startNewRound() {
		
		player1.setScoreInRound(0);
		player2.setScoreInRound(0);
		
		player1.resetQuestionState();
		player2.resetQuestionState();

		System.out
				.println("---------------------------------------------------------SSSSSSSSSSSPlayer1:"
						+ player1.getPlayerQuestions().size());
		System.out
				.println("---------------------------------------------------------SSSSSSSSSSS222Player:2"
						+ player2.getPlayerQuestions().size());
		System.out.println("# Round won player1: "
				+ this.getPlayer1().getNumberRoundWon());
		
		System.out.println("# Round won player2: "
				+ this.getPlayer2().getNumberRoundWon());

		questionNumber = 0;
		List<? extends Question> question = new ArrayList<Question>(categories
				.get(roundNumber++).getQuestions());
		questionForCategory = new ArrayList(question);

		questionForCategory = orderQuestions(questionForCategory);
		
		
		System.out.println("Player1: totalScore"+player1.getTotalScore());
		System.out.println("Player2: totalScore"+player2.getTotalScore());

	}

	private List<SimpleCategory> orderCategories(List<SimpleCategory> categories) {

		List<SimpleCategory> allCategories = new ArrayList<SimpleCategory>(
				categories);
		List<SimpleCategory> orderedCategories = new ArrayList<SimpleCategory>();

		Random rand = new Random();

		for (int i = 0; i < allCategories.size(); i++) {
			int r = rand.nextInt(allCategories.size() - i);
			orderedCategories.add(allCategories.get(r));
			allCategories.set(r,
					allCategories.get(allCategories.size() - 1 - i));
		}

		return orderedCategories;
	}

	private List<SimpleQuestion> orderQuestions(List<SimpleQuestion> allQuestion) {

		List<SimpleQuestion> all = new ArrayList<SimpleQuestion>(allQuestion);
		List<SimpleQuestion> orderedQuestions = new ArrayList<SimpleQuestion>();

		Random rand = new Random();

		for (int i = 0; i < 3; i++) {
			int r = rand.nextInt(all.size() - i);
			orderedQuestions.add(all.get(r));
			all.set(r, all.get(all.size() - 1 - i));
		}

		return orderedQuestions;
	}

	public void simulateTaktik() {
		Random rand = new Random();
		if (rand.nextDouble() < 0.50) {
			player2.incrementScoreInRound();
			player2.incrementTotalScore();
			player2.setStateOfQuestion(questionNumber - 1,
					QuestionState.CORRECT);
			int timeNeeded = rand.nextInt(38)+3;
			if(timeNeeded>acctuallQuestion.getMaxTime())timeNeeded = (int)acctuallQuestion.getMaxTime();  
			player2.setTime(player2.getTime()+timeNeeded);
			
		} else {
			player2.setStateOfQuestion(questionNumber - 1,
					QuestionState.INCORRECT);

		}

	}

	public void incrementScoreAfterRound() {
		System.out.println("player 1 has "+player1.getScoreInRound()+ " in round.");
		System.out.println("player 2 has "+player2.getScoreInRound()+ " in round.");
		System.out.println("player 1 has time "+player1.getTime()+ " sec total in round.");
		System.out.println("player 2 has time "+player2.getTime()+ " sec total in round.");
		System.out.println("winner of round is "+getRoundWinner());
		getRoundWinner().incrementRoundNumberWon();
	}

	public Player getRoundWinner() {
		
		if (player1.getScoreInRound() > player2.getScoreInRound()) {
			return player1;
		}
		else if(player1.getScoreInRound() == player2.getScoreInRound()){
			if(player1.getTime()>player2.getTime()){
				return player1;
			}
		}
		return player2;
	}
	//Juro
	public Player getTotalWinner(){
		if(player1.getNumberRoundWon()>player2.getNumberRoundWon())
			return player1;
		return player2;
		
	}
	//Juro

	public int getRoundNumber() {
		return roundNumber;
	}

	public void setRoundNumber(int roundNumber) {
		this.roundNumber = roundNumber;
	}

	public void incrementRoundNumber() {
		this.roundNumber++;
	}

	public int getQuestionNumber() {
		return questionNumber;
	}

	public void setQuestionNumber(int questionNumber) {
		this.questionNumber = questionNumber;
	}

	public void incrementQuestionNumber() {
		questionNumber++;
	}

	public Player getPlayer1() {
		return player1;
	}

	public void setPlayer1(Player player1) {
		this.player1 = player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public void setPlayer2(Player player1) {
		this.player2 = player1;
	}

	public List<SimpleCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<SimpleCategory> categories) {
		this.categories = categories;
	}

	public List<SimpleQuestion> getQuestionForCategory() {
		return questionForCategory;
	}

	public void setQuestionForCategory(List<SimpleQuestion> questionForCategory) {
		this.questionForCategory = questionForCategory;
	}

	public AskedQuestion getAcctuallQuestion() {
		return acctuallQuestion;
	}

	public void setAcctuallQuestion(AskedQuestion acctuallQuestion) {
		this.acctuallQuestion = acctuallQuestion;
	}

	public String getRequestDispatcherAction() {
		return requestDispatcherAction;
	}

	public void setRequestDispatcherAction(String requestDispatcherAction) {
		this.requestDispatcherAction = requestDispatcherAction;
	}

}
