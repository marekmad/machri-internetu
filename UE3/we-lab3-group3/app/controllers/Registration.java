package controllers;

import static play.data.Form.form;
import models.User;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

import play.mvc.*;
import play.data.*;
import static play.data.Form.*;

import java.util.*;

import models.*;

public class Registration extends Controller {

	final static Form<User> registrationForm = form(User.class);
	final static Form<LoginData> loginForm = form(LoginData.class);

	public static Result index() {
		return GO_HOME;
	}

	public static Result GO_HOME = redirect(routes.Registration.login());

	@Transactional
	public static Result save() {
		
		Form<User> userForm = form(User.class).bindFromRequest();	
		
		if(userForm.hasErrors()) {
			return badRequest(registration.render(userForm));
		}
		
		User user = userForm.get();
		
		UserService.save(user);	
		
		return GO_HOME;
	}
	
	

	@Transactional
	public static Result create() {
		Form<User> userForm = form(User.class);
		return ok(registration.render(userForm));
	}

	public static Result login() {
		return ok(authentification.render(loginForm));
	}
	
	@Transactional
	public static Result authentificate() {
		Form<LoginData> authentifForm = form(LoginData.class).bindFromRequest();	
		
		if(authentifForm.hasErrors()) {
			return badRequest(authentification.render(authentifForm));
		}
		else{
			LoginData loginData = authentifForm.get();
			
			session().clear();
			session("userName", loginData.userName);
			
			return redirect(routes.Registration.quizStart());
		}
		
		
	}
	
	public static Result logout() {
		session().clear();
	    flash("successLogout", "You've been logged out");
	    
	    return redirect(routes.Registration.login());
	}
	
	@Security.Authenticated(SessionSecured.class)
	public static Result quizStart() {
		return ok(index.render(""));
	}
	
//	@Security.Authenticated(SessionSecured.class)
//	public static Result quizOver() {
//		return ok(quizover.render(""));
//	}
	
//	@Security.Authenticated(SessionSecured.class)
//	public static Result roundOver() {
//		return ok(roundover.render(""));
//	}

//	@Security.Authenticated(SessionSecured.class)
//	public static Result quiz() {
//		return ok(quiz.render(""));
//	}
	
	
	public static class LoginData {
		
		public String userName;
        public String password;
        
        public String validate() {
        	boolean isAuthentificated = UserService.authetificateUserLoginData(this);
        	if(isAuthentificated) {
        		return null;
        	}else{
        		return "Invalid username or password";
        	}
        }
		
	}

	//
}
