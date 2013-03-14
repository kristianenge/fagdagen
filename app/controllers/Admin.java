package controllers;

import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.login;
import views.html.admin;

import static play.data.Form.form;

/**
 * Controller for the admin pages
 */
public class Admin extends Controller {

    // -- Authentication

    public static class Login {

        public String email;
        public String password;

        public String validate() {
            // TODO: Change implementation here when we have database
//            if(User.authenticate(email, password) == null) {
//                return "Invalid user or password";
//            }
            if(password.equalsIgnoreCase("password"))
            {
                return null;
            }
            return "Invalid user or password";
        }

    }

    /**
     * Login page.
     */
    public static Result login() {
        return ok(login.render(form(Login.class)));
    }

    /**
     * Admin page.
     */
    public static Result admin() {
        return ok(admin.render("Success!"));
    }

    /**
     * Handle login form submission.
     */
    public static Result authenticate() {
        Form<Login> loginForm = form(Login.class).bindFromRequest();
        if(loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session("email", loginForm.get().email);
            return redirect(routes.Application.index());
        }
    }

    /**
     * Logout and clean the session.
     */
    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(routes.Admin.login());
    }

}
