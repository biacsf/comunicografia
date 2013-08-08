package br.ufrj.ppgi.grafo;

import java.util.List;

import br.ufrj.br.ppgi.preProcessamento.TratamentoTextual;
import br.ufrj.ppgi.grafo.entidades.Discussao;
import br.ufrj.ppgi.grafo.entidades.Mensagem;

public class MainLeveinstein {

	public static void main(String[] args) {
		LeMensagensXML leMsgs = new LeMensagensXML();
		List<Discussao> discussoes = leMsgs.leituraXML();
		int contaReferencias = 0;
		for(Discussao discussao:(List<Discussao>) discussoes)
		{
			try {
				List<Mensagem> mensagens = TratamentoTextual.executaTratamentosDiscussao(discussao.getMensagem());
				mensagens = ConstrutorGrafo.mensagensPequenas(mensagens);
				mensagens = ConstrutorGrafo.acrescentaReferenciaPorMaiorSimilaridade(mensagens);
				discussao.setMensagens(mensagens);
				
				System.out.println("/**DIALOGO**/");
				System.out.println();
				for(Mensagem mensagem:(List<Mensagem>) mensagens)
				{
					System.out.println("/**Mensagem**/");
					if(mensagem.getNumero() != null)
					{
						System.out.println("Numero: "+mensagem.getNumero());
					}
					if(mensagem.getTexto() != null)
					{
						System.out.println("Texto: "+mensagem.getTexto());
					}
					if(mensagem.getTipo() != null)
					{
						System.out.println("Tipo: "+mensagem.getTipo());
					}
					if(mensagem.getReferencia() != null && mensagem.getReferencia().getNumero() != null)
					{
						System.out.println("Referencia: "+mensagem.getReferencia().getNumero());
						contaReferencias++;
					}
					if(mensagem.getReferenciaTreinamento() != null)
					{
						System.out.println("Referencia Treinamento: "+mensagem.getReferenciaTreinamento());
					}
					System.out.println("/*FIM MENSAGEM*/");
				}
				System.out.println("/*FIM DIALOGO*/");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		System.out.println("Referencias encontradas: "+contaReferencias);

}

}
