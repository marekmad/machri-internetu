package controllers;

import play.mvc.*;
import play.mvc.Http.*;


public class SessionSecured extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        return ctx.session().get("userName");
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.Registration.login());
    }
}
