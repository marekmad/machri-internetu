package controllers;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import controllers.Registration.LoginData;
import models.User;
import play.db.jpa.JPA;
import play.mvc.Controller;

public class UserService extends Controller{
	
	private static User findById(Long id) {
        return JPA.em().find(User.class, id);
    }
	public static User findByUsername(String username) {
		List<User> allUsers = (List<User>) UserService.getAllUsers();
		for(int i = 0; i < allUsers.size(); i++){
			if(allUsers.get(i).getUserName().equals(username)){
				return allUsers.get(i);
			}
		}
		return null;
	}
	
	
	public static void save(User user) {
		EntityManager em = JPA.em();
		em.persist(user);
	}
	
	public static Collection<User> getAllUsers() {
		EntityManager em = JPA.em();
		String queryString = "SELECT u FROM User u";
		TypedQuery<User> query = em.createQuery(queryString, User.class);
		return (Collection<User>) query.getResultList();
	}
	
	public static boolean authetificateUserLoginData(LoginData loginData){
		String userName = loginData.userName;
		String passwort = loginData.password;
		User u = UserService.findByUsername(userName);
		
		if(u == null){
			return false;
		}
		
		if(u.getPasswort().equals(passwort)){
			return true;
		}
		return false;
	}

}
