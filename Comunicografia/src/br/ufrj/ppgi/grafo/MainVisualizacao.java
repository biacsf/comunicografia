package br.ufrj.ppgi.grafo;

import java.util.List;

import br.ufrj.ppgi.grafo.entidades.Discussao;
import br.ufrj.ppgi.grafo.entidades.Mensagem;

public class MainVisualizacao {

	public static void main(String[] args) {
		LeMensagensXML leMsgs = new LeMensagensXML();
		List<Discussao> discussoes = leMsgs.leituraXML();
		int i=0;
		for(Discussao discussao: discussoes)
		{
			for(Mensagem mensagem: discussao.getMensagem())
			{
				System.out.println(mensagem.getNumero()+" "+mensagem.getUsuario()+": "+mensagem.getTexto());
				if(mensagem.getReferenciaTreinamento() != null && !mensagem.getReferenciaTreinamento().equals(""))
				{
					//System.out.println("faz referencia a mensagem: "+mensagem.getReferenciaTreinamento());
				}
				
			}
		}
	}
	

}
