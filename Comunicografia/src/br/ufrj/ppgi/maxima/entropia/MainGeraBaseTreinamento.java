package br.ufrj.ppgi.maxima.entropia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufrj.br.ppgi.preProcessamento.TratamentoTextual;
import br.ufrj.ppgi.grafo.ConstrutorGrafo;
import br.ufrj.ppgi.grafo.LeMensagensXML;
import br.ufrj.ppgi.grafo.entidades.Discussao;
import br.ufrj.ppgi.grafo.entidades.Mensagem;

public class MainGeraBaseTreinamento {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		LeMensagensXML leMsgs = new LeMensagensXML();
		
		// Gravando no arquivo
		File arquivo;

		arquivo = new File("src/recursos/treinamento/treinamento.dat");
		FileOutputStream fos = new FileOutputStream(arquivo);
		Map<Integer, Integer> frequenciaDistancias = new HashMap<Integer , Integer>();

		List<Discussao> discussoes = leMsgs.leituraXML();
		for(Discussao discussao:(List<Discussao>) discussoes)
		{
		
				List<Mensagem> mensagens = TratamentoTextual.executaTratamentosDiscussao(discussao.getMensagem());
				
				
				GeraArquivoFeatures.geraFeaturesDiscussaoTreinamento(mensagens,fos,frequenciaDistancias);
				
				
				
	
		}
		fos.close();
		
		for(Integer key:frequenciaDistancias.keySet())
		{
			System.out.println("Numero de vezes que ocorreu a distancia "+key+" entre as mensagens: "+frequenciaDistancias);
		}
		CreateModel.geraModelo(arquivo.toString());

}

}
