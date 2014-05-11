package controllers;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import controllers.Registration.LoginData;
import models.Player;
import play.db.jpa.JPA;
import play.mvc.Controller;

public class UserService extends Controller{
	
	private static Player findById(Long id) {
        return JPA.em().find(Player.class, id);
    }
	public static Player findByUsername(String username) {
		List<Player> allUsers = (List<Player>) UserService.getAllUsers();
		for(int i = 0; i < allUsers.size(); i++){
			if(allUsers.get(i).getUserName().equals(username)){
				return allUsers.get(i);
			}
		}
		return null;
	}
	
	
	public static void save(Player user) {
		EntityManager em = JPA.em();
		em.persist(user);
	}
	
	public static Collection<Player> getAllUsers() {
		EntityManager em = JPA.em();
		String queryString = "SELECT u FROM Player u";
		TypedQuery<Player> query = em.createQuery(queryString, Player.class);
		return (Collection<Player>) query.getResultList();
	}
	
	public static boolean authetificateUserLoginData(LoginData loginData){
		String userName = loginData.userName;
		String passwort = loginData.password;
		Player u = UserService.findByUsername(userName);
		
		if(u == null){
			return false;
		}
		
		if(u.getPasswort().equals(passwort)){
			return true;
		}
		return false;
	}

}
