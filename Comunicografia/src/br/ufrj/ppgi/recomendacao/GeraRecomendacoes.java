package br.ufrj.ppgi.recomendacao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.List;

import br.ufrj.br.ppgi.preProcessamento.TratamentoTextual;
import br.ufrj.ppgi.grafo.LeMensagensXML;
import br.ufrj.ppgi.grafo.entidades.Discussao;
import br.ufrj.ppgi.grafo.entidades.Mensagem;

public class GeraRecomendacoes {

	public static String geraRecomendacao(String texto, String usuario,String idDiscussao, String diretorio) {
		String entidadesAnteriores = "";
		String textoAnterior = "";
		String textoSalvar = "";
		try {
			textoSalvar = TratamentoTextual
					.executaTratamentosTextoParaSalvar(texto);
		} catch (Exception e1) {

			System.out.println("Erro ao tratar mensagem: " + texto);
		}

		String[] idDividido = idDiscussao.split("@");
		idDiscussao = idDividido[0];

		// Diretorio para salvar as mensagens enviadas na discussao
		String diretorioPalavras = diretorio+"recursos/palavras";
		diretorio = diretorio + "recursos/recomendacao";
		

		// Recupera as entidades nomeadas anteriormente
		try {
			File arquivoEntidades = new File(diretorioPalavras+ "/palavras.xml");
			if (!arquivoEntidades.exists()) {
				arquivoEntidades.createNewFile();
				arquivoEntidades.setWritable(true);
			}
			BufferedReader br = new BufferedReader(new FileReader(
					arquivoEntidades));
			try {
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();

				while (line != null) {
					sb.append(line);
					line = br.readLine();
				}
				entidadesAnteriores = sb.toString();
			} finally {
				br.close();
			}
		} catch (Exception e) {
			System.out.println("Erro ler arquivo palavras "+e.getMessage());
		}

		// Le as mensagens gravadas no diretorio
		List<Discussao> discussoesGravadas = LeMensagensXML
				.leituraXML(diretorio);

		Discussao discussaoAtual = new Discussao();

		/* VERIFICA SE JA FOI GRAVADA ALGUMA MENSAGEM PARA A DISCUSSAO */
		if (discussoesGravadas != null && !discussoesGravadas.isEmpty()) {
			for (Discussao discussao : discussoesGravadas) {
				if (discussao.getTitulo() != null
						&& discussao.getTitulo().equals(idDiscussao)) {
					// Recupera a discussao atual
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
				if ((discussaoAtual.getMensagem().size() - Integer
						.valueOf(mensagem.getNumero())) <= 5) {
					textoAnterior += " " + mensagem.getTexto();
				}
			}
			Integer numero = Integer.valueOf(ultimoNumero);
			numero++;

			xml += "<mensagem><numero>" + String.valueOf(numero)
					+ "</numero><texto>" + textoSalvar + "</texto><usuario>"
					+ usuario + "</usuario></mensagem></discussao>";
			try {
				File arquivo = new File(diretorio + "/" + idDiscussao + ".xml");
				FileOutputStream fos = new FileOutputStream(arquivo);
				fos.write(xml.getBytes());
				fos.close();
			} catch (Exception e) {
				System.out.println("Erro ao escrever no log " + e);
			}
		} else {
			try {
				File arquivo = new File(diretorio + "/" + idDiscussao + ".xml");
				if (!arquivo.exists()) {
					arquivo.createNewFile();
					arquivo.setWritable(true);
				}
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
				System.out.println("Erro ao escrever no log " + e);
			}
		}

		textoAnterior += " " + texto;
		try {
			textoAnterior = TratamentoTextual
					.executaTratamentosTextoParaSimilaridade(textoAnterior);
			entidadesAnteriores = TratamentoTextual
			.executaTratamentosTextoParaSimilaridade(entidadesAnteriores);
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* GERA AS RECOMENDACOES PARA A MENSAGEM ATUAL */
		System.out.println("Entidades anteriores utilizadas "+entidadesAnteriores);
		String recomendacao = DbpediaSpotlight.recuperaRecomendacoes(texto,
				diretorioPalavras, textoAnterior, entidadesAnteriores);

		return recomendacao;

	}
}
