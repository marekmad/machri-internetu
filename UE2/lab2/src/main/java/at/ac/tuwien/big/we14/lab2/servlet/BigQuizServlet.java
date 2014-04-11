package at.ac.tuwien.big.we14.lab2.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import at.ac.tuwien.big.we14.lab2.api.impl.SimpleQuestion;


public class BigQuizServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static int counter = 0;
	
    
    @Override
    public void init() throws ServletException {
        super.init();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	String [] selected = request.getParameterValues("opts");
    	
    	
//    	if(selected != null) {
//    		response.setContentType("text/html;charset=UTF-8");
//            PrintWriter out = response.getWriter();
//            out.print("<html><body>");
//            out.print("<h1> Your selections");
//            out.print("<ul>");
//            for(String s : selected){
//            	out.print("<li>"+s+"</li>");
//            }
//            out.print("</ul>");
//            out.print("</body></html>");
//    	}
    	
    	SimpleQuestion question = new SimpleQuestion();
    	question.setText("Jak sa volas ? "+(++counter));
    	
    	HttpSession session = request.getSession(true);
    	
    	session.setAttribute("question", question);
    	RequestDispatcher dispatcher = request.getRequestDispatcher("/question.jsp");  
    	if (dispatcher != null){  
    		dispatcher.forward(request, response);  
    	}
    	
    	
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	String [] selected = request.getParameterValues("opts");
    	
    	
//    	if(selected != null) {
//    		response.setContentType("text/html;charset=UTF-8");
//            PrintWriter out = response.getWriter();
//            out.print("<html><body>");
//            out.print("<h1> Your selections");
//            out.print("<ul>");
//            for(String s : selected){
//            	out.print("<li>"+s+"</li>");
//            }
//            out.print("</ul>");
//            out.print("</body></html>");
//    	}
    	
    	SimpleQuestion question = new SimpleQuestion();
    	question.setText("cigan ?");
    	
    	HttpSession session = request.getSession(true);
    	
    	session.setAttribute("question", question);
    	RequestDispatcher dispatcher = request.getRequestDispatcher("/question.jsp");  
    	if (dispatcher != null){  
    		dispatcher.forward(request, response);  
    	}
    	
    	
    }
    

}