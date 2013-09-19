package br.ufrj.ppgi.recomendacao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import br.ufrj.br.ppgi.preProcessamento.TratamentoTextual;
import br.ufrj.ppgi.grafo.LeMensagensXML;
import br.ufrj.ppgi.grafo.entidades.Discussao;
import br.ufrj.ppgi.grafo.entidades.Mensagem;

public class GeraRecomendacoes {

	public static String geraRecomendacao(String texto, String usuario,
			String idDiscussao) {
		String textoSalvar="";
		try {
			 textoSalvar = TratamentoTextual.executaTratamentosTextoParaSalvar(texto);
		} catch (Exception e1) {

			System.out.println("Erro ao tratar mensagem: "+ texto);
		}
		String diretorio = "src/recursos/recomendacao";

		List<Discussao> discussoesGravadas = LeMensagensXML
				.leituraXML(diretorio);

		Discussao discussaoAtual = new Discussao();

		/* VERIFICA SE JA FOI GRAVADA ALGUMA MENSAGEM PARA A DISCUSSAO */
		if (discussoesGravadas != null && !discussoesGravadas.isEmpty()) {
			for (Discussao discussao : discussoesGravadas) {
				if (discussao.getTitulo() != null
						&& discussao.getTitulo().equals(idDiscussao)) {
					discussaoAtual = discussao;
				}
			}
		}

		/* GRAVA A MENSAGEM ATUAL NA DISCUSSAO */
		if (discussaoAtual != null && discussaoAtual.getTitulo() != null) {
			String ultimoNumero = "";
			String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><discussao><titulo>"
					+ idDiscussao + "</titulo><data></data>";
			for (Mensagem mensagem : discussaoAtual.getMensagem()) {
				try {
					mensagem = TratamentoTextual
							.executaTratamentosMensagemParaSalvar(mensagem);
				} catch (Exception e) {
					System.out.println("Erro tratar mensagem: " + mensagem);
				}
				xml += "<mensagem><numero>" + mensagem.getNumero()
						+ "</numero><texto>" + mensagem.getTexto()
						+ "</texto><usuario>" + mensagem.getUsuario()
						+ "</usuario></mensagem>";
				ultimoNumero = mensagem.getNumero();
			}
			Integer numero = Integer.valueOf(ultimoNumero);
			numero++;

			xml += "<mensagem><numero>" + String.valueOf(numero)
					+ "</numero><texto>"
					+ textoSalvar
					+ "</texto><usuario>" + usuario
					+ "</usuario></mensagem></discussao>";
			try {
				File arquivo = new File("src/recursos/recomendacao/"
						+ idDiscussao + ".xml");
				FileOutputStream fos = new FileOutputStream(arquivo);
				fos.write(xml.getBytes());
				fos.close();
			} catch (Exception e) {
				System.out.println("Erro ao escrever no log");
			}
		} else {
			try {
				File arquivo = new File("src/recursos/recomendacao/"
						+ idDiscussao + ".xml");
				FileOutputStream fos = new FileOutputStream(arquivo);
				String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><discussao><titulo>"
						+ idDiscussao
						+ "</titulo><data></data><mensagem><numero>1</numero><texto>"
						+ textoSalvar
						+ "</texto><usuario>"
						+ usuario
						+ "</usuario></mensagem></discussao>";
				fos.write(xml.getBytes());
				fos.close();
			} catch (Exception e) {
				System.out.println("Erro ao escrever no log");
			}
		}

		/* Estrutura a mensagem de acordo com as referencias */

		/* GERA AS RECOMENDACOES PARA A MENSAGEM ATUAL */
		String recomendacao = DbpediaSpotlight.recuperaRecomendacoes(texto);
		
		System.out.println(recomendacao);

		return recomendacao;

	}

}
