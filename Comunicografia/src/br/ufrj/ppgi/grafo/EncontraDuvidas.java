	package br.ufrj.ppgi.grafo;

import java.util.ArrayList;
import java.util.List;

public class EncontraDuvidas {
    public Boolean contemDuvida(String texto)
    {
        List<String> palavrasComuns = new ArrayList<String>();
        palavrasComuns.add("duvida");
        palavrasComuns.add("dúvida");
        palavrasComuns.add("duvido");
        palavrasComuns.add("n entendi");
        palavrasComuns.add("nao entendi");
        palavrasComuns.add("não entendi");
        palavrasComuns.add("pode repetir");
        palavrasComuns.add("me perdi");
        palavrasComuns.add("perdido");
        palavrasComuns.add("perdida");
        palavrasComuns.add("n compreendi");
        palavrasComuns.add("nao compreendi");
        palavrasComuns.add("não compreendi");
        palavrasComuns.add("n compreendo");
        palavrasComuns.add("nao compreendo");
        palavrasComuns.add("não compreend");
        palavrasComuns.add("me ajude");
        palavrasComuns.add("me ajuda");
        palavrasComuns.add("me ajudem");
        palavrasComuns.add("ajudem-me");
        palavrasComuns.add("o que está acontecendo");
        palavrasComuns.add("o q está acontecendo");
        palavrasComuns.add("o que esta acontecendo");
        
        for(String palavra: palavrasComuns)
        {
        	if(texto.contains(palavra))
        	{
        		return true;
        	}
        }
        
        return false;
        
    }
}
