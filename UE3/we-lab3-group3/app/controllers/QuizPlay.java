package controllers;

import at.ac.tuwien.big.we14.lab2.api.*;
import at.ac.tuwien.big.we14.lab2.api.impl.*;

import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Security;
import play.mvc.Result;


public class QuizPlay extends Controller {
	
	int questionNummer = 0;
	int roundNummber = 0;
	
//	@Transactional
//	@Security.Authenticated(SessionSecured.class)
//	public static Result nextQuestion() {
//		
//		
//		String userName = session().get("userName");
//		
//		models.User user = UserService.findByUsername(userName);
//		
//		if(user != null) {
//			// user must implement Interface User
//			QuizFactory factory = new PlayQuizFactory("conf/data.de.json", user);
//			QuizGame game = factory.createQuizGame();
//			game.startNewRound(); // start new game/round
//			
//			
//			game.getPlayers().get(0); // get human player
//			
//			Round round = game.getCurrentRound();// current round
//			round.getAnswer(questionNr, user); // answer of question nr <questionNr> of <user>
//			Question question = round.getCurrentQuestion(user); // current question of user
//			question.getAllChoices(); // all possible choices for a question
//			round.answerCurrentQuestion(user, choices, time); // user with chosen answers and time left (also automatically answers the question for the computer opponent)
//			
//			round.getRoundWinner(); // winner of round or null if no winner exists yet
//			game.isRoundOver(); // check if round is over
//			game.getWonRounds(user); // number of rounds won by the given user
//			game.isGameOver(); // check if game is over
//			game.getWinner(); // winner of round or null if no winner exists yet
//		}
//	}
	
}
