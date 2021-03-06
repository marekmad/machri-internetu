package controllers;

import static play.data.Form.form;
import models.Player;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;
import play.mvc.*;
import play.data.*;
import static play.data.Form.*;

import java.util.*;

import models.*;

public class Registration extends Controller {

	final static Form<Player> registrationForm = form(Player.class);
	final static Form<LoginData> loginForm = form(LoginData.class);

	public static Result index() {
		return GO_HOME;
	}

	public static Result GO_HOME = redirect(routes.Registration.login());

	@Transactional
	public static Result save() {
		
		Form<Player> userForm = form(Player.class).bindFromRequest();	
		
		if(userForm.hasErrors()) {
			return badRequest(registration.render(userForm));
		}
		
		Player user = userForm.get();
		
		UserService.save(user);	
		
		return GO_HOME;
	}
	
	

	@Transactional
	public static Result create() {
		Form<Player> userForm = form(Player.class);
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
	
	
	@Security.Authenticated(SessionSecured.class)
	public static Result logout() {
		
		String userName = session().get("userName");
		session().clear();
	    flash("successLogout", "You've been logged out");
	    
	    Application.gameMap.remove(userName);
	    
	    return redirect(routes.Registration.login());
	}
	
	@Security.Authenticated(SessionSecured.class)
	public static Result quizStart() {
		return ok(index.render(""));
	}
	
	
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
