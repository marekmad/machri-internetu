package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import play.data.validation.Constraints;
import play.db.jpa.JPA;


@Entity
public class User {
	
	public enum Geschlecht {
		maenlich, weiblich;
		
		public static List<String> toList(){
			List<String> geschlecht = new ArrayList<String>();
			geschlecht.add("Manlich");
			geschlecht.add("Weiblich");
			return geschlecht;
		}
	}
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	String vorname;
	String nachname;
	//Date geburtsDatum;
	Geschlecht geschlecht;
	
	@Constraints.Required
	@Constraints.MinLength(4)
	@Constraints.MaxLength(8)
	String userName;
	
	@Constraints.Required
	@Constraints.MinLength(4)
	@Constraints.MaxLength(8)
	String passwort;

	
	 public void save() {
		  JPA.em().persist(this);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
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

//	public Date getGeburtsDatum() {
//		return geburtsDatum;
//	}
//
//	public void setGeburtsDatum(Date geburtsDatum) {
//		this.geburtsDatum = geburtsDatum;
//	}

	public Geschlecht getGeschlecht() {
		return geschlecht;
	}

	public void setGeschlecht(Geschlecht geschlecht) {
		this.geschlecht = geschlecht;
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



















	
	
	
}
