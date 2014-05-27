package data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import models.Choice;
import models.Question;
import models.Category;
import models.QuizDAO;
import play.Logger;
import play.db.jpa.Transactional;
import at.ac.tuwien.big.we14.lab4.dbpedia.api.DBPediaService;
import at.ac.tuwien.big.we14.lab4.dbpedia.api.SelectQueryBuilder;
import at.ac.tuwien.big.we14.lab4.dbpedia.vocabulary.DBPedia;
import at.ac.tuwien.big.we14.lab4.dbpedia.vocabulary.DBPediaOWL;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class DBPediaDataInserter {

	public static Question loadQuestionFromDBPedia() {
		
		Question question = new Question();
		
		// Check if DBpedia is available
		if (!DBPediaService.isAvailable()) {
			Logger.info("DBpedia is currently not available.");
			return null;
		}
		// Resource Tim Burton is available at
		// http://dbpedia.org/resource/Tim_Burton
		// Load all statements as we need to get the name later
		Resource director = DBPediaService.loadStatements(DBPedia
				.createResource("Tim_Burton"));
		// Resource Johnny Depp is available at
		// http://dbpedia.org/resource/Johnny_Depp
		// Load all statements as we need to get the name later
		Resource actor = DBPediaService.loadStatements(DBPedia
				.createResource("Johnny_Depp"));
		// retrieve english and german names, might be used for question text
		String englishDirectorName = DBPediaService.getResourceName(director,
				Locale.ENGLISH);
		String germanDirectorName = DBPediaService.getResourceName(director,
				Locale.GERMAN);
		String englishActorName = DBPediaService.getResourceName(actor,
				Locale.ENGLISH);
		String germanActorName = DBPediaService.getResourceName(actor,
				Locale.GERMAN);
		
		question.setTextDE(germanActorName + " cibulu "+ germanDirectorName);
		question.setTextEN(englishActorName + " cibulu "+ englishDirectorName);
		
		
		// build SPARQL-query
		SelectQueryBuilder movieQuery = DBPediaService
				.createQueryBuilder()
				.setLimit(5)
				// at most five statements
				.addWhereClause(RDF.type, DBPediaOWL.Film)
				.addPredicateExistsClause(FOAF.name)
				.addWhereClause(DBPediaOWL.director, director)
				.addFilterClause(RDFS.label, Locale.GERMAN)
				.addFilterClause(RDFS.label, Locale.ENGLISH);
		// retrieve data from dbpedia
		Model timBurtonMovies = DBPediaService.loadStatements(movieQuery
				.toQueryString());
		// get english and german movie names, e.g., for right choices
		List<String> englishTimBurtonMovieNames = DBPediaService
				.getResourceNames(timBurtonMovies, Locale.ENGLISH);
		List<String> germanTimBurtonMovieNames = DBPediaService
				.getResourceNames(timBurtonMovies, Locale.GERMAN);
		
		for(int i = 0; i < englishTimBurtonMovieNames.size(); i++){
			Choice correctChoice = new Choice();
			correctChoice.setTextEN(englishTimBurtonMovieNames.get(i).toString());
			correctChoice.setTextDE(germanTimBurtonMovieNames.get(i).toString());
			question.addRightChoice(correctChoice);
		}
		
		
		
		
		// alter query to get movies without tim burton
		movieQuery.removeWhereClause(DBPediaOWL.director, director);
		movieQuery.addMinusClause(DBPediaOWL.director, director);
		// retrieve data from dbpedia
		Model noTimBurtonMovies = DBPediaService.loadStatements(movieQuery
				.toQueryString());
		// get english and german movie names, e.g., for wrong choices
		List<String> englishNoTimBurtonMovieNames = DBPediaService
				.getResourceNames(noTimBurtonMovies, Locale.ENGLISH);
		List<String> germanNoTimBurtonMovieNames = DBPediaService
				.getResourceNames(noTimBurtonMovies, Locale.GERMAN);
		
		
		
		for(int i = 0; i < englishNoTimBurtonMovieNames.size(); i++){
			Choice wrongChoice = new Choice();
			wrongChoice.setTextEN(englishNoTimBurtonMovieNames.get(i).toString());
			wrongChoice.setTextDE(englishNoTimBurtonMovieNames.get(i).toString());
			question.addWrongChoice(wrongChoice);
		}
		
		return question;
		
	}
	
	public static Category loadCategory(){
		Category category = new Category();
		
		category.setNameDE("Bessere category DE");
		category.setNameEN("Bessere category EN");
		
		List<Question> questionCategories = new ArrayList<Question>();
		
		for(int i = 0; i < 7; i++){
			Question question = loadQuestionFromDBPedia();
			questionCategories.add(question);
		}
		
		return category;
	}
	
	public static List<Category> loadCategories(int categoryCount){
		List<Category> allCategories = new ArrayList<Category>();
		
		for(int i = 0; i < categoryCount; i++){
			allCategories.add(loadCategory());
		}
		
		return allCategories;
	}
	
	
	@Transactional
	public static void insertData(){
		
		List<Category> dbpediaCategories = loadCategories(5);
		Logger.info(dbpediaCategories.toString());
		System.out.println(dbpediaCategories.toString());
		for(Category category : dbpediaCategories){
			Logger.info(Integer.toString(category.getQuestions().get(0).getCorrectChoices().size()));
			QuizDAO.INSTANCE.persist(category);
		}
			
		
	}
	
	

}
