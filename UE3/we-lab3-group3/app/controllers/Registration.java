package controllers;

import static play.data.Form.form;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
import views.html.registration;
import views.html.authentification;
import views.html.quizover;
import views.html.roundover;
import views.html.quiz;

public class Registration extends Controller {

	// final static Form<User> registrationForm = form(User.class);
	/**
	 * Display a blank form.
	 */
	public static Result blank() {
		return ok(registration.render(""));
	}

	public static Result submit() {
		return ok(registration.render(""));
	}

	public static Result authentification() {
		return ok(authentification.render(""));
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
