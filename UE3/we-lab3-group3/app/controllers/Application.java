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

	@play.db.jpa.Transactional
	@Security.Authenticated(SessionSecured.class)
	public static Result startQuiz() {

		String userName = session().get("userName");
		session("questionNummer", "0");
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
		User computer = currentGame.getPlayers().get(1);
		
		player.setName(userName);
		
		currentGame.startNewRound();

		Round round = currentGame.getCurrentRound();// current round
		
		Question question = round.getCurrentQuestion(player);
		
		
		List<String> idList = new ArrayList<String>();
		for (int i = 0; i < question.getAllChoices().size(); i++) {
			idList.add("option" + (i + 1));
		}
		
		
		List<String> p1States = getStatesOfPlay(player, round, -1);
		List<String> p2States = getStatesOfPlay(computer, round, -1);

		return ok(quiz.render(question, idList, p1States, p2States));

	}
	

	@Security.Authenticated(SessionSecured.class)
	public static Result roundOver(){
		
		String userName = session().get("userName");
		
		QuizGame game = gameMap.get(userName);
	    
	    Round round = game.getCurrentRound();
	    
	    User player1 = game.getPlayers().get(0);
	    User computer = game.getPlayers().get(1);
	    
	    List<String> p1States = getStatesOfPlay(player1, round, 2);
		List<String> p2States = getStatesOfPlay(computer, round, 2);
		
		return ok(roundover.render(round.getRoundWinner().getName().toString(), game.getWonRounds(player1), game.getWonRounds(computer), game.getCurrentRoundCount(), p1States, p2States));
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
	    int questionNr = Integer.parseInt(session().get("questionNummer"));
		
		QuizGame game = gameMap.get(userName);
		
	    
	    Round round = game.getCurrentRound();
	    User player1 = game.getPlayers().get(0);
	    User computer = game.getPlayers().get(1);
	    
	    
	    Question currentQuestion = round.getCurrentQuestion(player1);
	    
	    List<Choice> questionChoices = currentQuestion.getAllChoices();
	    
	    List<Choice> selectedChoices = new ArrayList<Choice>();
	    
	    if(checkedVal != null) {
	    
		    for(int y = 0; y < checkedVal.length; y++) {
		    	for(int i = 0; i < questionChoices.size(); i++) {
			    	if(questionChoices.get(i).getText().equals(checkedVal[y])){
			    		selectedChoices.add(questionChoices.get(i));
			    	}
			    }
		    }
	    
	    }
	      
	    game.answerCurrentQuestion(player1, selectedChoices, 10);
	    
	    questionNr++;
	    
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
		
		session("questionNummer", Integer.toString(questionNr));
		
		List<String> p1States = getStatesOfPlay(player1, round, questionNr - 1);
		List<String> p2States = getStatesOfPlay(computer, round, questionNr - 1);
	    
	    return ok(quiz.render(nextQuestion, idList, p1States, p2States));

	}
	
	private static List<String> getStatesOfPlay(User user, Round round, int currentQuestionNr){
		List<String> states = new ArrayList<String>();
		for(int i = 0; i < 3; i++){
			if(i <= currentQuestionNr) {
				if(round.getAnswer(i, user).isCorrect()){
					states.add("correct");
				}
				else{
					states.add("incorrect");
				}
			}
			else{
				states.add("unknown");
			}
		}
		return states;
		
	}
	
	

}
