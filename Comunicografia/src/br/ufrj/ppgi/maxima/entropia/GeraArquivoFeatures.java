package br.ufrj.ppgi.maxima.entropia;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufrj.ppgi.grafo.entidades.Mensagem;

public class GeraArquivoFeatures {

	//Deprecated
	public static void geraFeaturesDiscussao(List<Mensagem> mensagens, FileOutputStream fos) {

		
		try {
			

			for (Mensagem mensagem : (List<Mensagem>) mensagens) {

				

				String[] palavras = mensagem.getTexto().split(" ");

				// Recupera as features de cada mensagem que faca referencia para uma anterior (apenas analisamos as mensagens que fazem referencias)
				if (mensagem.getReferenciaTreinamento() != null && !mensagem.getReferenciaTreinamento().equals("")) {
					
					
					int idMensagemTreinamento = Integer.valueOf(mensagem
							.getReferenciaTreinamento())-1;
					
					//Percorre da mensagem inicial até a mensagem anterior a essa mensagem a qual ela faz referencia
					for(int i=0;i<=idMensagemTreinamento; i++ )
					{
						/*
						 * A mensagem B é a mensagem para qual estamos procurando
						 * referencia A
						 */
						Boolean parAdjacencia;
						Integer numeroMsgsEntreAeB;
						Integer numeroPalavrasEmA;
						Integer numeroPalavrasEmAeB = 0;
						String primeiraPalavraA;
						String ultimaPalavraA;
						Boolean mensagemATemNomeB = false;
						Boolean mensagemBTemNomeA = false;
						
						//Caso a mensagem recuperada nao seja a mensagem a qual esse mensagem faz referencia
						if(i != idMensagemTreinamento)
						{
							parAdjacencia = false;
						}else{
							parAdjacencia = true;
						}
						//Recupera a mensagem que queremos saber se eh a referencia
						Mensagem mensagemA = mensagens.get(i);
						String[] palavrasMensagemA = mensagemA.getTexto()
								.split(" ");

						
						//FEATURES
						
						//numero de mensagens em A e B
						numeroMsgsEntreAeB = Integer.valueOf(mensagem.getNumero())
								- Integer.valueOf(mensagemA.getNumero())-1;
						
						//Numero de palavras na mensagem anterior
						numeroPalavrasEmA = palavrasMensagemA.length;

						for (int palavraMsg = 0; palavraMsg < palavras.length - 1; palavraMsg++) {
							for (int palavraMsgAnterior = 0; palavraMsgAnterior < palavrasMensagemA.length - 1; palavraMsgAnterior++) {
								if (palavras[palavraMsg]
										.equals(palavrasMensagemA[palavraMsgAnterior])) {
									//Numero de palavras iguais nas duas mensagens
									numeroPalavrasEmAeB++;
								}

							}
						}
						for (int palavraMsg = 0; palavraMsg < palavras.length - 1; palavraMsg++) {
							if (palavras[palavraMsg].equals(mensagemA.getUsuario())) {
								//Retorna se a mensagem B contem o nome do usuario que enviou a mensagem A em seu texto
								mensagemBTemNomeA = true;
							}
						}
						for (int palavraMsgAnterior = 0; palavraMsgAnterior < palavrasMensagemA.length - 1; palavraMsgAnterior++) {
							if (palavrasMensagemA[palavraMsgAnterior]
									.equals(mensagem.getUsuario())) {
								//Retorna se a mensagem A contem o nome do usuario que enviou a mensagem B em seu texto
								mensagemATemNomeB = true;
							}
						}
						
						primeiraPalavraA = palavrasMensagemA[0].replaceAll(" ","").replaceAll("/n", "");
						ultimaPalavraA = palavrasMensagemA[palavrasMensagemA.length-1].replaceAll(" ","").replaceAll("/n", "");
												
						if(parAdjacencia)
						{
							String texto = "numeroMsgsEntreAeB="+numeroMsgsEntreAeB+" "+"numeroPalavrasEmA="+numeroPalavrasEmA
							+ " " + "numeroPalavrasEmAeB="+numeroPalavrasEmAeB + " "
							+ "primeiraPalavraA="+primeiraPalavraA + " " + "ultimaPalavraA="+ultimaPalavraA+" "
							+ "mensagemATemNomeB="+mensagemATemNomeB.toString() +" "+"mensagemBTemNomeA="+ mensagemBTemNomeA.toString()+" "+
							"ehPar"+"\n";
							
							fos.write(texto.getBytes());
							
						}else{
							String texto = "numeroMsgsEntreAeB="+numeroMsgsEntreAeB + " " + "numeroPalavrasEmA="+numeroPalavrasEmA
							+ " " + "numeroPalavrasEmAeB="+numeroPalavrasEmAeB + " "
							+ "primeiraPalavraA="+primeiraPalavraA + " " + "ultimaPalavraA="+ultimaPalavraA+" "
							+ "mensagemATemNomeB="+mensagemATemNomeB.toString() +" "+"mensagemBTemNomeA="+ mensagemBTemNomeA.toString()+" "+
							"NaoEhPar"+"\n";
							fos.write(texto.getBytes());
						}
						

					}
						
					
					
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void geraFeaturesDiscussaoTreinamento(List<Mensagem> mensagens, FileOutputStream fos, Map<Integer, Integer> frequenciaDistancias) {

		int numeroMaximoDistanciaAdjacencia=0;
		
		try {
			

			for (Mensagem mensagem : (List<Mensagem>) mensagens) {

				

				String[] palavras = mensagem.getTexto().split(" ");

			
					
				//Percorre das 20 mensagens anteriores até a mensagem anterior a essa mensagem a qual ela faz referencia
				if(mensagem.getNumero() !=null && !mensagem.getNumero().equals(""))
				{
					Integer valor = Integer.valueOf(mensagem.getNumero())-20;
					if(valor < 0)
					{
						valor =0;
					}
					for(int i=Integer.valueOf(mensagem.getNumero())-1;i>=valor; i--)
					{
						/*
						 * A mensagem B é a mensagem para qual estamos procurando
						 * referencia A
						 */
						Integer numeroMsgsEntreAeB;
						Integer numeroPalavrasEmA;
						Integer numeroPalavrasEmB;
						Integer numeroPalavrasEmAeB = 0;
						String primeiraPalavraA;
						String ultimaPalavraA;
						String primeiraPalavraB;
						String ultimaPalavraB;
						Integer nGrams3PresentesEmAEB = 0;
						Integer nGrams4PresentesEmAEB = 0;
						Integer nGrams5PresentesEmAEB = 0;
						Boolean mensagemATemNomeB = false;
						Boolean mensagemBTemNomeA = false;

						
						//Recupera a mensagem que queremos saber se eh a referencia
						if(mensagens.get(i) != null)
						{
						Mensagem mensagemA = mensagens.get(i);
						String[] palavrasMensagemA = mensagemA.getTexto()
								.split(" ");

						
						//FEATURES
						
						//numero de mensagens em A e B
						numeroMsgsEntreAeB = Integer.valueOf(mensagem.getNumero())
								- Integer.valueOf(mensagemA.getNumero())-1;
						
						//Numero de palavras na mensagem anterior
						numeroPalavrasEmA = palavrasMensagemA.length;
						//Numero de palavras na mensagem atual
						numeroPalavrasEmB = palavras.length;

						for (int palavraMsg = 0; palavraMsg < palavras.length - 1; palavraMsg++) {
							for (int palavraMsgAnterior = 0; palavraMsgAnterior < palavrasMensagemA.length - 1; palavraMsgAnterior++) {
								if (palavras[palavraMsg]
										.equals(palavrasMensagemA[palavraMsgAnterior])) {
									//Numero de palavras iguais nas duas mensagens
									numeroPalavrasEmAeB++;
								}
								if(palavras[palavraMsg].length() >= 3 && palavrasMensagemA[palavraMsgAnterior].length()>=3 && 
										(palavras[palavraMsg].substring(0,3).equals(palavrasMensagemA[palavraMsgAnterior].substring(0, 3))))
								{
									nGrams3PresentesEmAEB++;
								}
								if(palavras[palavraMsg].length() >= 4 && palavrasMensagemA[palavraMsgAnterior].length()>=4 && 
										(palavras[palavraMsg].substring(0,4).equals(palavrasMensagemA[palavraMsgAnterior].substring(0, 4))))
								{
									nGrams4PresentesEmAEB++;
								}
								if(palavras[palavraMsg].length() >= 5 && palavrasMensagemA[palavraMsgAnterior].length()>=5 && 
										(palavras[palavraMsg].substring(0,5).equals(palavrasMensagemA[palavraMsgAnterior].substring(0, 5))))
								{
									nGrams5PresentesEmAEB++;
								}

							}
						}
						for (int palavraMsg = 0; palavraMsg < palavras.length - 1; palavraMsg++) {
							if (palavras[palavraMsg].equals(mensagemA.getUsuario())) {
								//Retorna se a mensagem B contem o nome do usuario que enviou a mensagem A em seu texto
								mensagemBTemNomeA = true;
							}
						}
						for (int palavraMsgAnterior = 0; palavraMsgAnterior < palavrasMensagemA.length - 1; palavraMsgAnterior++) {
							if (palavrasMensagemA[palavraMsgAnterior]
									.equals(mensagem.getUsuario())) {
								//Retorna se a mensagem A contem o nome do usuario que enviou a mensagem B em seu texto
								mensagemATemNomeB = true;
							}
						}
						
						primeiraPalavraA = palavrasMensagemA[0].replaceAll(" ","").replaceAll("/n", "");
						ultimaPalavraA = palavrasMensagemA[palavrasMensagemA.length-1].replaceAll(" ","").replaceAll("/n", "");
						primeiraPalavraB = palavras[0].replaceAll(" ","").replaceAll("/n", "");
						ultimaPalavraB = palavras[palavras.length-1].replaceAll(" ","").replaceAll("/n", "");
						
						Boolean parAdjacencia = false;
						
						if(mensagens.get(i) != null)
						{
							if(mensagem.getReferenciaTreinamento() != null && !mensagem.getReferenciaTreinamento().equals(""))
							{
								if(Integer.valueOf(mensagem.getReferenciaTreinamento()).equals(i))
								{
									parAdjacencia = true;
								}
							}
						
						if(parAdjacencia)
						{
							String texto = "numeroMsgsEntreAeB="+numeroMsgsEntreAeB
							 + " "+ "primeiraPalavraA="+primeiraPalavraA + " " + "ultimaPalavraA="+ultimaPalavraA+" "
							+ "primeiraPalavraB="+primeiraPalavraB + " " + "ultimaPalavraB="+ultimaPalavraB+" "
							+ "mensagemATemNomeB="+mensagemATemNomeB.toString() +" "+"mensagemBTemNomeA="+ mensagemBTemNomeA.toString()+" "+
							"ehPar"+"\n";
							if(numeroMsgsEntreAeB>numeroMaximoDistanciaAdjacencia)
							{
								numeroMaximoDistanciaAdjacencia = numeroMsgsEntreAeB;
							}
							if(frequenciaDistancias.containsKey(numeroMsgsEntreAeB))
							{
								frequenciaDistancias.put(numeroMsgsEntreAeB, frequenciaDistancias.get(numeroMsgsEntreAeB)+1);
							}else{
								frequenciaDistancias.put(numeroMsgsEntreAeB, 1);
							}
							fos.write(texto.getBytes());
							
						}else{
							String texto = "numeroMsgsEntreAeB="+numeroMsgsEntreAeB 
							 + " "+ "primeiraPalavraA="+primeiraPalavraA + " " + "ultimaPalavraA="+ultimaPalavraA+" "
							+ "primeiraPalavraB="+primeiraPalavraB + " " + "ultimaPalavraB="+ultimaPalavraB+" "
							+ "mensagemATemNomeB="+mensagemATemNomeB.toString() +" "+"mensagemBTemNomeA="+ mensagemBTemNomeA.toString()+" "+
							"NaoEhPar"+"\n";
							fos.write(texto.getBytes());
						}

					}
					}
					}
				}
					
					
				}
			

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("NUMERO MAXIMO="+numeroMaximoDistanciaAdjacencia);
	
	}

public static void geraFeaturesDiscussaoTeste(List<Mensagem> mensagens, FileOutputStream fos) {

	
	try {
		

		for (Mensagem mensagem : (List<Mensagem>) mensagens) {

			

			String[] palavras = mensagem.getTexto().split(" ");

		
			//Percorre das 20 mensagens anteriores até a mensagem anterior a essa mensagem a qual ela faz referencia
			if(mensagem.getNumero() !=null && !mensagem.getNumero().equals(""))
			{
				Integer valor = Integer.valueOf(mensagem.getNumero())-20;
				if(valor < 0)
				{
					valor =0;
				}
				for(int i=Integer.valueOf(mensagem.getNumero())-1;i>=valor; i--)
				{
					/*
					 * A mensagem B é a mensagem para qual estamos procurando
					 * referencia A
					 */
					Integer numeroMsgsEntreAeB;
					Integer numeroPalavrasEmA;
					Integer numeroPalavrasEmB;
					Integer numeroPalavrasEmAeB = 0;
					String primeiraPalavraA;
					String ultimaPalavraA;
					String primeiraPalavraB;
					String ultimaPalavraB;
					Boolean mensagemATemNomeB = false;
					Boolean mensagemBTemNomeA = false;
					Integer nGrams3PresentesEmAEB = 0;
					Integer nGrams4PresentesEmAEB = 0;
					Integer nGrams5PresentesEmAEB = 0;

					//Recupera a mensagem que queremos saber se eh a referencia
					if(mensagens.get(i) != null)
					{
						Mensagem mensagemA = mensagens.get(i);
						String[] palavrasMensagemA = mensagemA.getTexto()
								.split(" ");
						Boolean parAdjacencia = false;
						
						
							if(mensagem.getReferenciaTreinamento() != null && !mensagem.getReferenciaTreinamento().equals(""))
							{
								if(Integer.valueOf(mensagem.getReferenciaTreinamento()).equals(i))
								{
									parAdjacencia = true;
								}
							}

					
					//FEATURES
					
					//numero de mensagens em A e B
					numeroMsgsEntreAeB = Integer.valueOf(mensagem.getNumero())
							- Integer.valueOf(mensagemA.getNumero())-1;
					
					//Numero de palavras na mensagem anterior
					numeroPalavrasEmA = palavrasMensagemA.length;
					//Numero de palavras na mensagem atual
					numeroPalavrasEmB = palavras.length;
					for (int palavraMsg = 0; palavraMsg < palavras.length - 1; palavraMsg++) {
						for (int palavraMsgAnterior = 0; palavraMsgAnterior < palavrasMensagemA.length - 1; palavraMsgAnterior++) {
							if (palavras[palavraMsg]
									.equals(palavrasMensagemA[palavraMsgAnterior])) {
								//Numero de palavras iguais nas duas mensagens
								numeroPalavrasEmAeB++;
							}
							if(palavras[palavraMsg].length() >= 3 && palavrasMensagemA[palavraMsgAnterior].length()>=3 && 
									(palavras[palavraMsg].substring(0,3).equals(palavrasMensagemA[palavraMsgAnterior].substring(0, 3))))
							{
								nGrams3PresentesEmAEB++;
							}
							if(palavras[palavraMsg].length() >= 4 && palavrasMensagemA[palavraMsgAnterior].length()>=4 && 
									(palavras[palavraMsg].substring(0,4).equals(palavrasMensagemA[palavraMsgAnterior].substring(0, 4))))
							{
								nGrams4PresentesEmAEB++;
							}
							if(palavras[palavraMsg].length() >= 5 && palavrasMensagemA[palavraMsgAnterior].length()>=5 && 
									(palavras[palavraMsg].substring(0,5).equals(palavrasMensagemA[palavraMsgAnterior].substring(0, 5))))
							{
								nGrams5PresentesEmAEB++;
							}

						}
					}
					for (int palavraMsg = 0; palavraMsg < palavras.length - 1; palavraMsg++) {
						if (palavras[palavraMsg].equals(mensagemA.getUsuario())) {
							//Retorna se a mensagem B contem o nome do usuario que enviou a mensagem A em seu texto
							mensagemBTemNomeA = true;
						}
					}
					for (int palavraMsgAnterior = 0; palavraMsgAnterior < palavrasMensagemA.length - 1; palavraMsgAnterior++) {
						if (palavrasMensagemA[palavraMsgAnterior]
								.equals(mensagem.getUsuario())) {
							//Retorna se a mensagem A contem o nome do usuario que enviou a mensagem B em seu texto
							mensagemATemNomeB = true;
						}
					}
					
					primeiraPalavraA = palavrasMensagemA[0].replaceAll(" ","").replaceAll("/n", "");
					ultimaPalavraA = palavrasMensagemA[palavrasMensagemA.length-1].replaceAll(" ","").replaceAll("/n", "");
					primeiraPalavraB = palavras[0].replaceAll(" ","").replaceAll("/n", "");
					ultimaPalavraB = palavras[palavras.length-1].replaceAll(" ","").replaceAll("/n", "");
					
					String texto ="parAdjacencia-"+parAdjacencia.toString()+" numeroMsgsEntreAeB="+numeroMsgsEntreAeB 
							+ " "+ "primeiraPalavraA="+primeiraPalavraA + " " + "ultimaPalavraA="+ultimaPalavraA+" "
							+ "primeiraPalavraB="+primeiraPalavraB + " " + "ultimaPalavraB="+ultimaPalavraB+" "
							+ "mensagemATemNomeB="+mensagemATemNomeB.toString() +" "+"mensagemBTemNomeA="+ mensagemBTemNomeA.toString()+" ?"+"\n";
					fos.write(texto.getBytes());

				}
				}
			}
				
				
			}

		
	} catch (Exception e) {
		e.printStackTrace();
	}
}
}
