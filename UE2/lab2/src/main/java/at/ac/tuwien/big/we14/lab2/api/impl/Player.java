package at.ac.tuwien.big.we14.lab2.api.impl;

import java.util.ArrayList;
import java.util.List;

public class Player {

	private int score = 0;
	private int roundNumberWon = 0;
	private long time = 0;
	private String name;

	public Player(String name) {
		this.name = name;
	}

	private List<QuestionState> playerQuestions = new ArrayList<QuestionState>(
			3);

	public Player(int score, long time, List<QuestionState> playerQuestions) {
		super();
		this.score = score;
		this.time = time;
		this.playerQuestions = playerQuestions;
	}

	public void setStateOfQuestion(int questionNumber, QuestionState state) {
		playerQuestions.set(questionNumber, state);
	}

	public void resetQuestionState() {
		playerQuestions = new ArrayList<QuestionState>(3);
		for (int i = 0; i < 3; i++) {
			playerQuestions.add(QuestionState.UNKNOWN);
		}
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void incrementScore() {
		score++;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public List<QuestionState> getPlayerQuestions() {
		return playerQuestions;
	}

	public void setPlayerQuestions(List<QuestionState> playerQuestions) {
		this.playerQuestions = playerQuestions;
	}
	
	public void incrementRoundNumberWon(){
		roundNumberWon++;
	}
	
	public int getNumberRoundWon(){
		return this.roundNumberWon;
	}

	public String getQuestionState(int qN) {
		if (playerQuestions.get(qN).equals(QuestionState.CORRECT)) {
			return "correct";
		} else if (playerQuestions.get(qN).equals(QuestionState.INCORRECT)) {
			return "incorrect";
		}
		return "unknown";
	}
	
	public String getName(){
		return name;
	}

	public enum QuestionState {
		CORRECT, INCORRECT, UNKNOWN
	}

}
