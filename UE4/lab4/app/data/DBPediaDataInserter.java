package data;

import java.math.BigDecimal;
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

	public static void loadToQuestionFromDBPedia(Question question,String actorName, String directorName) {
		
		// Check if DBpedia is available
		if (!DBPediaService.isAvailable()) {
			Logger.info("DBpedia is currently not available.");
			
		}
		// Resource Tim Burton is available at
		// http://dbpedia.org/resource/Tim_Burton
		// Load all statements as we need to get the name later
		Resource director = DBPediaService.loadStatements(DBPedia
				.createResource(directorName));
		// Resource Johnny Depp is available at
		// http://dbpedia.org/resource/Johnny_Depp
		// Load all statements as we need to get the name later
		Resource actor = DBPediaService.loadStatements(DBPedia
				.createResource(actorName));
		// retrieve english and german names, might be used for question text
		String englishDirectorName = DBPediaService.getResourceName(director,
				Locale.ENGLISH);
		String germanDirectorName = DBPediaService.getResourceName(director,
				Locale.GERMAN);
		String englishActorName = DBPediaService.getResourceName(actor,
				Locale.ENGLISH);
		String germanActorName = DBPediaService.getResourceName(actor,
				Locale.GERMAN);
		
		question.setTextDE("Im welche Filmen hat "+germanActorName+" gespielt und "+germanDirectorName+" Regie gefürt");
		question.setTextEN("In which movies did "+englishActorName+" played and "+englishDirectorName+" was director");
		question.setMaxTime(new BigDecimal(30));
		
		
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
		
	}
	
	public static void loadToCategory(Category category){
		
		category.setNameDE("bessere Kategorie");
		category.setNameEN("better category");
		
		List<Question> questionCategories = new ArrayList<Question>();
		
		String directorName[] = new String[] {"Tim_Burton","Steven_Spielberg", "Robert_Zemeckis", "Frank_Darabont","Nora_Ephron", "Ron_Howard","David_Silverman"};
		String actorName[] = new String [] {"Johny_Depp","Tom_Hanks","Tom_Hanks","Tom_Hanks","Tom_Hanks", "Tom_Hanks","Tom_Hanks"};

		for(int i = 0; i < 7; i++) {
			Question question = new Question();
			loadToQuestionFromDBPedia(question, actorName[i], directorName[i]);
			question.setCategory(category);
			questionCategories.add(question);
		}
		
		category.setQuestions(questionCategories);
		
	}
	
	public static List<Category> loadCategories(int categoryCount){
		List<Category> allCategories = new ArrayList<Category>();
		
		for(int i = 0; i < categoryCount; i++) {
			Category category = new Category();
			loadToCategory(category);
			
			allCategories.add(category);
		}
		
		return allCategories;
	}
	
	
	@Transactional
	public static void insertData(){
		
		List<Category> dbpediaCategories = loadCategories(1);
		
		
		
		for(Category category : dbpediaCategories) {
			
			Logger.info(Integer.toString(category.getQuestions().size()));
			
			QuizDAO.INSTANCE.persist(category);
		}
			
		Logger.info("Data from DBPedia inserted.");
		
	}
	
	

}
