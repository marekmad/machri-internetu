package controllers;

import static play.data.Form.form;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
import views.html.registration;
import views.html.authentification;

public class Registration extends Controller {
	
	//final static Form<User> registrationForm = form(User.class);
	/**
     * Display a blank form.
     */ 
    public static Result blank() {
        return ok(registration.render(""));
    }
    public static Result submit() {
        return ok(registration.render(""));
    }
    
    public static Result ahoj(){
    	return ok(authentification.render(""));
    }
//  
}
