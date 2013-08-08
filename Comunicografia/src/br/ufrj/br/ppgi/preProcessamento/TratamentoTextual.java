package br.ufrj.br.ppgi.preProcessamento;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

import br.ufrj.ppgi.grafo.entidades.Mensagem;

import ptstemmer.Stemmer;
import ptstemmer.exceptions.PTStemmerException;
import ptstemmer.support.PTStemmerUtilities;

public class TratamentoTextual {

	public static String aplicaStemmingPalavra(String palavra)
	{
	
		Stemmer st = null;
		try {
			st = Stemmer.StemmerFactory(Stemmer.StemmerType.ORENGO);
			st.enableCaching(1000);
			st.ignore(PTStemmerUtilities.fileToSet(""));
		} catch (PTStemmerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return st.getWordStem(palavra);
	
	}
	
	public static String aplicaStemmingFrase(String frase)
	{
	
		Stemmer st = null;
		try {
			st = Stemmer.StemmerFactory(Stemmer.StemmerType.ORENGO);
			st.enableCaching(1000);
			//st.ignore(PTStemmerUtilities.fileToSet(""));
		} catch (PTStemmerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String mensagem ="";
		for(String palavra: st.getPhraseStems(frase))
		{
			mensagem+=palavra+" ";
		}
		return mensagem;
	
	}
	
	public static String removeStopWords(String mensagem) throws IOException
	{
		Analyzer analyzer = new BrazilianAnalyzer(Version.LUCENE_42);
		TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(mensagem));
		tokenStream = new StopFilter(Version.LUCENE_42, tokenStream, BrazilianAnalyzer.getDefaultStopSet());
		CharTermAttribute cattr = tokenStream.addAttribute(CharTermAttribute.class);
		
		tokenStream.reset();
		StringBuffer sb = new StringBuffer();
		while (tokenStream.incrementToken()) {
		  sb.append(cattr.toString());
		  sb.append(" ");
		}
		tokenStream.end();
		tokenStream.close();
		    
	    return sb.toString();
	

	}
	public static List<Mensagem> executaTratamentosDiscussao(List<Mensagem> mensagens) throws FileNotFoundException, IOException
	{
		for(Mensagem mensagem:(List<Mensagem>) mensagens)
		{
			String texto;
			texto = TratamentoTextual.removeStopWords(mensagem.getTexto());
			
			//O corretor nao esta tendo bons resultados
			//new CorretorGramatical(texto);
			
			//texto = TratamentoTextual.aplicaStemmingFrase(mensagem.getTexto());
			
			mensagem.setTexto(texto);
			
		}
		return mensagens;
	}

}
