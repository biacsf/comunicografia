package br.ufrj.ppgi.grafo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import br.ufrj.ppgi.grafo.entidades.Discussao;
import br.ufrj.ppgi.grafo.entidades.Mensagem;

public class LeMensagensXML {
	/**
	 * Le os logs do diretorio para usar os logs como treinamento
	 */
	public List<Discussao> leituraXML() {

		String diretorio = "src/recursos/recomendacao";
		List<Discussao> discussoes = new ArrayList<Discussao>();

		try {
			JAXBContext context = JAXBContext
					.newInstance("br.ufrj.ppgi.grafo.entidades");
			Unmarshaller unmarshaller = context.createUnmarshaller();

			// Busca todos os logs salvos no diretorio
			File file = new File(diretorio);
			File afile[] = file.listFiles();
			for (int j = 0; j < afile.length; j++) {

				
				Discussao discussao = new Discussao();
				File arquivos = afile[j];
				
				System.out.println(arquivos.getPath());
				if (!arquivos.getPath().contains(".DS_Store")) {

					// Recupera a discussao
					JAXBElement<Discussao> element = (JAXBElement<Discussao>) unmarshaller
							.unmarshal(new File(arquivos.getPath()));

					// Recupera as mensagens enviadas nesse log
					List<Mensagem> mensagens = element.getValue().getMensagem();
					discussao.setMensagens(mensagens);
					discussao.setTitulo(element.getValue().getTitulo());
					discussao.setData(element.getValue().getData());
					discussoes.add(discussao);
					System.out.println("O arquivo "+arquivos.getPath() +" foi lido!");
					System.out.println("O arquivo tem "+discussao.getMensagem().size()+" mensagens");
				}

			}

		} catch (JAXBException e) {

			e.printStackTrace();
		}
		return discussoes;
	}

	/**
	 * 
	 * @param nomeArquivo
	 * 
	 *            Le um log para testar a classificacao das mensagens
	 *            relacionadas
	 */
	public static List<Discussao> leituraXML(String nomeArquivo) {

		String diretorio = nomeArquivo;
		List<Discussao> discussoes = new ArrayList<Discussao>();

		try {
			JAXBContext context = JAXBContext
					.newInstance("br.ufrj.ppgi.grafo.entidades");
			Unmarshaller unmarshaller = context.createUnmarshaller();

			// Busca todos os logs salvos no diretorio
			File file = new File(diretorio);
			File afile[] = file.listFiles();
			if(afile != null)
			{
				for (int j = 0; j < afile.length; j++) {
	
					
					Discussao discussao = new Discussao();
					File arquivos = afile[j];
					
					System.out.println(arquivos.getPath());
					if (!arquivos.getPath().contains(".DS_Store")) {
	
						// Recupera a discussao
						JAXBElement<Discussao> element = (JAXBElement<Discussao>) unmarshaller
								.unmarshal(new File(arquivos.getPath()));
	
						// Recupera as mensagens enviadas nesse log
						List<Mensagem> mensagens = element.getValue().getMensagem();
						discussao.setMensagens(mensagens);
						discussao.setTitulo(element.getValue().getTitulo());
						discussao.setData(element.getValue().getData());
						discussoes.add(discussao);
						System.out.println("O arquivo "+arquivos.getPath() +" foi lido!");
					}
				}

			}

		} catch (JAXBException e) {

			e.printStackTrace();
		}
		return discussoes;
	}
}
