package at.ac.tuwien.big.we14.lab2.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	String [] selected = request.getParameterValues("opts");
    	
    	
    	if(selected != null){
    		response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.print("<html><body>");
            out.print("<h1> Your selections");
            out.print("<ul>");
            for(String s : selected){
            	out.print("<li>"+s+"</li>");
            }
            out.print("</ul>");
            out.print("</body></html>");
    	}
    
    }

}