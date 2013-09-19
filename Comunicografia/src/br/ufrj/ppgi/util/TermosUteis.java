package br.ufrj.ppgi.util;

import java.util.ArrayList;
import java.util.List;

import br.ufrj.br.ppgi.preProcessamento.TratamentoTextual;

public class TermosUteis {

	public static Boolean contemTermosLegislacao(String texto) {
		List<String> palavrasComuns = new ArrayList<String>();
		palavrasComuns.add("lei");
		palavrasComuns.add("projeto de lei");
		palavrasComuns.add("projeto");
		palavrasComuns.add("emenda");
		palavrasComuns.add("norma");
		palavrasComuns.add("decreto");
		palavrasComuns.add("legislativo");
		palavrasComuns.add("constitucional");
		palavrasComuns.add("portaria");
		palavrasComuns.add("resolução");
		palavrasComuns.add("resolucao");
		palavrasComuns.add("resoluçao");
		palavrasComuns.add("resolucão");
		
		return fraseContemListaPalavras(texto,palavrasComuns);
	}
	public static String palavraAposTermosLegislacao(String texto) {
		List<String> palavrasComuns = new ArrayList<String>();
		palavrasComuns.add("lei");
		palavrasComuns.add("projeto de lei");
		palavrasComuns.add("emenda");
		palavrasComuns.add("norma");
		palavrasComuns.add("decreto");
		palavrasComuns.add("legislativo");
		palavrasComuns.add("constitucional");
		palavrasComuns.add("portaria");
		palavrasComuns.add("resolução");
		palavrasComuns.add("resolucao");
		palavrasComuns.add("resoluçao");
		palavrasComuns.add("resolucão");
		
		return palavraAposListaPalavras(texto,palavrasComuns);
	}

	private static String palavraAposListaPalavras(String texto, List<String> palavras) {

		String retorno = "";
		for (String palavra : palavras) {
			if (texto.contains(palavra)) {
				String[] textoDividido = texto.split(" ");
				for(int i=0;i<=(textoDividido.length-2);i++)
				{
					if(textoDividido[i].equals(palavra))
					{
						int count = i+1;
						if(TratamentoTextual.isIntegerParseInt(textoDividido[count]))
						{
							return textoDividido[count];
						}else{
							if(textoDividido[count].equals("da") || textoDividido[count].equals("do") || textoDividido[count].equals("de"))
							{
								int number = count+1;
								return textoDividido[number];
							}
						}
						
					}
				}
			}

		}

		return retorno;
	}
	
	public static Boolean contemDuvida(String texto) {
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
		
		return fraseContemListaPalavras(texto,palavrasComuns);

	}

	private static Boolean fraseContemListaPalavras(String texto, List<String> palavras) {

		for (String palavra : palavras) {
			if (texto.contains(palavra)) {
				return true;
			}

		}

		return false;
	}

}
