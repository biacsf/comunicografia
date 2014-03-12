package br.ufrj.ppgi.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.cogroo.analyzer.Analyzer;
import org.cogroo.analyzer.ComponentFactory;
import org.cogroo.text.Chunk;
import org.cogroo.text.Document;
import org.cogroo.text.Sentence;
import org.cogroo.text.SyntacticChunk;
import org.cogroo.text.Token;
import org.cogroo.text.impl.DocumentImpl;

public class PosTagger {

	private Analyzer cogroo;

	public PosTagger() {
		/*
		 * The following command creates a component factory given a locale. The
		 * locale will be resolved as a configuration file in the classpath with
		 * the following pattern: /models_lang_COUNTRY. Another option is to use
		 * the method ComponentFactory.create(InputStream) directly.
		 */
		ComponentFactory factory = ComponentFactory.create(new Locale("pt",
				"BR"));

		/*
		 * Create the default pipe, which is complete, including from sentence
		 * detection to featurization.
		 */
		cogroo = factory.createPipe();
	}

	public List<String> analyzeAndGetNames(String documentText) {

		// Cria um documento com o texto
		Document document = new DocumentImpl();
		document.setText(documentText);

		//Analisa o documento
		cogroo.analyze(document);


		return getNamesAndProps(document);
	}
	public List<String> analyzeAndGetNumeric(String documentText) {

		// Cria um documento com o texto
		Document document = new DocumentImpl();
		document.setText(documentText);

		//Analisa o documento
		cogroo.analyze(document);


		return getNumeric(document);
	}
	//Imprime nomes proprios
	private List<String> getNamesAndProps(Document document) {
		List<String> nomes = new ArrayList<String>();
		for (Sentence sentence : document.getSentences()) {

			for (Token token : sentence.getTokens()) {
			
				String pos = token.getPOSTag();
				if(pos.equals("prop"))
				{
					nomes.add(sentence.getText().substring(token.getStart(), token.getEnd()));
				}
			}
		}
		return nomes;
	}

	private List<String> getNumeric(Document document) {
		List<String> nomes = new ArrayList<String>();
		for (Sentence sentence : document.getSentences()) {

			for (Token token : sentence.getTokens()) {
			
				String pos = token.getPOSTag();
				if(pos.equals("num"))
				{
					nomes.add(sentence.getText().substring(token.getStart(), token.getEnd()));
				}
			}
		}
		return nomes;
	}

	/** A utility method that prints the analyzed document to the std output. */
	private void print(Document document) {
		StringBuilder output = new StringBuilder();

		// and now we navigate the document to print its data
		for (Sentence sentence : document.getSentences()) {

			// Print the sentence. You can also get the sentence span
			// annotation.
			output.append("Sentence: ").append(sentence.getText()).append("\n");

			output.append("  Tokens: \n");

			// for each token found...
			for (Token token : sentence.getTokens()) {
				String lexeme = token.getLexeme();
				String lemmas = Arrays.toString(token.getLemmas());
				String pos = token.getPOSTag();
				String feat = token.getFeatures();

				output.append(String.format("    %-10s %-12s %-6s %-10s\n",
						lexeme, lemmas, pos, feat));
			}

			// we can also print the chunks, but printing it is not that easy!
			output.append("  Chunks: ");
			for (Chunk chunk : sentence.getChunks()) {
				output.append("[").append(chunk.getTag()).append(": ");
				for (Token innerToken : chunk.getTokens()) {
					output.append(innerToken.getLexeme()).append(" ");
				}
				output.append("] ");
			}
			output.append("\n");

			// we can also print the shallow parsing results!
			output.append("  Shallow Structure: ");
			for (SyntacticChunk structure : sentence.getSyntacticChunks()) {
				output.append("[").append(structure.getTag()).append(": ");
				for (Token innerToken : structure.getTokens()) {
					output.append(innerToken.getLexeme()).append(" ");
				}
				output.append("] ");
			}
			output.append("\n");
		}

		System.out.println(output.toString());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		PosTagger ex = new PosTagger();

		ex.analyzeAndGetNumeric("Hoje eu fui a loja com o Rogério Almada comprar um novo carro. Pela lei 10.506/2013 podemos comprar parcelado.");

	}
}
