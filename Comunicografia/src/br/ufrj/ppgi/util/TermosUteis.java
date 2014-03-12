package br.ufrj.ppgi.util;

import java.util.ArrayList;
import java.util.List;

public class TermosUteis {

	public static String palavraAposTermosLegislacao(String texto) {
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
		
		

		return palavraAposListaPalavras(texto, palavrasComuns);
	}

	private static String palavraAposListaPalavras(String texto,
			List<String> palavras) {

		String retorno = "";
		for (String palavra : palavras) {
			if (texto.contains(palavra)) {
				String[] textoDividido = texto.split(" ");
				for (int i = 0; i <= (textoDividido.length - 2); i++) {
					if (textoDividido[i].toLowerCase().equals(
							palavra.toLowerCase())) {
						
						int count = i + 1;

						if (textoDividido[count].toLowerCase().equals("da")
								|| textoDividido[count].toLowerCase().equals(
										"do")
								|| textoDividido[count].toLowerCase().equals(
										"de")
								|| textoDividido[count].toLowerCase().equals(
										"d")
								|| textoDividido[count].toLowerCase().equals(
										"para")) {
							int number = count + 1;
							if (textoDividido.length > number + 1) {
								return palavra+" "+textoDividido[number] + " "
										+ textoDividido[number + 1];
							} else {
								if (textoDividido.length > number) {
									return palavra+" "+textoDividido[number];
								}
							}
						} else {
							String possivelNumero = recuperaNumeroLei(textoDividido[count]);
							if (possivelNumero.equals("")) {
								int number = count;
								if (textoDividido.length > (number + 2)) {
									return palavra+" "+textoDividido[number] + " "
											+ textoDividido[number + 1] + " "
											+ textoDividido[number + 2];

								} else {
									if (textoDividido.length > (number + 1)) {
										return palavra+" "+textoDividido[number] + " "
												+ textoDividido[number + 1];

									} else {
										if (textoDividido.length > number) {
											return palavra+" "+textoDividido[number];

										}
									}

								}
							} else {
								return palavra+" "+textoDividido[count];
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

		return true;

	}

	private static String recuperaNumeroLei(String texto) {
		if (texto.matches(".*\\d.*")) {
			String[] palavras = texto.split(" ");
			for (String palavra : palavras) {
				if (palavra.matches(".*\\d.*")) {
					return palavra;
				}
			}
		}
		return "";

	}

}