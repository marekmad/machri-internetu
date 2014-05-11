package controllers;

import static play.data.Form.form;

import java.util.*;

import models.AskedQuestion;
import models.User;
import at.ac.tuwien.big.we14.lab2.api.*;
import at.ac.tuwien.big.we14.lab2.api.impl.PlayQuizFactory;
import play.*;
import play.api.data.Form;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

	public static QuizFactory factory;
	public static QuizGame game;

	public static int questionNr;
	
	final static play.data.Form<AskedQuestion> checkedAnsweres = form(AskedQuestion.class);

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

		

		List<String> idList = new ArrayList<String>();
		for (int i = 0; i < question.getAllChoices().size(); i++) {
			idList.add("option" + (i + 1));
		}
		
		
		
		
		
		


		return ok(quiz.render(question.getText(), question, question
				.getCategory().getName(), idList));

	}

	public static Result nextQuestion() {
		
		play.data.Form<AskedQuestion> checkedAnswere = form(AskedQuestion.class).bindFromRequest();
		AskedQuestion askedQuestion = checkedAnswere.get();
		Map<String, String[]> map = request().body().asFormUrlEncoded();
	    String[] checkedVal = map.get("answer"); // get selected topics
		askedQuestion.chosenAnseres = Arrays.asList(checkedVal);
	    
	    String aa=askedQuestion.chosenAnseres.get(1);
		at.ac.tuwien.big.we14.lab2.api.User player = game.getPlayers().get(0);
		Round round = game.getCurrentRound();// current round

		round.getAnswer(questionNr++, player);

		if (questionNr == 3) {

			return ok(roundover.render(round.getRoundWinner().getName(), 4, 4,1));
		}

		Question question = round.getCurrentQuestion(player);

		question.getAllChoices(); // all possible choices for a question
		round.answerCurrentQuestion(question.getAllChoices(), 5, player,
				factory);

//		ArrayList<String> questions = new ArrayList<String>();
//		for (Choice ch : question.getAllChoices()) {
//			questions.add(ch.getText());
//		}

		List<String> idList = new ArrayList<String>();
		for (int i = 0; i < question.getAllChoices().size(); i++) {
			idList.add("option" + (i + 1));
		}

		return ok(quiz.render(question.getText(), question, question
				.getCategory().getName(), idList));

	}
	
	

}
