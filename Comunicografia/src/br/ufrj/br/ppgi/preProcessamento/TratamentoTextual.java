package br.ufrj.br.ppgi.preProcessamento;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

import br.ufrj.ppgi.grafo.entidades.Mensagem;

import ptstemmer.Stemmer;
import ptstemmer.exceptions.PTStemmerException;
import ptstemmer.support.PTStemmerUtilities;

public class TratamentoTextual {
	
	public static String converteParaMinuscula(String mensagem)
	{
		return mensagem.toLowerCase();
	}

	private static String aplicaStemmingPalavra(String palavra)
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
	
	private static String aplicaStemmingFrase(String frase)
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
	
	private static String removeStopWords(String mensagem) throws IOException
	{
		Analyzer analyzer = new BrazilianAnalyzer(Version.LUCENE_42);
		TokenStream tokenStream = new StandardTokenizer(Version.LUCENE_42, new StringReader(mensagem));
		CharArraySet stopSet = BrazilianAnalyzer.getDefaultStopSet();
		stopSet.add("é");
		stopSet.add("além");
		stopSet.add("além");
		stopSet.add("desse");
		stopSet.add("nesse");
		stopSet.add("ai");
		stopSet.add("vc");
		stopSet.add("voce");
		stopSet.add("você");
		stopSet.add("nisso");
		stopSet.add("onde");
		stopSet.add("no");
		
		
		

		
		tokenStream = new StopFilter(Version.LUCENE_42, tokenStream, stopSet);
		//CharTermAttribute cattr = tokenStream.getAttribute(CharTermAttribute.class);
		CharTermAttribute termAttr = tokenStream.getAttribute(CharTermAttribute.class);
		
		tokenStream.reset();
		StringBuilder sb = new StringBuilder();
		  while (tokenStream.incrementToken()) {
		        if (sb.length() > 0) {
		            sb.append(" ");
		        }
		        sb.append(termAttr.toString());
		    }
		    return sb.toString();
	

	}
	private static String removeStopWordsSimilaridade(String mensagem) throws IOException
	{
		Analyzer analyzer = new BrazilianAnalyzer(Version.LUCENE_42);
		TokenStream tokenStream = new StandardTokenizer(Version.LUCENE_42, new StringReader(mensagem));
		CharArraySet stopSet = BrazilianAnalyzer.getDefaultStopSet();
		stopSet.add("é");
		stopSet.add("além");
		stopSet.add("além");
		stopSet.add("desse");
		stopSet.add("nesse");
		stopSet.add("ai");
		stopSet.add("vc");
		stopSet.add("voce");
		stopSet.add("você");
		stopSet.add("nisso");
		stopSet.add("onde");
		stopSet.add("no");
		stopSet.add("muito");
		stopSet.add("das");
		stopSet.add("que");
		stopSet.add(".");
		stopSet.add(",");
		stopSet.add("?");
		stopSet.add("!");
		stopSet.add(";");
		stopSet.add(":");
		stopSet.add("maior");
		stopSet.add("menor");
		stopSet.add("mega");
		stopSet.add("metros");
		stopSet.add("atinge");
		stopSet.add("atingir");
		stopSet.add("ah");
		stopSet.add("eh");
		stopSet.add("tem");
		stopSet.add("ter");
		stopSet.add("pra");
		stopSet.add("outra");
		stopSet.add("coisa");
		stopSet.add("para");
		stopSet.add("esta");
		stopSet.add("hoje");
		stopSet.add("amanha");
		stopSet.add("amanhã");
		stopSet.add("ontem");
		stopSet.add("houve");
		stopSet.add("puro");
		stopSet.add("sabe");
		stopSet.add("saber");
		stopSet.add("acho");
		stopSet.add("usam");
		stopSet.add("usando");	
		stopSet.add("tendo");		
		stopSet.add("está");

		
		tokenStream = new StopFilter(Version.LUCENE_42, tokenStream, stopSet);
		//CharTermAttribute cattr = tokenStream.getAttribute(CharTermAttribute.class);
		CharTermAttribute termAttr = tokenStream.getAttribute(CharTermAttribute.class);
		
		tokenStream.reset();
		StringBuilder sb = new StringBuilder();
		  while (tokenStream.incrementToken()) {
		        if (sb.length() > 0) {
		            sb.append(" ");
		        }
		        sb.append(termAttr.toString());
		    }
		    return sb.toString();
	

	}
	public static List<Mensagem> executaTratamentosDiscussao(List<Mensagem> mensagens) throws FileNotFoundException, IOException
	{
		for(Mensagem mensagem:(List<Mensagem>) mensagens)
		{
			String texto;
			texto = TratamentoTextual.removeStopWords(mensagem.getTexto());
			
			texto = TratamentoTextual.converteParaMinuscula(mensagem.getTexto());
			
			
			//O corretor nao esta tendo bons resultados
			//new CorretorGramatical(texto);
			
			//texto = TratamentoTextual.aplicaStemmingFrase(mensagem.getTexto());
			
			mensagem.setTexto(texto);
			
		}
		return mensagens;
	}
	private static Mensagem executaTratamentosMensagem(Mensagem mensagem) throws FileNotFoundException, IOException
	{
		
			String texto;
			texto = TratamentoTextual.removeStopWords(mensagem.getTexto());
			
			texto = TratamentoTextual.converteParaMinuscula(mensagem.getTexto());
			
			
			//O corretor nao esta tendo bons resultados
			//new CorretorGramatical(texto);
			
			//texto = TratamentoTextual.aplicaStemmingFrase(mensagem.getTexto());
			
			mensagem.setTexto(texto);
			
		
		return mensagem;
	}
	public static Mensagem executaTratamentosMensagemParaSalvar(Mensagem mensagem) throws FileNotFoundException, IOException
	{
		
		String texto;

		texto = TratamentoTextual.converteParaMinuscula(mensagem.getTexto());

		mensagem.setTexto(texto);
		
	
	return mensagem;
}
	public static String executaTratamentosTextoParaSalvar(String mensagem) throws FileNotFoundException, IOException
	{
		
			String texto;
			
			texto = TratamentoTextual.converteParaMinuscula(mensagem);
		
		return texto;
	}
	public static String executaTratamentosTextoParaRecomendacao(String mensagem) throws FileNotFoundException, IOException
	{
		
			String texto;
			
			texto = TratamentoTextual.converteParaMinuscula(mensagem);
			texto = TratamentoTextual.removeStopWords(mensagem);

			
			//new CorretorGramatical(texto);
			
			//texto = TratamentoTextual.aplicaStemmingFrase(mensagem.getTexto());
						
		
		return texto;
	}
	
	public static String executaTratamentosTextoParaSimilaridade(String mensagem) throws FileNotFoundException, IOException
	{
		
			String texto;
			String novoTexto="";
			
			texto = TratamentoTextual.converteParaMinuscula(mensagem);
			texto = TratamentoTextual.removeStopWordsSimilaridade(mensagem);
			
			for(String palavra:texto.split(" "))
			{
				if(palavra.length() > 3)
				{
					novoTexto+=palavra+" ";
				}
			}
			if(novoTexto.length() > 120)
			{
				novoTexto.substring(novoTexto.length()-121);
			}
		
		return novoTexto;
	}
	
	public static String executaTratamentosTextoParaFrequencias(String mensagem) throws FileNotFoundException, IOException
	{
		
			String texto;			
			texto = TratamentoTextual.converteParaMinuscula(mensagem);
			
			
			texto = TratamentoTextual.aplicaStemmingFrase(mensagem);
						
		
		return texto;
	}
	
    public static boolean isIntegerParseInt(String str) {
        try {
            Float.parseFloat(str);
            return true;
        } catch (NumberFormatException nfe) {}
        return false;
    }

}
