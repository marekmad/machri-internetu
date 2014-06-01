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
import twitter.ITwitterClient;
import twitter.TwitterClient;
import twitter.TwitterStatusMessage;
import views.html.quiz.index;
import views.html.quiz.quiz;
import views.html.quiz.quizover;
import views.html.quiz.roundover;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//imports for soap service
import java.io.ByteArrayOutputStream;

import javax.xml.namespace.QName;
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
		List<Category> allCategories = QuizDAO.INSTANCE
				.findEntities(Category.class);
		Logger.info("Start game with " + allCategories.size() + " categories.");
		QuizGame game = new QuizGame(allCategories, user());
		game.startNewRound();
		cacheGame(game);
		return game;
	}

	private static String dataFilePath() {
		return Play.application().configuration()
				.getString("questions.filePath");
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
		String UUID = "";

		if (game != null && isGameOver(game)) {
			game.setMessage("Post to Hightscoreboard and Twitter successful!");
			// _________________________________________ SOAP
			try {
				// Create SOAP Connection
				SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory
						.newInstance();
				SOAPConnection soapConnection = soapConnectionFactory
						.createConnection();

				// Send SOAP Message to SOAP Server

				String url = "http://playground.big.tuwien.ac.at:8080/highscore/PublishHighScoreService?wsdl";
				SOAPMessage soapResponse = soapConnection.call(
						createSOAPRequest(game), url);

				// Process the SOAP Response
				// printSOAPResponse(soapResponse);
				// TODO get UID from soapResponse

				QName bodyName = new QName(
						"http://big.tuwien.ac.at/we/highscore/data",
						"HighScoreResponse", "d");

				SOAPBody sb = soapResponse.getSOAPBody();
				java.util.Iterator iterator = sb.getChildElements(bodyName);
				while (iterator.hasNext()) {

					SOAPBodyElement bodyElement = (SOAPBodyElement) iterator
							.next();
					UUID = bodyElement.getValue();
					Logger.info("UUID: " + UUID); 
				}

				soapConnection.close();
				Logger.info("Post to Highscoreboard with UUID: " + UUID + " successful" );
				// java.netConnectException

			} catch (Exception e) {
				game.setMessage("Cannot post - connection problem");
				Logger.info("Post to Highscoreboard with UUID: " + UUID + " failed!!!" );
			}

			try {
				if(UUID.equals("")) UUID = "Hightscoreboard problem";
				TwitterStatusMessage StatusMessage = new TwitterStatusMessage(
						"Gruppe 6", UUID, Calendar.getInstance().getTime());
				ITwitterClient TClient = new TwitterClient();
				TClient.publishUuid(StatusMessage);
				Logger.info("Post to Twitter with UUID: " + UUID + " successful" );

			} catch (Exception e) {
				if(game.getMessage().equals(""))
				game.setMessage("Post on Highscoreboard successful, Twitter connection problem");
				else game.setMessage("Post to Highscoreboard and Twitter failed!");
				Logger.error("Post to Twitter with UUID: " + UUID + " failed!!!" );
			}

			// _________________________________________ SOAP AND TWITTER
			
			return ok(quizover.render(game));
		} else {
			return badRequest(Messages.get("quiz.no-end-result"));
		}
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

	// creates SOAPRequest
	private static SOAPMessage createSOAPRequest(QuizGame game)
			throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();

		String serverURI = "http://big.tuwien.ac.at/we/highscore/data";

		// SOAP Envelope
		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration("data", serverURI);

		// SOAP Body
		SOAPBody soapBody = envelope.getBody();
		SOAPElement highScoreNode = soapBody.addChildElement(
				"HighScoreRequest", "data");
		SOAPElement userKeyNode = highScoreNode.addChildElement("UserKey",
				"data");
		userKeyNode.setValue("rkf4394dwqp49x");

		SOAPElement quiz = highScoreNode.addChildElement("quiz");

		SOAPElement users = quiz.addChildElement("users");

		SOAPElement user1 = users.addChildElement("user");

		String p1Status;
		String p2Status;
		if (game.getWinner().equals(game.getPlayers().get(0))) {
			p1Status = "winner";
			p2Status = "loser";

		} else {
			p1Status = "loser";
			p2Status = "winner";

		}

		List<QuizUser> list = game.getPlayers();

//		for (QuizUser u : list) {
//			System.out.println(u.getId() + "gender: " + u.getFirstName());
//
//		}

		String genderP1;
		if (game.getPlayers().get(0).getGender() != null)
			genderP1 = game.getPlayers().get(0).getGender().toString();
		else
			genderP1 = "male";

		String firstnameP1;
		if (game.getPlayers().get(0).getGender() != null)
			firstnameP1 = game.getPlayers().get(0).getFirstName();
		else
			firstnameP1 = "nicht angegeben";

		String lastnameP1;
		if (game.getPlayers().get(0).getGender() != null)
			lastnameP1 = game.getPlayers().get(0).getLastName();
		else
			lastnameP1 = "nicht angegeben";

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		String birthDateP1 = "1980-01-01";
		if (game.getPlayers().get(0).getBirthDate() != null)
			birthDateP1 = dateFormat.format(game.getPlayers().get(0)
					.getBirthDate());

		user1.setAttribute("name", p1Status);
		user1.setAttribute("gender", genderP1);
		SOAPElement passwd1Element = user1.addChildElement("password");
		SOAPElement firstname1Element = user1.addChildElement("firstname");
		firstname1Element.setValue(firstnameP1);
		SOAPElement lastname1Element = user1.addChildElement("lastname");
		lastname1Element.setValue(lastnameP1);
		SOAPElement birthdate1Element = user1.addChildElement("birthdate");

		birthdate1Element.setValue(birthDateP1);

		SOAPElement user2 = users.addChildElement("user");

		String genderP2;
		if (game.getPlayers().get(1).getGender() != null)
			genderP2 = game.getPlayers().get(1).getGender().toString();
		else
			genderP2 = "male";

		String firstnameP2;
		if (game.getPlayers().get(1).getGender() != null)
			firstnameP2 = game.getPlayers().get(1).getFirstName();
		else
			firstnameP2 = "nicht angegeben";

		String lastnameP2;
		if (game.getPlayers().get(1).getGender() != null)
			lastnameP2 = game.getPlayers().get(1).getLastName();
		else
			lastnameP2 = "nicht angegeben";

		String birthDateP2 = "1982-01-01";
		if (game.getPlayers().get(1).getBirthDate() != null)
			birthDateP2 = dateFormat.format(game.getPlayers().get(1)
					.getBirthDate());

		user2.setAttribute("name", p2Status);
		user2.setAttribute("gender", genderP2);
		SOAPElement passwd2Element = user2.addChildElement("password");
		SOAPElement firstname2Element = user2.addChildElement("firstname");
		firstname2Element.setValue(firstnameP2);
		SOAPElement lastname2Element = user2.addChildElement("lastname");
		lastname2Element.setValue(lastnameP2);
		SOAPElement birthdate2Element = user2.addChildElement("birthdate");

		birthdate2Element.setValue(birthDateP2);

		soapMessage.saveChanges();
		// System.out.println(messa);

		//message output
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		soapMessage.writeTo(out);
//		String strMsg = new String(out.toByteArray());
//		System.out.println(strMsg);

		return soapMessage;

	}

}
