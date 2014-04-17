package at.ac.tuwien.big.we14.lab2.api.impl;

import java.util.ArrayList;
import java.util.List;

public class Player {

	private int score = 0;
	private long time = 0;
	
	public Player(){
		
	}

	private List<QuestionState> playerQuestions = new ArrayList<QuestionState>(3);

	public Player(int score, long time, List<QuestionState> playerQuestions) {
		super();
		this.score = score;
		this.time = time;
		this.playerQuestions = playerQuestions;
	}

	
	
	public void setStateOfQuestion(int questionNumber, QuestionState state) {
		playerQuestions.set(questionNumber, state);
	}
	
	public void resetQuestionState(){
		playerQuestions = new ArrayList<QuestionState>(3);
		for(int i = 0; i < playerQuestions.size(); i++){
			playerQuestions.add(QuestionState.UNKNOWN);
		}
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public void incrementScore(){
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

	public enum QuestionState {
		CORRECT, INCORRECT, UNKNOWN
	}

}
