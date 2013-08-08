package br.ufrj.ppgi.recomendacao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class ConsultaSparql {
	public static void main(String[] args) throws IOException {
		
		String[] classesOntologia = {"Animal","Band","RadioStation",
				"TelevisionStation","Airline","LawFirm",
				"RecordLabel","College","Library",
				"School","University","GeopoliticalOrganisation",
				"GovernmentAgency","Legislature","MilitaryUnit",
				"Non-ProfitOrganisation","PoliticalParty","SambaSchool",
				"SportsLeague","SportsTeam","Person",
				"AnatomicalStructure","Award","Biomolecule",
				"CelestialBody","ChemicalSubstance","Colour",
				"Constellation","Currency","Device",
				"Disease","Drug","EthnicGroup",
				"Event","Flag","Food",
				"GovernmentType","Holiday","Ideology",
				"MeanOfTransportation","MusicGenre","Name",
				"PersonFunction","Building","Infrastructure",
				"Park","HistoricPlace","Monument",
				"MountainPass","NaturalPlace","AdministrativeRegion",
				"Atoll","Continent","Country",
				"Island","City","Town",
				"Village","ProtectedArea","SiteOfSpecialScientificInterest",
				"SkiArea","WineRegion","WorldHeritageSite",
				"ProgrammingLanguage","ResearchProject","Sales",
				"SnookerWorldRanking","Bacteria","Plant",
				"Fungus","Tax","Unknown",
				"TopicalConcept","Artwork","Cartoon",
				"Film","Musical","Album",
				"Single","Song","RadioProgram",
				"VideoGame","TelevisionEpisode","TelevisionSeason",
				"TelevisionShow","Website","Book",
				"Comics","PeriodicalLiterature","Play"}; 
		
		Writer output = new BufferedWriter(new FileWriter(new File("entidadesOpenNLP.txt")));
		
		 for(String classe: classesOntologia){
			 
			 String sparqlQuery = 
		        	"SELECT  * WHERE   { ?recurso ?tipo <http://dbpedia.org/ontology/"+classe+">."+
		             "  ?recurso <http://www.w3.org/2000/01/rdf-schema#label> ?label } ";	
			 
			 	//Cria uma query com o string
		        Query query = QueryFactory.create(sparqlQuery);
		        
		        //Faz o sparql no servidor indicado
		        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://pt.dbpedia.org/sparql", query);
		        
		        
		        //Recupera os resultados da query
		        ResultSet resultSet = qexec.execSelect(); 
		        
		        
		        
		        for ( ; resultSet.hasNext() ; )
		        {
		          QuerySolution soln = resultSet.nextSolution() ;
		          RDFNode x = soln.get("recurso") ; 
		          Literal l = soln.getLiteral("label") ;
		          
		          output.write("<START:"+classe+"> "+l.getString() +" <END> ."+"\n");
		        }
		        qexec.close();

		        
			 
		    }
        
        output.close();
        	
     	

	}
}
