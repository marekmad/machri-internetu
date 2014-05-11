package models;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import play.data.Form;
import at.ac.tuwien.big.we14.lab2.api.Choice;
import at.ac.tuwien.big.we14.lab2.api.Question;

public class AskedQuestion implements Choice{
	
	
	Question question;
	public List<String> chosenAnseres;
	
	
	public List<String> getchosenAnseres(){
		return chosenAnseres;
	}
	public void setchosenAnseres(ArrayList<String> s){
		chosenAnseres = s;
		
	}
	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setText(String text) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setQuestion(Question question) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Question getQuestion() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setId(int ID) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
}
