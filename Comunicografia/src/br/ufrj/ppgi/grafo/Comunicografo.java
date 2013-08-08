package br.ufrj.ppgi.grafo;
import java.util.List;

import br.ufrj.ppgi.grafo.entidades.Mensagem;

public class Comunicografo {

    public List<Mensagem> mensagensPequenas(List<Mensagem> mensagens) {

	for (int i = 0; i < mensagens.size(); i++) {
	    Mensagem mensagem = mensagens.get(i);

	    if (mensagem.getTexto().length() == 1) {
		mensagem.setReferencia(mensagens.get(i - 1));
	    }
	}
	return mensagens;

    }

    public List<Mensagem> acrescentaReferenciasPorPalavrasRepetidas(List<Mensagem> mensagens) {
	// Itera nas mensagens de maneira crescente
	for (int i = 0; i < mensagens.size(); i++) {
	    Mensagem mensagem = mensagens.get(i);

	    String[] palavras = mensagem.getTexto().split(" ");

	    // Para cada mensagem pega todas as anteriores a ela
	    for (int j = i - 1; mensagens.get(j) != null; j--) {
		Mensagem mensagemAnterior = mensagens.get(j);

		// Procura no texto da mensagem anterior se existe alguma palavra repetida
		String[] palavrasMsgAnterior = mensagemAnterior.getTexto().split(" ");

		for (int palavraMsg = 0; i < palavras.length; palavraMsg++) {
		    for (int palavraMsgAnterior = 0; i < palavrasMsgAnterior.length; palavraMsgAnterior++) {
			if (palavras[palavraMsg].equals(palavrasMsgAnterior[palavraMsgAnterior])) {
			    mensagem.setReferencia(mensagemAnterior);
			}
		    }

		}

	    }
	}
	return mensagens;

    }

}
