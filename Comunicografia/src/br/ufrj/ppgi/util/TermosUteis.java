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
		palavrasComuns.add("crime");
		palavrasComuns.add("destruição");
		palavrasComuns.add("destruicao");
		palavrasComuns.add("destruiçao");
		palavrasComuns.add("denuncia");
		palavrasComuns.add("denúncia");
		palavrasComuns.add("denuncias");
		palavrasComuns.add("regulamenta");
		palavrasComuns.add("regulamentar");
		palavrasComuns.add("ação");
		palavrasComuns.add("acao");
		palavrasComuns.add("proibe");
		palavrasComuns.add("proíbe");
		palavrasComuns.add("proibição");
		palavrasComuns.add("proíbição");
		palavrasComuns.add("proibir");
		palavrasComuns.add("proibicão");
		palavrasComuns.add("proibicao");
		palavrasComuns.add("proíbicao");
		palavrasComuns.add("proíbiçao");
		palavrasComuns.add("proibido");
		palavrasComuns.add("dever");
		palavrasComuns.add("direito");
		palavrasComuns.add("liberdade");
		palavrasComuns.add("incitação");
		palavrasComuns.add("incitacao");
		palavrasComuns.add("incitar");
		palavrasComuns.add("abuso");
		palavrasComuns.add("autoridade");
		palavrasComuns.add("fiscalização");
		palavrasComuns.add("fiscalizacao");
		palavrasComuns.add("direitos");
		palavrasComuns.add("direito");

		return fraseContemListaPalavras(texto, palavrasComuns);

	}

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
		palavrasComuns.add("resolucao");
		palavrasComuns.add("resoluçao");
		palavrasComuns.add("resolucão");
		palavrasComuns.add("crime");
		palavrasComuns.add("destruição");
		palavrasComuns.add("destruicao");
		palavrasComuns.add("destruiçao");
		palavrasComuns.add("denuncia");
		palavrasComuns.add("denúncia");
		palavrasComuns.add("denuncias");
		palavrasComuns.add("regulamenta");
		palavrasComuns.add("regulamentar");
		palavrasComuns.add("ação");
		palavrasComuns.add("acao");
		palavrasComuns.add("proibe");
		palavrasComuns.add("proíbe");
		palavrasComuns.add("proibição");
		palavrasComuns.add("proíbição");
		palavrasComuns.add("proibir");
		palavrasComuns.add("proibicão");
		palavrasComuns.add("proibicao");
		palavrasComuns.add("proíbicao");
		palavrasComuns.add("proíbiçao");
		palavrasComuns.add("proibido");
		palavrasComuns.add("dever");
		palavrasComuns.add("direito");
		palavrasComuns.add("liberdade");
		palavrasComuns.add("incitação");
		palavrasComuns.add("incitacao");
		palavrasComuns.add("incitar");
		palavrasComuns.add("abuso");
		palavrasComuns.add("autoridade");
		palavrasComuns.add("fiscalização");
		palavrasComuns.add("fiscalizacao");
		palavrasComuns.add("direitos");
		palavrasComuns.add("direito");

		return palavraAposListaPalavras(texto, palavrasComuns);
	}

	private static String palavraAposListaPalavras(String texto,
			List<String> palavras) {

		String retorno = "";
		for (String palavra : palavras) {
			if (texto.contains(palavra)) {
				String[] textoDividido = texto.split(" ");
				for (int i = 0; i <= (textoDividido.length - 2); i++) {
					if (textoDividido[i].toLowerCase().equals(palavra.toLowerCase())) {
						int count = i + 1;

						if (textoDividido[count].toLowerCase().equals("da")
								|| textoDividido[count].toLowerCase().equals("do")
								|| textoDividido[count].toLowerCase().equals("de")
								|| textoDividido[count].toLowerCase().equals("d")
								|| textoDividido[count].toLowerCase().equals("para")) {
							int number = count + 1;
							if (textoDividido.length > number) {
								return textoDividido[number];
							}
						} else {
							int number = count;
							if (textoDividido.length > (number + 2)) {
								return textoDividido[number] + " "
										+ textoDividido[number + 1] + " "
										+ textoDividido[number + 2];

							} else {
								if (textoDividido.length > (number + 1)) {
									return textoDividido[number] + " "
											+ textoDividido[number + 1];

								} else {
									if (textoDividido.length > number) {
										return textoDividido[number];

									}
								}

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

		return fraseContemListaPalavras(texto, palavrasComuns);

	}

	public static Boolean contemPalavra(String texto) {
		List<String> palavrasComuns = new ArrayList<String>();
		palavrasComuns.add("manifestação");
		palavrasComuns.add("manifestacao");
		palavrasComuns.add("manifestaçao");
		palavrasComuns.add("vandalismo");
		// palavrasComuns.add("via");
		palavrasComuns.add("público");
		palavrasComuns.add("pública");
		palavrasComuns.add("publico");
		palavrasComuns.add("publica");
		palavrasComuns.add("infiltados");
		palavrasComuns.add("infiltado");
		palavrasComuns.add("imprensa");
		palavrasComuns.add("informacao");
		palavrasComuns.add("informação");
		palavrasComuns.add("democracia");
		palavrasComuns.add("reforma");
		palavrasComuns.add("constituição");
		palavrasComuns.add("constituicao");
		palavrasComuns.add("abordagem");
		palavrasComuns.add("proibição");
		palavrasComuns.add("proíbição");
		palavrasComuns.add("proibir");
		palavrasComuns.add("proibicão");
		palavrasComuns.add("proibicao");
		palavrasComuns.add("proíbicao");
		palavrasComuns.add("proíbiçao");
		palavrasComuns.add("proibido");
		palavrasComuns.add("dever");
		palavrasComuns.add("direito");
		palavrasComuns.add("liberdade");
		palavrasComuns.add("incitação");
		palavrasComuns.add("incitacao");
		palavrasComuns.add("incitar");
		palavrasComuns.add("abuso");
		palavrasComuns.add("autoridade");
		palavrasComuns.add("fiscalização");
		palavrasComuns.add("fiscalizacao");
		palavrasComuns.add("direitos");
		palavrasComuns.add("direito");
		palavrasComuns.add("regulamenta");
		palavrasComuns.add("regulamentar");
		palavrasComuns.add("formação de quadrilha");
		palavrasComuns.add("formacao de quadrilha");
		palavrasComuns.add("corrupção");
		palavrasComuns.add("incriminar");

		return fraseContemListaPalavras(texto, palavrasComuns);
	}

	public static String palavraAposPalavra(String texto) {
		List<String> palavrasComuns = new ArrayList<String>();
		palavrasComuns.add("manifestação");
		palavrasComuns.add("manifestacao");
		palavrasComuns.add("manifestaçao");
		palavrasComuns.add("vandalismo");
		// palavrasComuns.add("via");
		palavrasComuns.add("público");
		palavrasComuns.add("pública");
		palavrasComuns.add("publico");
		palavrasComuns.add("publica");
		palavrasComuns.add("infiltados");
		palavrasComuns.add("infiltado");
		palavrasComuns.add("imprensa");
		palavrasComuns.add("informacao");
		palavrasComuns.add("informação");
		palavrasComuns.add("democracia");
		palavrasComuns.add("reforma");
		palavrasComuns.add("constituição");
		palavrasComuns.add("constituicao");
		palavrasComuns.add("abordagem");
		palavrasComuns.add("proibição");
		palavrasComuns.add("proíbição");
		palavrasComuns.add("proibir");
		palavrasComuns.add("proibicão");
		palavrasComuns.add("proibicao");
		palavrasComuns.add("proíbicao");
		palavrasComuns.add("proíbiçao");
		palavrasComuns.add("proibido");
		palavrasComuns.add("dever");
		palavrasComuns.add("direito");
		palavrasComuns.add("liberdade");
		palavrasComuns.add("incitação");
		palavrasComuns.add("incitacao");
		palavrasComuns.add("incitar");
		palavrasComuns.add("abuso");
		palavrasComuns.add("autoridade");
		palavrasComuns.add("fiscalização");
		palavrasComuns.add("fiscalizacao");
		palavrasComuns.add("direitos");
		palavrasComuns.add("direito");
		palavrasComuns.add("regulamenta");
		palavrasComuns.add("regulamentar");
		palavrasComuns.add("formação de quadrilha");
		palavrasComuns.add("formacao de quadrilha");
		palavrasComuns.add("corrupção");
		palavrasComuns.add("incriminar");

		return palavraeAposListaPalavras(texto, palavrasComuns);
	}

	private static String palavraeAposListaPalavras(String texto,
			List<String> palavras) {

		String retorno = "";
		for (String palavra : palavras) {
			if (texto.toLowerCase().contains(palavra)) {
				String[] textoDividido = texto.split(" ");
				for (int i = 0; i <= (textoDividido.length - 2); i++) {
					if (textoDividido[i].toLowerCase().equals(palavra)) {
						int count = i;
						if (textoDividido.length > (count+1))
						{
							return textoDividido[count]+textoDividido[count+1];
						}else{
							return textoDividido[count];
						}

					}

				}
			}
		}

		return retorno;
	}

	private static Boolean fraseContemListaPalavras(String texto,
			List<String> palavras) {

		for (String palavra : palavras) {
			if (texto.toLowerCase().contains(palavra.toLowerCase())) {
				return true;
			}

		}

		return false;
	}

}