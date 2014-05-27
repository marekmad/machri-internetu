package controllers;

import models.*;
import play.Logger;
import play.Play;
import play.api.Application;
import play.api.cache.Cache;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import scala.Option;
import views.html.quiz.index;
import views.html.quiz.quiz;
import views.html.quiz.quizover;
import views.html.quiz.roundover;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//imports for soap service
import java.io.ByteArrayOutputStream;

import javax.xml.soap.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

@Security.Authenticated(Secured.class)
public class Quiz extends Controller {

	public static Result index() {
		return ok(index.render());
	}

	@play.db.jpa.Transactional(readOnly = true)
	public static Result newGame() {
		createNewGame();
		return redirect(routes.Quiz.question());
	}

	@play.db.jpa.Transactional(readOnly = true)
	private static QuizGame createNewGame() {
		List<Category> allCategories = QuizDAO.INSTANCE.findEntities(Category.class);
		Logger.info("Start game with " + allCategories.size() + " categories.");
		QuizGame game = new QuizGame(allCategories);
		game.startNewRound();
		cacheGame(game);
		return game;
	}

	private static String dataFilePath() {
		return Play.application().configuration().getString("questions.filePath");
	}

	private static QuizUser user() {
		String userId = Secured.getAuthentication(session());
		return QuizDAO.INSTANCE.findById(Long.valueOf(userId));
	}

	@play.db.jpa.Transactional(readOnly = true)
	public static Result question() {
		QuizGame game = cachedGame();
		if (currentQuestion(game) != null) {
			return ok(quiz.render(game));
		} else {
			return badRequest(Messages.get("quiz.no-current-question"));
		}
	}

	@Transactional(readOnly = true)
	private static Question currentQuestion(QuizGame game) {
		if (game != null && game.getCurrentRound() != null) {
			QuizUser user = game.getPlayers().get(0);
			return game.getCurrentRound().getCurrentQuestion(user);
		} else {
			return null;
		}
	}

	@play.db.jpa.Transactional(readOnly = true)
	public static Result addAnswer() {
		QuizGame game = cachedGame();
		Question question = currentQuestion(game);
		if (question != null) {
			processAnswerIfSent(game);
			return redirectAccordingToGameState(game);
		} else {
			return badRequest(Messages.get("quiz.no-current-question"));
		}
	}

	@Transactional
	private static void processAnswerIfSent(QuizGame game) {
		DynamicForm form = Form.form().bindFromRequest();
		QuizUser user = game.getPlayers().get(0);
		Question question = game.getCurrentRound().getCurrentQuestion(user);
		int sentQuestionId = Integer.valueOf(form.data().get("questionid"));
		if (question.getId() == sentQuestionId) {
			List<Choice> choices = obtainSelectedChoices(form, question);
			long time = Long.valueOf(form.get("timeleft"));
			game.answerCurrentQuestion(user, choices, time);
		}
	}

	@Transactional
	private static List<Choice> obtainSelectedChoices(DynamicForm form,
			Question question) {
		Map<String, String> formData = form.data();
		List<Choice> choices = new ArrayList<Choice>();
		int i = 0;
		String chosenId = null;
		while ((chosenId = formData.get("choices[" + i + "]")) != null) {
			Choice choice = getChoiceById(Integer.valueOf(chosenId), question);
			if (choice != null) {
				choices.add(choice);
			}
			i++;
		}
		return choices;
	}

	private static Choice getChoiceById(int id, Question question) {
		for (Choice choice : question.getChoices())
			if (id == choice.getId())
				return choice;
		return null;
	}

	private static Result redirectAccordingToGameState(QuizGame game) {
		if (isRoundOver(game)) {
			return redirect(routes.Quiz.roundResult());
		} else if (isGameOver(game)) {
			return redirect(routes.Quiz.endResult());
		} else {
			return redirect(routes.Quiz.question());
		}
	}

	private static boolean isGameOver(QuizGame game) {
		return game.isRoundOver() && game.isGameOver();
	}

	private static boolean isRoundOver(QuizGame game) {
		return game.isRoundOver() && !game.isGameOver();
	}

	private static void cacheGame(QuizGame game) {
		Cache.set(gameId(), game, 3600, application());
	}

	@play.db.jpa.Transactional(readOnly = true)
	public static Result roundResult() {
		QuizGame game = cachedGame();
		if (game != null && isRoundOver(game)) {
			return ok(roundover.render(game));
		} else {
			return badRequest(Messages.get("quiz.no-round-result"));
		}
	}

	@play.db.jpa.Transactional(readOnly = true)
	public static Result endResult() {
		QuizGame game = cachedGame();
		
	
		if (game != null && isGameOver(game)) {
			
			//_________________________________________ SOAP
			 try {
		            // Create SOAP Connection
		            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
		            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

		            // Send SOAP Message to SOAP Server
		           
		            String url = "http://playground.big.tuwien.ac.at:8080/highscore/PublishHighScoreService?wsdl";
		            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(game), url);
		            

		            // Process the SOAP Response
		            printSOAPResponse(soapResponse);

		            soapConnection.close();
		        } catch (Exception e) {
		            System.err.println("Error occurred while sending SOAP Request to Server");
		            e.printStackTrace();
		        }
			 
			//_________________________________________ SOAP
			
			
			
			
			return ok(quizover.render(game));
		} else {
			return badRequest(Messages.get("quiz.no-end-result"));
		}
	}
	
	 /**
     * Method used to print the SOAP Response delete after it is not used anymore
     */
    private static void printSOAPResponse(SOAPMessage soapResponse) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Source sourceContent = soapResponse.getSOAPPart().getContent();
       //System.out.println(soapResponse.);
        
        // TODO get response..
        
       // .getAttribute("xmlns:ns3")
        
        System.out.print("\nResponse SOAP Message = ");
        StreamResult result = new StreamResult(System.out);
        transformer.transform(sourceContent, result);
    }

	@play.db.jpa.Transactional(readOnly = true)
	public static Result newRound() {
		QuizGame game = cachedGame();
		if (game != null && isRoundOver(game)) {
			game.startNewRound();
			return redirect(routes.Quiz.question());
		} else {
			return badRequest(Messages.get("quiz.no-round-ended"));
		}
	}

	private static QuizGame cachedGame() {
		Option<Object> option = Cache.get(gameId(), application());
		if (option.isDefined() && option.get() instanceof QuizGame) {
			return (QuizGame) option.get();
		} else {
			return createNewGame();
		}
	}

	private static String gameId() {
		return "game." + uuid();
	}

	private static String uuid() {
		String uuid = session("uuid");
		if (uuid == null) {
			uuid = UUID.randomUUID().toString();
			session("uuid", uuid);
		}
		return uuid;
	}

	private static Application application() {
		return Play.application().getWrappedApplication();
	}
	
	
	//creates SOAPRequest
	 private static SOAPMessage createSOAPRequest(QuizGame game) throws Exception {
		 	
		 
	        MessageFactory messageFactory = MessageFactory.newInstance();
	        SOAPMessage soapMessage = messageFactory.createMessage();
	        SOAPPart soapPart = soapMessage.getSOAPPart();

	        String serverURI = "http://big.tuwien.ac.at/we/highscore/data";

	        // SOAP Envelope
	        SOAPEnvelope envelope = soapPart.getEnvelope();
	        envelope.addNamespaceDeclaration("data", serverURI);

	       
	        
	        

	        // SOAP Body
	        SOAPBody soapBody = envelope.getBody();
	        SOAPElement highScoreNode = soapBody.addChildElement("HighScoreRequest", "data");
	        SOAPElement userKeyNode  = highScoreNode.addChildElement("UserKey", "data");
	        userKeyNode.setValue("rkf4394dwqp49x");
	       
	        SOAPElement quiz = highScoreNode.addChildElement("quiz");
	        
	        SOAPElement users = quiz.addChildElement("users");
	        
	        SOAPElement user1 = users.addChildElement("user");
	        
	        String p1Status;
	        String p2Status;
	       if( game.getWinner().equals(game.getPlayers().get(0))){
	    	   p1Status = "winner";
	    	   p2Status = "loser";
	    	   
	       }
	       else
	       {
	    	   p1Status = "loser";
	    	   p2Status = "winner";
	    	   
	       }
	        
	        
	        user1.setAttribute("name", p1Status);
	        user1.setAttribute("gender", game.getPlayers().get(0).getGender().toString());
	        SOAPElement passwd1Element = user1.addChildElement("password"); 
	        SOAPElement firstname1Element = user1.addChildElement("firstname");
	        firstname1Element.setValue(game.getPlayers().get(0).getFirstName());
	        SOAPElement lastname1Element = user1.addChildElement("lastname");
	        lastname1Element.setValue(game.getPlayers().get(0).getLastName());
	        SOAPElement birthdate1Element = user1.addChildElement("birthdate");
	        
	        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	        	        
	        birthdate1Element.setValue(dateFormat.format(game.getPlayers().get(0).getBirthDate()));
	        
	        SOAPElement user2 = users.addChildElement("user");
	        
	        user2.setAttribute("name", p2Status);
	        user2.setAttribute("gender", game.getPlayers().get(1).getGender().toString());
	        SOAPElement passwd2Element = user2.addChildElement("password"); 
	        SOAPElement firstname2Element = user2.addChildElement("firstname");
	        firstname2Element.setValue(game.getPlayers().get(1).getFirstName());
	        SOAPElement lastname2Element = user2.addChildElement("lastname");
	        lastname2Element.setValue(game.getPlayers().get(1).getLastName());
	        SOAPElement birthdate2Element = user2.addChildElement("birthdate");
	        
	        
	        birthdate2Element.setValue(dateFormat.format(game.getPlayers().get(1).getBirthDate()));
	     
	        soapMessage.saveChanges();
	        //System.out.println(messa);
	        
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        soapMessage.writeTo(out);
	        String strMsg = new String(out.toByteArray());
	        System.out.println(strMsg);
	        
	    
	        
	        return soapMessage;
	        
	    }

}
