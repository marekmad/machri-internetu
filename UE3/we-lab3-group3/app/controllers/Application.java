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
	
	static Map<String, QuizGame> gameMap = new HashMap<String, QuizGame>();
	
	static int roundNr = 0;

	@play.db.jpa.Transactional
	@Security.Authenticated(SessionSecured.class)
	public static Result startQuiz() {

		String userName = session().get("userName");
		models.Player user = new models.Player();
		
		if(!gameMap.containsKey(userName)) {
			user = UserService.findByUsername(userName);
			QuizFactory factory = new PlayQuizFactory("conf/data.de.json", user);
			QuizGame game = factory.createQuizGame();
			factory.createQuizGame();
			gameMap.put(userName, game);
		}
		
		
		
		QuizGame currentGame = gameMap.get(userName);
		
		User player = currentGame.getPlayers().get(0);
		
		player.setName(userName);
		
		currentGame.startNewRound();

		Round round = currentGame.getCurrentRound();// current round
		
		Question question = round.getCurrentQuestion(player);
		
		
		List<String> idList = new ArrayList<String>();
		for (int i = 0; i < question.getAllChoices().size(); i++) {
			idList.add("option" + (i + 1));
		}
		user.toString();

		return ok(quiz.render(question, idList));

	}
	

	@Security.Authenticated(SessionSecured.class)
	public static Result roundOver(){
		
		String userName = session().get("userName");
		
		QuizGame game = gameMap.get(userName);
	    
	    Round round = game.getCurrentRound();
		
		return ok(roundover.render(round.getRoundWinner().getName().toString(), 0, 0, game.getCurrentRoundCount()));
	}
	
	@Security.Authenticated(SessionSecured.class)
	public static Result quizOver() {
		
		String userName = session().get("userName");
		
		QuizGame game = gameMap.get(userName);
		
		Round round = game.getCurrentRound();
		
		User player = game.getPlayers().get(0);
		User computer = game.getPlayers().get(1);
	
		gameMap.remove(userName);
		
		return ok(quizover.render(game.getWinner().getName(), game.getWonRounds(player), game.getWonRounds(computer), player.getName(), computer.getName()));
		
	}

	@Security.Authenticated(SessionSecured.class)
	public static Result nextQuestion() {
		
		Map<String, String[]> map = request().body().asFormUrlEncoded();
	    String[] checkedVal = map.get("answer"); // get selected answers
	    
	    String userName = session().get("userName");
		
		QuizGame game = gameMap.get(userName);
		
	    
	    Round round = game.getCurrentRound();
	    User player1 = game.getPlayers().get(0);
	    
	    
	    Question currentQuestion = round.getCurrentQuestion(player1);
	    
	    List<Choice> questionChoices = currentQuestion.getAllChoices();
	    
	    List<Choice> selectedChoices = new ArrayList<Choice>();
	    
	    if(checkedVal != null){
	    
		    for(int y = 0; y < checkedVal.length; y++) {
		    	for(int i = 0; i < questionChoices.size(); i++){
			    	if(questionChoices.get(i).getText().equals(checkedVal[y])){
			    		selectedChoices.add(questionChoices.get(i));
			    	}
			    }
		    }
	    
	    }
	    

		    
	    game.answerCurrentQuestion(player1, selectedChoices, 10);
	    
	    if(game.isRoundOver()) {
	    	
	    	if(game.isGameOver()) {
	    		return redirect(routes.Application.quizOver());
	    	}
	    	
	    	return redirect(routes.Application.roundOver());
	    }
	 
	    Question nextQuestion = round.getCurrentQuestion(player1);
	    
	    List<String> idList = new ArrayList<String>();
		for (int i = 0; i < nextQuestion.getAllChoices().size(); i++) {
			idList.add("option" + (i + 1));
		}
		
		
	    
	    return ok(quiz.render(nextQuestion, idList));

	}
	
	

}
