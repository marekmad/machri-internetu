package at.ac.tuwien.big.we14.lab2.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
	
	private Game game = new Game();

	@Override
	public void init() throws ServletException {
		
		super.init();
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String[] selected = request.getParameterValues("opts");
		
		game.validateAnswer(selected);

		if (game.getQuestionNumber() == 3) {

			System.out.println("incrementing score after round "+game.getRoundNumber());
			game.incrementScoreAfterRound();
			
			if (game.getRoundNumber() == 5) {
				
				
				System.out.println("end of game: ");
				System.out.println("p1 score: " + game.getPlayer1().getNumberRoundWon());
				System.out.println("p2 score: " + game.getPlayer2().getNumberRoundWon());
				
				
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("/finish.html");

				if (dispatcher != null) {
					dispatcher.forward(request, response);

				}
			} else {
				
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("/roundcomplete.jsp");

				if (dispatcher != null) {
					dispatcher.forward(request, response);
				}
			}

		} else {

			HttpSession session = request.getSession(true);

			game.nextQuestion();
			
			session.setAttribute("game", game);
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("/question.jsp");

			if (dispatcher != null) {
				dispatcher.forward(request, response);
			}
		}

	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String action = request.getParameter("action");

		System.out.println("Action: " + action);

		if (action.equals("newGame")) {
			startGame();
		}

		if (action.equals("newRound")) {
			game.startNewRound();
			System.out.println("starting new round " + game.getRoundNumber());
		}

		game.nextQuestion();

		HttpSession session = request.getSession(true);

		session.setAttribute("game", game);

		RequestDispatcher dispatcher = request
				.getRequestDispatcher("question.jsp");
		if (dispatcher != null) {
			dispatcher.forward(request, response);
		}

	}
	
	public void startGame(){
		ServletContext servletContext = getServletContext();
		QuizFactory factory = ServletQuizFactory.init(servletContext);
		QuestionDataProvider provider = factory.createQuestionDataProvider();
		List<? super Category> categories = provider.loadCategoryData();
		List<SimpleCategory> cats = new ArrayList(categories);
		game.startQuiz(cats);
		System.out.println("starting new game");
	}

}