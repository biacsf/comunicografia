package br.ufrj.ppgi.grafo;


import java.io.IOException;


import com.google.api.GoogleAPI;
import com.google.api.GoogleAPIException;
import com.google.api.translate.Language;
import com.google.api.translate.Translate;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class ConsultaSparql {

	public static void main(String[] args) throws IOException, GoogleAPIException {		
		
		 GoogleAPI.setHttpReferrer("http://translate.google.pl");
		 GoogleAPI.setKey("AIzaSyAE_ByOzrogPXtx9wUZlA6gTZJNrlYS41o");

		 String translatedText = Translate.DEFAULT.execute("run", Language.ENGLISH, Language.ENGLISH);

		 System.out.println(translatedText);

			 
			 String sparqlQuery = 
		        	"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
					"PREFIX  wordnet: <http://www.w3.org/2006/03/wn/wn20/schema/> "+
					
					"SELECT DISTINCT ?label {  "+
					 "?input_word a wordnet:WordSense;  "+
					    " rdfs:label ?input_label.  "+
					  "FILTER (?input_label = 'run')  "+
					  "?synset wordnet:containsWordSense ?input_word.  "+
					  "?synset wordnet:containsWordSense ?synonym.  "+
					  "?synonym rdfs:label ?label.  "+
					"} LIMIT 100  ";
			 
			 	//Cria uma query com o string
		        Query query = QueryFactory.create(sparqlQuery);
		        
		        //Faz o sparql no servidor indicado
		        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://wordnet.rkbexplorer.com/sparql/", query);
		        
		        
		        //Recupera os resultados da query
		        ResultSet resultSet = qexec.execSelect(); 
		        
		        
		        
		        for ( ; resultSet.hasNext() ; )
		        {
		        	
		          QuerySolution soln = resultSet.nextSolution() ;
		          RDFNode x = soln.get("recurso") ; 
		          Literal l = soln.getLiteral("label") ;
		          String[] words = l.getString().split(" ");
		          for(String s:words)
		          {
		        	  System.out.println(s);
		          }
		         
		        }
		        qexec.close();

		        
			 
		    }
       
       	
	
}
