package controllers;

import static play.data.Form.form;

import models.User;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

public class Registration extends Controller {

	final static Form<User> registrationForm = form(User.class);
	final static Form<User> authentifForm = form(User.class);

	public static Result index() {
		return GO_HOME;
	}

	public static Result GO_HOME = redirect(routes.Registration
			.authentification());

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

	public static Result submit() {
		return ok(registration.render(registrationForm));
	}

	@Transactional
	public static Result authentification() {
		return ok(authentification.render(authentifForm));
	}
	
	@Transactional
	public static Result submitLogin() {
		Form<User> authentifForm = form(User.class).bindFromRequest();	
		
		if(authentifForm.hasErrors()) {
			return badRequest(authentification.render(authentifForm));
		}
		User user = authentifForm.get();
		
		boolean isAuthentificated = UserService.authetificateUserLoginData(user);
		
		if(isAuthentificated){
			return ok("user "+user.getUserName() + " is authetificated");
		}else{
			return ok("user "+user.getUserName() + " is not authetificated");
		}
		
		
	}

	public static Result quizOver() {
		return ok(quizover.render(""));
	}

	public static Result roundOver() {
		return ok(roundover.render(""));
	}

	public static Result quiz() {
		return ok(quiz.render(""));
	}

	//
}
