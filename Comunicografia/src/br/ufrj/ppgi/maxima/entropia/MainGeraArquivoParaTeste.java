package br.ufrj.ppgi.maxima.entropia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import br.ufrj.br.ppgi.preProcessamento.TratamentoTextual;
import br.ufrj.ppgi.grafo.LeMensagensXML;
import br.ufrj.ppgi.grafo.entidades.Discussao;
import br.ufrj.ppgi.grafo.entidades.Mensagem;

public class MainGeraArquivoParaTeste {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		LeMensagensXML leMsgs = new LeMensagensXML();
		
		// Gravando no arquivo
		File arquivo;

		arquivo = new File("src/recursos/treinamento/teste.dat");
		String diretorio = "src/recursos/teste";
		FileOutputStream fos = new FileOutputStream(arquivo);
		
		List<Discussao> discussoes = leMsgs.leituraXML(diretorio);
		for(Discussao discussao:(List<Discussao>) discussoes)
		{
		
				List<Mensagem> mensagens = TratamentoTextual.executaTratamentosDiscussao(discussao.getMensagem());
				
				GeraArquivoFeatures.geraFeaturesDiscussaoTeste(mensagens,fos);
				
	
		}
		fos.close();
		Predict.predictMessagesRelations(arquivo.toString());
	}
}
