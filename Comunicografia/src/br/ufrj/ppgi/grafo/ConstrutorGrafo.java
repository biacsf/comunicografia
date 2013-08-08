package br.ufrj.ppgi.grafo;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import br.ufrj.ppgi.grafo.entidades.Mensagem;

public class ConstrutorGrafo {

	public static List<Mensagem> mensagensPequenas(List<Mensagem> mensagens) {

		for (int i = 0; i < mensagens.size(); i++) {
			Mensagem mensagem = mensagens.get(i);

			if (mensagem.getTexto().length() == 1
					&& mensagem.getReferencia() == null) {
				mensagem.setReferencia(mensagens.get(i - 1));
			}
		}
		return mensagens;

	}

	public static List<Mensagem> acrescentaReferenciaPorMaiorSimilaridade(
			List<Mensagem> mensagens) {
		SortedMap<Integer, Mensagem> mapaSimilaridade = new TreeMap<Integer, Mensagem>();
		// Itera nas mensagens de maneira crescente
		for (int i = 0; i < mensagens.size(); i++) {
			Mensagem mensagem = mensagens.get(i);
			if (mensagem.getReferencia() == null && i>0) {
				// Para cada mensagem pega todas as anteriores a ela
				for (int j = i - 1; j>0; j--) {
					if(mensagens.get(j) != null)
					{
						Mensagem mensagemAnterior = mensagens.get(j);
	
						// Calcula a similaridade entre as mensagens
						int similaridade = LevenshteinDistance
								.computeLevenshteinDistance(mensagem.getTexto(),
										mensagemAnterior.getTexto());
						mapaSimilaridade.put(similaridade, mensagemAnterior);
					}

				}

				// Seta a referencia para a mensagem anterior de maior
				// similaridade
				if(!mapaSimilaridade.isEmpty())
				{
					mensagem.setReferencia(mapaSimilaridade.get(mapaSimilaridade
						.firstKey()));
				}
			}
		}
		return mensagens;
	}

	public static List<Mensagem> acrescentaReferenciasPorPalavrasRepetidas(
			List<Mensagem> mensagens) {
		// Itera nas mensagens de maneira crescente
		for (int i = 0; i < mensagens.size(); i++) {
			Mensagem mensagem = mensagens.get(i);

			if (mensagem.getReferencia() == null && i>0) {
				String[] palavras = mensagem.getTexto().split(" ");

				// Para cada mensagem pega todas as anteriores a ela
				for (int j = i - 1; j>0; j--) {
					if(mensagens.get(j) != null)
					{
					Mensagem mensagemAnterior = mensagens.get(j);

					// Procura no texto da mensagem anterior se existe alguma
					// palavra repetida
					String[] palavrasMsgAnterior = mensagemAnterior.getTexto()
							.split(" ");

					for (int palavraMsg = 0; palavraMsg < palavras.length-1; palavraMsg++) {
						for (int palavraMsgAnterior = 0; palavraMsgAnterior < palavrasMsgAnterior.length-1; palavraMsgAnterior++) {
							if (palavras[palavraMsg].equals(palavrasMsgAnterior[palavraMsgAnterior])) {
								mensagem.setReferencia(mensagemAnterior);
								break;
							}

						}
					}

				}
				}
			}

		}
		return mensagens;
	}
}
