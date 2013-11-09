package br.ufrj.ppgi.grafo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import br.ufrj.ppgi.grafo.entidades.Mensagem;


public class LeitorMensagens {

	public static List<Mensagem> leLogMensagens(String log) {
		
		List<Mensagem> mensagens = new ArrayList<Mensagem>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					log)));
			String linha = "";

			while ((linha = reader.readLine()) != null) {
				Mensagem mensagem = new Mensagem();
				mensagem.setTexto(linha);
				mensagens.add(mensagem);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mensagens;

	}
}
