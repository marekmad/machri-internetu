package controllers;

import static play.data.Form.form;

import java.util.*;

import models.AskedQuestion;
import at.ac.tuwien.big.we14.lab2.api.*;
import at.ac.tuwien.big.we14.lab2.api.impl.PlayQuizFactory;
import play.*;
import play.api.data.Form;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

	public static QuizFactory factory;
	public static QuizGame game;
	
	static int qNumber = 0;
	

	@play.db.jpa.Transactional
	@Security.Authenticated(SessionSecured.class)
	public static Result startQuiz() {

		String userName = session().get("userName");

		models.User user = UserService.findByUsername(userName);

		factory = new PlayQuizFactory("conf/data.de.json", user);
		game = factory.createQuizGame();
		game.startNewRound(); // start new game/round

		Round round = game.getCurrentRound();// current round

		Question question = round.getCurrentQuestion(user);

		List<String> idList = new ArrayList<String>();
		for (int i = 0; i < question.getAllChoices().size(); i++) {
			idList.add("option" + (i + 1));
		}

		return ok(quiz.render(question, idList));

	}
	
	public static Result newRound() {
		
		game.startNewRound();
		Round round = game.getCurrentRound();// current round
		
		User user = game.getPlayers().get(0);
		
		Question question = round.getCurrentQuestion(user);

		question.getAllChoices(); // all possible choices for a question
		round.answerCurrentQuestion(question.getAllChoices(), 5, user, factory);

		

		List<String> idList = new ArrayList<String>();
		for (int i = 0; i < question.getAllChoices().size(); i++) {
			idList.add("option" + (i + 1));
		}

		return ok(quiz.render(question, idList));
		
	}
	
	public static Result roundOver(){
		
		Round round = game.getCurrentRound();
		
		return ok(roundover.render(round.getRoundWinner().getName(), 0, 0, game.getCurrentRoundCount()));
	}

	public static Result nextQuestion() {
		
		Map<String, String[]> map = request().body().asFormUrlEncoded();
	    String[] checkedVal = map.get("answer"); // get selected answers
	    

	    
	    Round round = game.getCurrentRound();
	    User player1 = game.getPlayers().get(0);
	    Question currentQuestion = round.getCurrentQuestion(player1);
	    
	    List<Choice> questionChoices = currentQuestion.getAllChoices();
	    
	    List<Choice> selectedChoices = new ArrayList<Choice>();
	    
	    for(int y = 0; y < checkedVal.length; y++) {
	    	for(int i = 0; i < questionChoices.size(); i++){
		    	if(questionChoices.get(i).getText().equals(checkedVal[y])){
		    		selectedChoices.add(questionChoices.get(i));
		    	}
		    }
	    }
		    
	    round.answerCurrentQuestion(selectedChoices, 10, player1, factory);
	    
	    if(game.isRoundOver()) {
	    	return redirect(routes.Application.roundOver());
	    }
	    
	    
	    Question nextQuestion = round.getCurrentQuestion(player1);
	    
	    
//	    if(qNumber++ == 2) {
//	    	return ok(round.getAnswer(0, player1).getChoices().toString() + '\n' + 
//	    			round.getAnswer(1, player1).getChoices().toString() + '\n' + 
//	    			round.getAnswer(2, player1).getChoices().toString() + '\n' +
//	    			round.getQuestions().size() + round.areAllQuestionsAnswered());
//	    	
//	    }
	   
	    
	    
	    List<String> idList = new ArrayList<String>();
		for (int i = 0; i < nextQuestion.getAllChoices().size(); i++) {
			idList.add("option" + (i + 1));
		}
		
		
	    
	    return ok(quiz.render(nextQuestion, idList));
	    
//		askedQuestion.chosenAnsweres = Arrays.asList(checkedVal);
//	    
//	    String aa = askedQuestion.chosenAnsweres.get(1);
//		at.ac.tuwien.big.we14.lab2.api.User player = game.getPlayers().get(0);
//		Round round = game.getCurrentRound();// current round
//
//		round.getAnswer(questionNr++, player);
//
//		if (questionNr == 3) {
//
//			return ok(roundover.render(round.getRoundWinner().getName(), 4, 4,1));
//		}
//
//		Question question = round.getCurrentQuestion(player);
//
//		question.getAllChoices(); // all possible choices for a question
//		round.answerCurrentQuestion(question.getAllChoices(), 5, player,
//				factory);
//
////		ArrayList<String> questions = new ArrayList<String>();
////		for (Choice ch : question.getAllChoices()) {
////			questions.add(ch.getText());
////		}
//

//
//		return ok(quiz.render(question.getText(), question, question
//				.getCategory().getName(), idList));

	}
	
	

}
