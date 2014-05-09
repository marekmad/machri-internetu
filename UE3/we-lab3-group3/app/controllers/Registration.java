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
import play.db.jpa.*;
import models.*;
import play.db.ebean.*;

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
		User a = userForm.get();
		a.save();
		flash("success", "Computer "+userForm.get().getUserName()+" has been created");
		return GO_HOME;
	}

	@Transactional(readOnly = true)
	public static Result create() {
		 Form<User> userForm = form(User.class);
		return ok(registration.render(userForm));
	}

	public static Result submit() {
		return ok(registration.render(registrationForm));
	}

	public static Result authentification() {
		return ok(authentification.render(authentifForm));
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
