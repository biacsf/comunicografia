package br.ufrj.ppgi.recomendacao;

import java.util.List;

import br.ufrj.ppgi.grafo.LeMensagensXML;
import br.ufrj.ppgi.grafo.entidades.Discussao;
import br.ufrj.ppgi.grafo.entidades.Mensagem;

public class GeraRecomendacoesLogs {

	private void reocmendacoesLogs(){
		
		LeMensagensXML leMsgs = new LeMensagensXML();
			
		List<Discussao> discussoes = leMsgs.leituraXML();
		for(Discussao discussao:(List<Discussao>) discussoes)
		{
		
			List<Mensagem> mensagens = discussao.getMensagem();				
				
			for(Mensagem mensagem:mensagens)
			{
				//System.out.println(mensagem.getUsuario()+" : "+DbpediaSpotlight.recuperaRecomendacoes(mensagem.getTexto()));
			}
	
		}
		
	}
}
