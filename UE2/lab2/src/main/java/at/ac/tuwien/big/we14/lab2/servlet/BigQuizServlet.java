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

	private List<SimpleCategory> cat = new ArrayList<SimpleCategory>();
	private List<SimpleQuestion> q = new ArrayList<SimpleQuestion>();
	private int questionNumber = 0;
	private int roundNumber = 0;
	private int[] scoreP1 = new int[3];
	private int[] scoreP2 = new int[3];
	private int timeP1 = 0;
	private int timeP2 = 0;

	@Override
	public void init() throws ServletException {
		super.init();
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String[] selected = request.getParameterValues("opts");
		String[] testing = request.getParameterValues("");
		
		System.out.println(testing + "testing");

		System.out.println("______Questionnumber" + questionNumber);

		List<Choice> allcChoises = new ArrayList<Choice>();
		List<Choice> cChoises = new ArrayList<Choice>();
		// List<Choice> wChoises = new ArrayList<Choice>();
		// q.get(questionNumber).getCorrectChoices().toString();
		allcChoises.addAll(q.get(questionNumber - 1).getCorrectChoices());
		List<Choice> copyOfChoises = new ArrayList<Choice>(cChoises);

		// wChoises.addAll(q.get(questionNumber).getAllChoices());
		// wChoises.removeAll(c)

		boolean wasNotCorrect = false;
		boolean wasIncluded = false;
		if (selected != null) {

			for (String s : selected) {
				System.out.println(s);
				for (Choice c : allcChoises) {
					if (String.valueOf(c.getId()).equals(s)) {
						cChoises.add(c);
						System.out.println("adding" + c.getId());
					}

				}
			}
			if (!cChoises.isEmpty()) {
				for (String s : selected) {
					System.out.println(s);

					wasIncluded = false;
					for (Choice c : cChoises) {
						System.out.println("ceking for choise" + c.getId());
						if (String.valueOf(c.getId()).equals(s)) {
							copyOfChoises.remove(c);
							wasIncluded = true;
							System.out.println("Removing: " + s);
						}
					}
					if (!wasIncluded)
						wasNotCorrect = true;
				}

			}
		}

		System.out.println("was not Correct: " + wasNotCorrect);
		System.out.println("Choises: " + copyOfChoises);
		System.out.println("Is empty:" + copyOfChoises.isEmpty());
		System.out.println("____________________________");
		// cChoises.

		if (questionNumber == 3) {

			if (roundNumber == 5) {
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("/finish.html");

				if (dispatcher != null) {
					dispatcher.forward(request, response);

				}
			} else {
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("/roundcomplete.html");

				if (dispatcher != null) {
					dispatcher.forward(request, response);
				}
			}

		} else {
			SimpleQuestion question = q.get(questionNumber++);

			HttpSession session = request.getSession(true);

			session.setAttribute("question", question);
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
			startQuiz();
			System.out.println("starting new game");
		}

		if (action.equals("newRound")) {
			startNewRound();
			System.out.println("starting new round " + roundNumber);
		}

		SimpleQuestion q1 = q.get(questionNumber++);

		HttpSession session = request.getSession(true);

		session.setAttribute("question", q1);

		RequestDispatcher dispatcher = request
				.getRequestDispatcher("question.jsp");
		if (dispatcher != null) {
			dispatcher.forward(request, response);
		}

	}

	private void startQuiz() {
		roundNumber = 0;
		ServletContext servletContext = getServletContext();
		QuizFactory factory = ServletQuizFactory.init(servletContext);
		QuestionDataProvider provider = factory.createQuestionDataProvider();
		List<? super Category> categories = provider.loadCategoryData();
		List<SimpleCategory> cats = new ArrayList(categories);
		cat = orderCategories(cats);
		System.out.println(cats);
		startNewRound();

	}

	private void startNewRound() {
		questionNumber = 0;
		List<? extends Question> question = new ArrayList<Question>(cat.get(
				roundNumber++).getQuestions());
		List<SimpleQuestion> qu = new ArrayList(question);

		q = orderQuestions(qu);

	}

	private List<SimpleCategory> orderCategories(List<SimpleCategory> categories) {

		List<SimpleCategory> allCategories = new ArrayList<SimpleCategory>(
				categories);
		List<SimpleCategory> orderedCategories = new ArrayList<SimpleCategory>();

		Random rand = new Random();

		for (int i = 0; i < allCategories.size(); i++) {
			int r = rand.nextInt(allCategories.size() - i);
			orderedCategories.add(allCategories.get(r));
			allCategories.set(r,
					allCategories.get(allCategories.size() - 1 - i));
		}

		return orderedCategories;
	}

	private List<SimpleQuestion> orderQuestions(List<SimpleQuestion> allQuestion) {

		List<SimpleQuestion> all = new ArrayList<SimpleQuestion>(allQuestion);
		List<SimpleQuestion> orderedQuestions = new ArrayList<SimpleQuestion>();

		Random rand = new Random();

		for (int i = 0; i < 3; i++) {
			int r = rand.nextInt(all.size() - i);
			orderedQuestions.add(all.get(r));
			all.set(r, all.get(all.size() - 1 - i));
		}

		return orderedQuestions;
	}

}