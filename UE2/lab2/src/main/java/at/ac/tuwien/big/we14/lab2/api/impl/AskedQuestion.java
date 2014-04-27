package at.ac.tuwien.big.we14.lab2.api.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import at.ac.tuwien.big.we14.lab2.api.Category;
import at.ac.tuwien.big.we14.lab2.api.Choice;
import at.ac.tuwien.big.we14.lab2.api.Question;

public class AskedQuestion implements Question {

	private int id;
	private String text;
	private List<Choice> askedChoices = new ArrayList<>();
	private Category category;
	private Date askedTime;
	private long maxTime;

	public AskedQuestion() {
		this.text = "";
		this.maxTime = 60;
		this.askedChoices = new ArrayList<>();
		this.category = null;
	}

	public Date getAskedTime() {
		return askedTime;
	}

	public void setAskedTime(Date askedTime) {
		this.askedTime = askedTime;
	}

	public AskedQuestion(SimpleQuestion q) {
		this.id = q.getId();
		this.text = q.getText();

		List<Choice> allChoices = q.getAllChoices();
		for (int i = 0; i < 4; i++) {
			askedChoices.add(allChoices.get(i));
		}

		boolean hasCorrectChoice = false;
		for (int i = 0; i < 4; i++) {
			for (int y = 0; y < q.getCorrectChoices().size(); y++) {
				if (askedChoices.get(i).equals(q.getCorrectChoices().get(y))) {
					hasCorrectChoice = true;
				}
			}
		}
		if (!hasCorrectChoice) {
			
			Random r = new Random();
			int i = r.nextInt(4);
			askedChoices.set(i, q.getCorrectChoices().get(0));
			
		}

		this.category = q.getCategory();
		this.maxTime = q.getMaxTime();

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<Choice> getAllChoices() {
		return askedChoices;
	}

	public void setAskedChoices(List<Choice> askedChoices) {
		this.askedChoices = askedChoices;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public long getMaxTime() {
		return maxTime;
	}

	public String getFormatedMaxTime() {

		SimpleDateFormat df = new SimpleDateFormat("mm:ss");
		String time = df.format(maxTime * 1000);

		return time;
	}

	public void setMaxTime(long maxTime) {
		this.maxTime = maxTime;
	}

	@Override
	public List<Choice> getCorrectChoices() {

		return null;
	}

	@Override
	public void addChoice(Choice choice, boolean isCorrect) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeChoice(Choice choice) {
		// TODO Auto-generated method stub

	}

}
