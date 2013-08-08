package br.ufrj.br.ppgi.preProcessamento;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellCheckListener;
import com.swabunga.spell.event.SpellChecker;
import com.swabunga.spell.event.StringWordTokenizer;

public class CorretorGramatical implements SpellCheckListener {

	public CorretorGramatical(String mensagem) 
	{
		try{
		SpellDictionary dictionary = new SpellDictionaryHashMap(new File("src/recursos/wordlist_pt_br.txt"));
	    SpellChecker spellChecker = new SpellChecker(dictionary);
	    spellChecker.addSpellCheckListener(this);
	   
	   spellChecker.checkSpelling(new StringWordTokenizer(mensagem));	
		} catch (Exception e) {
		      e.printStackTrace();
		    }
	   
	}

		@Override
		public void spellingError(SpellCheckEvent event) {
			List suggestions = event.getSuggestions();
		    if (suggestions.size() > 0) {
		      System.out.println("MISSPELT WORD: " + event.getInvalidWord());
		      for (Iterator suggestedWord = suggestions.iterator(); suggestedWord.hasNext();) {
		        System.out.println("\tSuggested Word: " + suggestedWord.next());
		      }
		    } else {
		      System.out.println("MISSPELT WORD: " + event.getInvalidWord());
		      System.out.println("\tNo suggestions");
		    }
		}
}
