package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import play.data.validation.Constraints;

@Entity
@SequenceGenerator(name = "user_seq", sequenceName = "user_seq")
public class Player implements at.ac.tuwien.big.we14.lab2.api.User{

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
	private Long id;
	
	String vorname;
	String nachname;
	String geschlecht;

	@Constraints.Required
	@Constraints.MinLength(4)
	@Constraints.MaxLength(8)
	String userName;

	@Constraints.Required
	@Constraints.MinLength(4)
	@Constraints.MaxLength(8)
	String passwort;

	public Long getId() {
		return id;
	}

	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public void setID(long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPasswort() {
		return passwort;
	}

	public void setPasswort(String passwort) {
		this.passwort = passwort;
	}
	
	public void setGeschlecht(String geschlecht){
		this.geschlecht = geschlecht;
	}
	
	public String getGeschlecht(){
		return this.geschlecht;
	}
	
	public static List<String> geschlechtToList() {
		List<String> geschlecht = new ArrayList<String>();
		geschlecht.add("Manlich");
		geschlecht.add("Weiblich");
		return geschlecht;
	}

	@Override
	public String getName() {
		return this.nachname;
	}

	@Override
	public void setName(String name) {
		this.nachname = name;
		
	}
	
	

}
