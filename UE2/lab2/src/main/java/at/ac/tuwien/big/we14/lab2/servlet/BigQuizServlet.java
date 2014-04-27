package at.ac.tuwien.big.we14.lab2.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import at.ac.tuwien.big.we14.lab2.api.impl.AskedQuestion;
import at.ac.tuwien.big.we14.lab2.api.impl.Game;
import at.ac.tuwien.big.we14.lab2.api.impl.ServletQuizFactory;
import at.ac.tuwien.big.we14.lab2.api.impl.SimpleCategory;
import at.ac.tuwien.big.we14.lab2.api.impl.SimpleQuestion;
import at.ac.tuwien.big.we14.lab2.api.Category;
import at.ac.tuwien.big.we14.lab2.api.Choice;
import at.ac.tuwien.big.we14.lab2.api.Question;
import at.ac.tuwien.big.we14.lab2.api.QuestionDataProvider;
import at.ac.tuwien.big.we14.lab2.api.QuizFactory;
import at.ac.tuwien.big.we14.lab2.api.impl.ServletQuizFactory;
import at.ac.tuwien.big.we14.lab2.api.impl.SimpleQuestion;

public class BigQuizServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {

		super.init();
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String action = request.getParameter("action");

		
		System.out.println("doPost "+action);
		
		if (action != null) {
			if (action.equals("newGame")) {

				Game game = null;

				game = (Game) request.getSession().getAttribute("game"); 
				
				System.out.println("starting new game "+game);
				
				if (game == null || (game != null && game.getRequestDispatcherAction().equals(Game.FINISH_JSP))) {
				// disable generating new game on refresh, BigQuizServlet?action=newGame
					game = new Game();
					startGame(game);
					game.nextQuestion();
					game.getAcctuallQuestion().setAskedTime(new Date());
				} 

				game.setRequestDispatcherAction(Game.QUESTION_JSP);// next page question.jsp

				HttpSession session = request.getSession(true);

				session.setAttribute("game", game);
				
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("question.jsp");
				if (dispatcher != null) {
					dispatcher.forward(request, response);
				}
				
				

			}
			
			if (action.equals("submitAnswer")) {

				String[] selected = request.getParameterValues("opts");

				Game game = (Game) request.getSession().getAttribute("game"); // get game parameter from our session

				if (game.getQuestionNumber() == 3) {

					System.out.println("incrementing score after round "
							+ game.getRoundNumber());

					if (game.getRoundNumber() == 5) {

						if (game.getRequestDispatcherAction().equals(
								Game.QUESTION_JSP)) {
							// finish.jsp can come only after question.jsp page, "disables refresh" finish.jsp
							game.validateAnswer(selected);
							game.incrementScoreAfterRound();
						}

						System.out.println("end of game: ");
						System.out.println("p1 score: "
								+ game.getPlayer1().getNumberRoundWon());
						System.out.println("p2 score: "
								+ game.getPlayer2().getNumberRoundWon());

						game.setRequestDispatcherAction(Game.FINISH_JSP); // next page finish.jsp

						HttpSession session = request.getSession(true);
						session.setAttribute("game", game);

						RequestDispatcher dispatcher = request
								.getRequestDispatcher("/finish.jsp");

						if (dispatcher != null) {
							dispatcher.forward(request, response);

						}
					} else {

						if (game.getRequestDispatcherAction().equals(
								Game.QUESTION_JSP)) {
							// roundcomplete.jsp can come only after question.jsp page, "disables refresh" roundcomplete.jsp
							game.validateAnswer(selected);
							game.incrementScoreAfterRound();
						}

						game.setRequestDispatcherAction(Game.ROUND_COMPLETE_JSP); // next page roundcomplete.jsp

						HttpSession session = request.getSession(true);
						session.setAttribute("game", game);

						RequestDispatcher dispatcher = request
								.getRequestDispatcher("/roundcomplete.jsp");

						if (dispatcher != null) {
							dispatcher.forward(request, response);
						}
					}

				} else {

					game.validateAnswer(selected);

					HttpSession session = request.getSession(true);

					game.nextQuestion();

					game.setRequestDispatcherAction(Game.QUESTION_JSP); // next page question.jsp

					session.setAttribute("game", game);
					game.getAcctuallQuestion().setAskedTime(new Date());
					RequestDispatcher dispatcher = request
							.getRequestDispatcher("/question.jsp");

					if (dispatcher != null) {
						dispatcher.forward(request, response);
					}
				}
			}
		}
		

	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String action = request.getParameter("action");

		System.out.println("Action: " + action);

		if (action != null) {

			if (action.equals("newGame")) {

				Game game = null;

				game = (Game) request.getSession().getAttribute("game"); 
				
				System.out.println("starting new game "+game);
				
				if (game == null || (game != null && game.getRequestDispatcherAction().equals(Game.FINISH_JSP))) {
				// disable generating new game on refresh, BigQuizServlet?action=newGame
					game = new Game();
					startGame(game);
					game.nextQuestion();
					game.getAcctuallQuestion().setAskedTime(new Date());
				} 

				game.setRequestDispatcherAction(Game.QUESTION_JSP);// next page question.jsp

				HttpSession session = request.getSession(true);

				session.setAttribute("game", game);

			}

			if (action.equals("newRound")) {

				Game game = (Game) request.getSession().getAttribute("game");

				if (game.getRequestDispatcherAction().equals(
						Game.ROUND_COMPLETE_JSP)) {
					//disable generating new round on refresh, BigQuizServlet?action=newRound
					game.startNewRound();
					game.nextQuestion();
					game.getAcctuallQuestion().setAskedTime(new Date());
				}

				game.setRequestDispatcherAction(Game.QUESTION_JSP);// next page question.jsp

				HttpSession session = request.getSession(true);
				session.setAttribute("game", game);

				System.out.println("starting new round "
						+ game.getRoundNumber());
			}

			RequestDispatcher dispatcher = request
					.getRequestDispatcher("question.jsp");
			if (dispatcher != null) {
				dispatcher.forward(request, response);
			}

		}
		else {
			//send to start.jsp 
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("/start.jsp");
			
			if (dispatcher != null) {
				System.out.println("");
				dispatcher.forward(request, response);
			}
			
			
		}

	}

	public void startGame(Game game) {
		ServletContext servletContext = getServletContext();
		QuizFactory factory = ServletQuizFactory.init(servletContext);
		QuestionDataProvider provider = factory.createQuestionDataProvider();
		List<? super Category> categories = provider.loadCategoryData();
		List<SimpleCategory> cats = new ArrayList(categories);
		game.startQuiz(cats);
		System.out.println("starting new game");
	}

}