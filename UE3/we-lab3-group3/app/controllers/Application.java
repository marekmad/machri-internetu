package controllers;

import java.util.*;


import at.ac.tuwien.big.we14.lab2.api.*;
import at.ac.tuwien.big.we14.lab2.api.impl.PlayQuizFactory;
import play.*;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

	public static QuizFactory factory;
	public static QuizGame game;
	
	public static int questionNr;

	@play.db.jpa.Transactional
	@Security.Authenticated(SessionSecured.class)
	public static Result quiz() {
		
		String userName = session().get("userName");
		
		models.User user = UserService.findByUsername(userName);
		
		factory = new PlayQuizFactory("conf/data.de.json", user);
		game = factory.createQuizGame();
		game.startNewRound(); // start new game/round
		questionNr = 0;
		
		Round round = game.getCurrentRound();// current round
		
		Question question = round.getCurrentQuestion(user);
		
		question.getAllChoices(); // all possible choices for a question
		round.answerCurrentQuestion(question.getAllChoices(), 5, user, factory);
		
		ArrayList<String> questions = new ArrayList<String>();
		for (Choice ch : question.getAllChoices()) {
			questions.add(ch.getText());
		}

		List<String> idList = new ArrayList<String>();
		for (int i = 0; i<questions.size();i++){
			 idList.add("option"+(i+1));
		}

		return ok(quiz.render(question.getText(), questions, question
			    .getCategory().getName(),idList));
		
	}

	public static Result nextQuestion() {
		
		User player = game.getPlayers().get(0); 
		Round round = game.getCurrentRound();// current round
		
		round.getAnswer(questionNr++, player);
		
		if(questionNr == 3) {
			return ok(quizover.render(""));
		}
		
		Question question = round.getCurrentQuestion(player);
		
		question.getAllChoices(); // all possible choices for a question
		round.answerCurrentQuestion(question.getAllChoices(), 5, player, factory);
		
		ArrayList<String> questions = new ArrayList<String>();
		for (Choice ch : question.getAllChoices()) {
			questions.add(ch.getText());
		}

		List<String> idList = new ArrayList<String>();
		for (int i = 0; i<questions.size();i++){
			 idList.add("option"+(i+1));
		}

		return ok(quiz.render(question.getText(), questions, question
			    .getCategory().getName(),idList));

	}

}
