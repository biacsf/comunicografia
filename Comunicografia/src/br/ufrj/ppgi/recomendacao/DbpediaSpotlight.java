package br.ufrj.ppgi.recomendacao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import br.ufrj.br.ppgi.preProcessamento.TratamentoTextual;
import br.ufrj.ppgi.util.TermosUteis;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;

public class DbpediaSpotlight {
	
	private static BigDecimal semanticSimilarity(String a,String b)
	{
		
		b = b.replace(",", "").replace(".", "").replace("?", "").replace("!", "").trim().replace("\"","\\\"");
		a = a.replace(",", "").replace(".", "").replace("?", "").replace("!", "").trim().replace("\"","\\\"");
		
		b = Normalizer.normalize(b, Normalizer.Form.NFD);
		b = b.replaceAll("[^\\p{ASCII}]", "").trim();
		
		a = Normalizer.normalize(a, Normalizer.Form.NFD);
		a = a.replaceAll("[^\\p{ASCII}]", "").trim();
		
		if(a.length() >= 120)
		{
			a = a.substring(0, 120);
		}
		if(b.length() >= 120)
		{
			b = b.substring(0, 120);
		}
		
		URIBuilder builder = new URIBuilder();
		builder.setScheme("https")
				.setHost("amtera.p.mashape.com/relatedness/pt");

		URI uri = null;
		try {
			uri = builder.build();

		} catch (URISyntaxException e1) {
			System.out.println("Erro ao conectar na URI");
			return null;
		}
		
		

		HttpPost httpget = new HttpPost(uri);
		httpget.setHeader("X-Mashape-Authorization", "I9yQM1U2l2D1UeyNw1Ms7J3oa9bQ8rxB");
		httpget.setHeader("Content-type", "application/json");
		
		StringEntity params = null;
		try {
			params = new StringEntity("{\"t1\":\""+a+"\",\"t2\":\""+b+"\"}");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		httpget.setEntity(params);


		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used.
		int timeoutConnection = 10000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 15000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);

		HttpResponse response = null;
		try {
			response = httpclient.execute(httpget);
		} catch (Exception e) {
			System.out.println("Erro ao recuperar resposta");
			return null;
		}
		
		HttpEntity entity = response.getEntity();

		JsonElement jelement = null;
		try {
			jelement = new JsonParser().parse(EntityUtils.toString(entity,
					HTTP.UTF_8).trim());
		} catch (Exception e) {

			return null;
		}
		JsonObject jobject = jelement.getAsJsonObject();
		JsonPrimitive textoJson = jobject.getAsJsonPrimitive("v");
		String texto = textoJson.getAsString();

		return new BigDecimal(texto);

	}


	private static Map<String, String> recuperaEntidades(String mensagem,
			Map<String, String> mapa) {

		URIBuilder builder = new URIBuilder();
		builder.setScheme("http")
				.setHost("access.alchemyapi.com/calls/text")
				.setPath("/TextGetRankedNamedEntities")
				.setParameter("apikey",
						"16787a7d136e97801ffc5fdbf3d3057f3b459afb")
				.setParameter("text", mensagem).setParameter("linkedData", "0");

		URI uri = null;
		try {
			uri = builder.build();

		} catch (URISyntaxException e1) {
			System.out.println("Erro ao conectar na URI");
			return mapa;
		}

		HttpGet httpget = new HttpGet(uri);
		httpget.setHeader("Accept", "application/xml");

		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used.
		int timeoutConnection = 10000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 15000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);

		HttpResponse response = null;
		try {
			response = httpclient.execute(httpget);
		} catch (Exception e) {
			System.out.println("Erro ao recuperar resposta");
			return mapa;
		}
		HttpEntity entity = response.getEntity();

		String xmlString = "";

		try {
			xmlString = EntityUtils.toString(entity, HTTP.UTF_8).trim();
		} catch (Exception e) {
			System.out.println("Erro ao recuperar resposta");
			return mapa;

		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		org.w3c.dom.Document doc = null;
		try {
			DocumentBuilder db = factory.newDocumentBuilder();
			InputSource inStream = new InputSource();
			inStream.setCharacterStream(new StringReader(xmlString));
			doc = db.parse(inStream);
		} catch (Exception e) {
			System.out.println("Erro ao converter resposta para xml");
			return mapa;

		}

		NodeList entities = doc.getElementsByTagName("entities");
		if (entities != null) {
			Element entit = (Element) entities.item(0);
			NodeList entidades = entit.getElementsByTagName("entity");
			for (int i = 0; i < entidades.getLength(); ++i) {
				Element record = (Element) entidades.item(i);

				NodeList recordDataList = record.getElementsByTagName("text");
				for (int j = 0; j < recordDataList.getLength(); ++j) {
					Element recordData = (Element) recordDataList.item(j);
					String palavras = recordData.getFirstChild().getNodeValue();

					String[] reco = palavras.split(" ");
					String ultimaPalavra = reco[(reco.length - 1)];
					if (ultimaPalavra != null && !ultimaPalavra.equals("")) {
						if (ultimaPalavra.equals("de")
								|| ultimaPalavra.equals("do")
								|| ultimaPalavra.equals("da")
								|| ultimaPalavra.equals("no")
								|| ultimaPalavra.equals("na")
								|| ultimaPalavra.equals("a")) {
							if (mensagem.contains(palavras)) {
								String palavraConcat = "";
								Boolean achou = false;
								for (int count = (mensagem.indexOf(palavras)
										+ palavras.length() + 1); count < mensagem
										.length(); count++) {
									if (String.valueOf(mensagem.charAt(count))
											.equals(" ")) {
										palavraConcat = mensagem
												.substring(
														(mensagem
																.indexOf(palavras)
																+ palavras
																		.length() + 1),
														count);
										achou = true;
									}
								}
								if (!achou) {
									palavraConcat = mensagem.substring(
											(mensagem.indexOf(palavras)
													+ palavras.length() + 1),
											mensagem.length());

								}
								palavras = palavras + " " + palavraConcat;
							}
						}
					}
					if (mensagem.contains(palavras)) {
						for (int count = (mensagem.indexOf(palavras)
								+ palavras.length() + 1); count < mensagem.length(); count++) {
							if (String.valueOf(mensagem.charAt(count)).equals(" ")) {
								String palavraComparar = mensagem.substring(
										(mensagem.indexOf(palavras)
												+ palavras.length() + 1), count);
								if (palavraComparar.equals("de")
										|| palavraComparar.equals("do")
										|| palavraComparar.equals("da")
										|| palavraComparar.equals("no")
										|| palavraComparar.equals("na")
										|| palavraComparar.equals("a")) {
									String palavraConcat = "";
									Boolean achou = false;
									for (int count2 = (mensagem.indexOf(palavraComparar)
											+ palavraComparar.length() + 1); count2 < mensagem
											.length(); count2++) {
										if (String.valueOf(mensagem.charAt(count2))
												.equals(" ")) {
											palavraConcat = mensagem
													.substring(
															(mensagem
																	.indexOf(palavraComparar)
																	+ palavraComparar
																			.length() + 1),
															count2);
											achou = true;
										}
									}
									if (!achou) {
										palavraConcat = mensagem.substring(
												(mensagem.indexOf(palavraComparar)
														+ palavraComparar.length() + 1),
												mensagem.length());

									}
									palavras = palavras + " " +palavraComparar+" "+ palavraConcat;
								}
							}
						}
					}

					String sparqlQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
							+ "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "
							+

							"SELECT ?comment WHERE { "
							+ "?entity rdfs:label ?label . "
							+ "?entity dbpedia-owl:abstract ?abstract . "
							+ "?entity rdfs:comment ?comment . "
							+ "?label <bif:contains> \"'"
							+ palavras
							+ "'\". "
							// + "?abstract <bif:contains> \"'"
							// + palavras
							// + "'\". "
							// + "?comment <bif:contains> \"'"
							// + palavras
							// + "'\". "
							+ "FILTER (lang(?abstract) = 'pt'). "
							+ "FILTER (lang(?comment) = 'pt'). " + "} LIMIT 10";

					// Cria uma query com o string
					Query query = QueryFactory.create(sparqlQuery);

					// Faz o sparql no servidor indicado
					QueryExecution qexec = QueryExecutionFactory.sparqlService(
							"http://dbpedia.org/sparql", query);

					qexec.setTimeout(5000);
					// Recupera os resultados da query
					ResultSet resultSet = qexec.execSelect();
					String resumo = "";
					
					Map<BigDecimal,String> similaridade = new HashMap<BigDecimal, String>();

					for (; resultSet.hasNext();) {
						QuerySolution soln = resultSet.nextSolution();
						Literal l = soln.getLiteral("comment");						
						similaridade.put(semanticSimilarity(l.getString(), mensagem),l.getString());

					}
					
					BigDecimal valor = null;
					
					for(BigDecimal key: similaridade.keySet())
					{
						if(valor == null || (key.compareTo(valor) == 1))
						{
							valor = key;
						}
					}
					resumo = similaridade.get(valor);
					
					qexec.close();

					Boolean salvou = false;

					if (mensagem.contains(palavras) && !resumo.equals("")) {

						if (!mapa.containsKey(palavras)) {

							for (String key : mapa.keySet()) {
								if (palavras.contains(key)) {
									salvou = true;
									if (palavras.length() > key.length()) {
										mapa.remove(key);
										mapa.put(palavras, resumo);
									}
									if (key.contains(palavras)) {
										salvou = true;
									}
								}
							}
							if (!salvou) {
								mapa.put(palavras, resumo);
							}
						}

					}
				}
			}
		}
		return mapa;
	}

	private static Map<String, String> recuperaEntidadesLtasks(String mensagem,
			Map<String, String> mapa) {
		

		URIBuilder builder = new URIBuilder();
		builder.setScheme("http").setHost("api.ltasks.com/app/v0b/ner")
				.setParameter("apikey", "bfa26559-5528-43c2-8ab6-404d6044fece")
				.setParameter("text", mensagem).setParameter("output", "xml");

		URI uri = null;
		try {
			uri = builder.build();

		} catch (URISyntaxException e1) {
			System.out.println("Erro ao conectar na URI");
			return mapa;
		}

		HttpGet httpget = new HttpGet(uri);
		httpget.setHeader("Accept", "application/xml");

		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used.
		int timeoutConnection = 13000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 15000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);

		HttpResponse response = null;
		try {
			response = httpclient.execute(httpget);
		} catch (Exception e) {
			System.out.println("Erro ao recuperar resposta");
			return mapa;
		}
		HttpEntity entity = response.getEntity();

		String xmlString = "";

		try {
			xmlString = EntityUtils.toString(entity, HTTP.UTF_8).trim();
		} catch (Exception e) {
			System.out.println("Erro ao recuperar resposta");
			return mapa;

		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		org.w3c.dom.Document doc = null;
		try {
			DocumentBuilder db = factory.newDocumentBuilder();
			InputSource inStream = new InputSource();
			inStream.setCharacterStream(new StringReader(xmlString));
			doc = db.parse(inStream);
		} catch (Exception e) {
			System.out.println("Erro ao converter resposta para xml");
			return mapa;

		}

		NodeList entities = doc.getElementsByTagName("result");
		if (entities != null) {
			Element entit = (Element) entities.item(0);
			NodeList entidades = entit.getElementsByTagName("namedEntities");
			Element entitidade2 = (Element) entidades.item(0);
			NodeList entidades2 = entitidade2
					.getElementsByTagName("namedEntity");
			for (int i = 0; i < entidades2.getLength(); ++i) {
				Element record = (Element) entidades2.item(i);

				String palavras = record.getAttribute("text");

				String[] reco = palavras.split(" ");
				String ultimaPalavra = reco[(reco.length - 1)];
				if (ultimaPalavra != null && !ultimaPalavra.equals("")) {
					if (ultimaPalavra.equals("de")
							|| ultimaPalavra.equals("do")
							|| ultimaPalavra.equals("da")
							|| ultimaPalavra.equals("no")
							|| ultimaPalavra.equals("na")
							|| ultimaPalavra.equals("a")) {
						if (mensagem.contains(palavras)) {
							String palavraConcat = "";
							Boolean achou = false;
							for (int count = (mensagem.indexOf(palavras)
									+ palavras.length() + 1); count < mensagem
									.length(); count++) {
								if (String.valueOf(mensagem.charAt(count))
										.equals(" ")) {
									palavraConcat = mensagem.substring(
											(mensagem.indexOf(palavras)
													+ palavras.length() + 1),
											count);
									achou = true;
								}
							}
							if (!achou) {
								palavraConcat = mensagem.substring(
										(mensagem.indexOf(palavras)
												+ palavras.length() + 1),
										mensagem.length());

							}
							palavras = palavras + " " + palavraConcat;
						}
					}
				}
				if (mensagem.contains(palavras)) {
					for (int count = (mensagem.indexOf(palavras)
							+ palavras.length() + 1); count < mensagem.length(); count++) {
						if (String.valueOf(mensagem.charAt(count)).equals(" ")) {
							String palavraComparar = mensagem.substring(
									(mensagem.indexOf(palavras)
											+ palavras.length() + 1), count);
							if (palavraComparar.equals("de")
									|| palavraComparar.equals("do")
									|| palavraComparar.equals("da")
									|| palavraComparar.equals("no")
									|| palavraComparar.equals("na")
									|| palavraComparar.equals("a")) {
								String palavraConcat = "";
								Boolean achou = false;
								for (int count2 = (mensagem.indexOf(palavraComparar)
										+ palavraComparar.length() + 1); count2 < mensagem
										.length(); count2++) {
									if (String.valueOf(mensagem.charAt(count2))
											.equals(" ")) {
										palavraConcat = mensagem
												.substring(
														(mensagem
																.indexOf(palavraComparar)
																+ palavraComparar
																		.length() + 1),
														count2);
										achou = true;
									}
								}
								if (!achou) {
									palavraConcat = mensagem.substring(
											(mensagem.indexOf(palavraComparar)
													+ palavraComparar.length() + 1),
											mensagem.length());

								}
								palavras = palavras + " " +palavraComparar+" "+ palavraConcat;
							}
						}
					}
				}

				String sparqlQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
						+ "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "
						+

						"SELECT ?comment WHERE { "
						+ "?entity rdfs:label ?label . "
						+ "?entity dbpedia-owl:abstract ?abstract . "
						+ "?entity rdfs:comment ?comment . "
						+ "?label <bif:contains> \"'"
						+ palavras
						+ "'\". "
						// + "?abstract <bif:contains> \"'"
						// + palavras
						// + "'\". "
						// + "?comment <bif:contains> \"'"
						// + palavras
						// + "'\". "
						+ "FILTER (lang(?abstract) = 'pt'). "
						+ "FILTER (lang(?comment) = 'pt'). " + "} LIMIT 10";

				// Cria uma query com o string
				Query query = QueryFactory.create(sparqlQuery);

				// Faz o sparql no servidor indicado
				QueryExecution qexec = QueryExecutionFactory.sparqlService(
						"http://dbpedia.org/sparql", query);

				qexec.setTimeout(5000);
				// Recupera os resultados da query
				ResultSet resultSet = qexec.execSelect();
				String resumo = "";
				
				Map<BigDecimal,String> similaridade = new HashMap<BigDecimal, String>();

				for (; resultSet.hasNext();) {
					QuerySolution soln = resultSet.nextSolution();
					Literal l = soln.getLiteral("comment");						
					similaridade.put(semanticSimilarity(l.getString(), mensagem),l.getString());

				}
				
				BigDecimal valor = null;
				
				for(BigDecimal key: similaridade.keySet())
				{
					if(valor == null || (key.compareTo(valor) == 1))
					{
						valor = key;
					}
				}
				resumo = similaridade.get(valor);
				qexec.close();

				Boolean salvou = false;

				if (mensagem.contains(palavras) && !resumo.equals("")) {

					if (!mapa.containsKey(palavras)) {

						for (String key : mapa.keySet()) {
							if (palavras.contains(key)) {
								salvou = true;
								if (palavras.length() > key.length()) {
									mapa.remove(key);
									mapa.put(palavras, resumo);
								}
								if (key.contains(palavras)) {
									salvou = true;
								}
							}
						}
						if (!salvou) {
							mapa.put(palavras, resumo);
						}
					}

				}

			}
		}
		return mapa;
	}

	private static Map<String, String> buscaDadosLexml(Map<String, String> mapa) {

		for (String key : mapa.keySet()) {

			String chave = "";
			try {
				chave = TratamentoTextual
						.executaTratamentosTextoParaRecomendacao(mapa.get(key));
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			URIBuilder builder = new URIBuilder();
			builder.setScheme("http")
					.setHost("www.lexml.gov.br/busca")
					.setPath("/SRU")
					.setParameter("operation", "searchRetrieve")
					.setParameter("maximumRecords", "10")
					.setParameter(
							"query",
							"( dc.subject any \""
									+ chave
									+ "\" or dc.title any \""
									+ chave
									+ "\" or dc.description any \""
									+ chave
									+ "\" ) and ( urn any lei or urn any decreto or urn any portaria or urn any emenda or urn any resolucao )");

			URI uri = null;
			try {
				uri = builder.build();

			} catch (URISyntaxException e1) {
				System.out.println("Erro ao criar URI");
				return mapa;
			}

			HttpGet httpget = new HttpGet(uri);
			httpget.setHeader("Accept", "application/xml");

			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is
			// established.
			// The default value is zero, that means the timeout is not used.
			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 5000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);

			HttpResponse response = null;
			try {
				response = httpclient.execute(httpget);
			} catch (Exception e) {
				System.out.println("Erro ao recuperar resposta");
				return mapa;
			}
			HttpEntity entity = response.getEntity();

			String xmlString = "";

			try {
				xmlString = EntityUtils.toString(entity, HTTP.UTF_8).trim();
			} catch (Exception e) {
				System.out.println("Erro ao recuperar resposta");
				return mapa;
			}
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			org.w3c.dom.Document doc = null;
			try {
				DocumentBuilder db = factory.newDocumentBuilder();
				InputSource inStream = new InputSource();
				inStream.setCharacterStream(new StringReader(xmlString));
				doc = db.parse(inStream);
			} catch (Exception e) {
				System.out.println("Erro ao converter resposta para xml");
				return mapa;
			}
			Map<BigDecimal,String> similaridade = new HashMap<BigDecimal, String>();


			NodeList recordList = doc.getElementsByTagName("srw:record");
			for (int i = 0; i < recordList.getLength(); ++i) {
				Element record = (Element) recordList.item(i);

				NodeList recordDataList = record
						.getElementsByTagName("srw:recordData");
				for (int j = 0; j < recordDataList.getLength(); ++j) {
					Element recordData = (Element) recordDataList.item(j);
					String titulo = "";
					String descricao = "";

					NodeList titleList = recordData
							.getElementsByTagName("dc:title");
					for (int k = 0; k < titleList.getLength(); ++k) {
						Element title = (Element) titleList.item(k);
						String titleText = title.getFirstChild().getNodeValue();
						titulo = titleText;
					}
					NodeList subjectList = recordData
							.getElementsByTagName("dc:description");
					for (int k = 0; k < subjectList.getLength(); ++k) {
						Element subject = (Element) subjectList.item(k);
						String subjectText = subject.getFirstChild()
								.getNodeValue();
						descricao = subjectText;
					}
					
					similaridade.put(semanticSimilarity(descricao, mapa.get(key)),titulo+" - "+descricao);
					
				
				}
			}
			BigDecimal valor = null;
			
			for(BigDecimal key1: similaridade.keySet())
			{
				if(valor == null || (key1.compareTo(valor) == 1))
				{
					valor = key1;
				}
			}
			String resumo = similaridade.get(valor);
			
			
			if (mapa.containsKey(key)) {
				String textoAntigo = mapa.get(key);
				if (!textoAntigo.equals("")) {
					textoAntigo = textoAntigo
							+ " / Legislação relacionada: " + resumo;
					mapa.put(key, textoAntigo);
				} else {
					textoAntigo = textoAntigo
							+ " Legislação relacionada: " + resumo;

					mapa.put(key, textoAntigo);
				}
			} else {
				mapa.put(key, resumo);
			}
		}

		return mapa;

	}

	private static Map<String, String> buscaDadosLexml(String filtro,
			Map<String, String> mapa,String mensagem) {

		URIBuilder builder = new URIBuilder();
		builder.setScheme("http")
				.setHost("www.lexml.gov.br/busca")
				.setPath("/SRU")
				.setParameter("operation", "searchRetrieve")
				.setParameter("maximumRecords", "1")
				.setParameter(
						"query",
						"( dc.subject any \""
								+ filtro
								+ "\" or dc.title any \""
								+ filtro
								+ "\" or dc.description any \""
								+ filtro
								+ "\" ) and ( urn any lei or urn any decreto or urn any portaria or urn any emenda or urn any resolucao )");

		URI uri = null;
		try {
			uri = builder.build();

		} catch (URISyntaxException e1) {
			return mapa;
		}

		HttpGet httpget = new HttpGet(uri);
		httpget.setHeader("Accept", "application/xml");

		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used.
		int timeoutConnection = 3000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 5000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);

		HttpResponse response = null;
		try {
			response = httpclient.execute(httpget);
		} catch (Exception e) {
			return mapa;
		}
		HttpEntity entity = response.getEntity();

		String xmlString = "";

		try {
			xmlString = EntityUtils.toString(entity, HTTP.UTF_8).trim();
		} catch (Exception e) {
			return mapa;
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		org.w3c.dom.Document doc = null;
		try {
			DocumentBuilder db = factory.newDocumentBuilder();
			InputSource inStream = new InputSource();
			inStream.setCharacterStream(new StringReader(xmlString));
			doc = db.parse(inStream);
		} catch (Exception e) {
			return mapa;

		}
		Map<BigDecimal,String> similaridade = new HashMap<BigDecimal, String>();

		NodeList recordList = doc.getElementsByTagName("srw:record");
		for (int i = 0; i < recordList.getLength(); ++i) {
			Element record = (Element) recordList.item(i);

			NodeList recordDataList = record
					.getElementsByTagName("srw:recordData");
			for (int j = 0; j < recordDataList.getLength(); ++j) {
				Element recordData = (Element) recordDataList.item(j);
				String titulo = "";
				String descricao = "";

				NodeList titleList = recordData
						.getElementsByTagName("dc:title");
				for (int k = 0; k < titleList.getLength(); ++k) {
					Element title = (Element) titleList.item(k);
					String titleText = title.getFirstChild().getNodeValue();
					titulo = titleText;
				}
				NodeList subjectList = recordData
						.getElementsByTagName("dc:description");
				for (int k = 0; k < subjectList.getLength(); ++k) {
					Element subject = (Element) subjectList.item(k);
					String subjectText = subject.getFirstChild().getNodeValue();
					descricao = subjectText;
				}

								
				similaridade.put(semanticSimilarity(descricao, mensagem),titulo+" - "+descricao);

				
				
				
			}
		}
		BigDecimal valor = null;
		
		for(BigDecimal key: similaridade.keySet())
		{
			if(valor == null || (key.compareTo(valor) == 1))
			{
				valor = key;
			}
		}
		String resumo = similaridade.get(valor);
		if (mapa.containsKey(filtro)) {
			String textoAntigo = mapa.get(filtro);
			if (!textoAntigo.equals("")) {
				textoAntigo = textoAntigo
						+ " / Legislação relacionada: " + resumo;
				mapa.put(filtro, textoAntigo);
			} else {
				textoAntigo = textoAntigo + " Legislação relacionada: "
						+resumo;

				mapa.put(filtro, textoAntigo);
			}
		} else {
			mapa.put(filtro, resumo);
		}
		return mapa;

	}

	private static Map<String, String> retornaRecomendacoesDbpedia(
			String mensagem, Map<String, String> mapa) {

		URIBuilder builder = new URIBuilder();
		builder.setScheme("http").setHost("spotlight.sztaki.hu:2228/rest")
				.setPath("/annotate").setParameter("text", mensagem)
				.setParameter("confidence", "0.4")
				.setParameter("support", "20")
				.setParameter("spotter", "Default")
				.setParameter("disambiguator", "Default")
				.setParameter("policy", "whitelist");
		URI uri = null;
		try {
			uri = builder.build();
		} catch (URISyntaxException e1) {
			return mapa;
		}
		HttpGet httpget = new HttpGet(uri);
		httpget.setHeader("Accept", "application/json");

		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used.
		int timeoutConnection = 3000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 5000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);

		HttpResponse response = null;
		try {
			response = httpclient.execute(httpget);
		} catch (Exception e) {
			return mapa;
		}
		HttpEntity entity = response.getEntity();

		JsonElement jelement = null;
		try {
			jelement = new JsonParser().parse(EntityUtils.toString(entity,
					HTTP.UTF_8).trim());
		} catch (Exception e) {

			return mapa;
		}
		JsonObject jobject = jelement.getAsJsonObject();
		JsonPrimitive textoJson = jobject.getAsJsonPrimitive("@text");
		String texto = textoJson.getAsString();

		if (jobject.getAsJsonArray("Resources") != null
				&& jobject.getAsJsonArray("Resources").size() > 0) {
			for (int i = 0; i < jobject.getAsJsonArray("Resources").size(); i++) {
				String URI = jobject.getAsJsonArray("Resources").get(i)
						.getAsJsonObject().getAsJsonPrimitive("@URI")
						.getAsString();
				String palavra = jobject.getAsJsonArray("Resources").get(i)
						.getAsJsonObject().getAsJsonPrimitive("@surfaceForm")
						.getAsString();

				String sparqlQuery =

				"PREFIX owl: <http://www.w3.org/2002/07/owl#> "
						+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
						+

						"select ?resumo where { " +

						"?recurso owl:sameAs <" + URI + ">. "
						+ "?recurso  rdfs:comment ?resumo. "
						+ "FILTER (LANG(?resumo) = 'pt') " +

						"  }";

				// Cria uma query com o string
				Query query = QueryFactory.create(sparqlQuery);

				// Faz o sparql no servidor indicado
				QueryExecution qexec = QueryExecutionFactory.sparqlService(
						"http://dbpedia.org/sparql", query);

				// Recupera os resultados da query
				ResultSet resultSet = qexec.execSelect();
				String resumo = "";

				for (; resultSet.hasNext();) {
					QuerySolution soln = resultSet.nextSolution();
					Literal l = soln.getLiteral("resumo");
					resumo = l.getString();

				}
				qexec.close();

				Boolean salvou = false;

				if (texto.contains(palavra) && !resumo.equals("")) {

					if (!mapa.containsKey(palavra)) {

						for (String key : mapa.keySet()) {
							if (palavra.contains(key)) {
								salvou = true;
								if (palavra.length() > key.length()) {
									mapa.remove(key);
									mapa.put(palavra, resumo);
								}
							}
							if (key.contains(palavra)) {
								salvou = true;
							}
						}
						if (!salvou) {
							mapa.put(palavra, resumo);
						}
					}

				}

			}
		}

		return mapa;

	}

	public static String recuperaRecomendacoes(String texto, String diretorio) {
		Map<String, String> mapa = new HashMap<String, String>();
		
		// Busca recomendacoes do ltasks
		mapa = recuperaEntidadesLtasks(texto, mapa);

		// Busca as recomendacoes da dbpedia
		mapa = retornaRecomendacoesDbpedia(texto, mapa);

		// Busca recomendacoes do alchemyapi
		mapa = recuperaEntidades(texto, mapa);

		// Busca as recomendacoes do LEXML
		// mapa = buscaDadosLexml(mapa);

		// Busca as recomendacoes do LEXML com palavras comuns as leis
		if (TermosUteis.contemTermosLegislacao(texto)) {
			String palavra = TermosUteis.palavraAposTermosLegislacao(texto);
			if (!palavra.equals("")) {
				// Recomendacao a partir da palavra apos o termo legislativo

				mapa = buscaDadosLexml(palavra, mapa,texto);
			}

		}

		if (TermosUteis.contemPalavra(texto)) {
			mapa = buscaDadosLexml(TermosUteis.palavraAposPalavra(texto), mapa,texto);
		}

		if (!buscaSiglas(texto).equals("")) {
			mapa = buscaDadosLexml(buscaSiglas(texto), mapa,texto);
		}

		// if(TermosUteis.contemDuvida(texto))
		// {
		//
		// }

		Set<String> keyset = mapa.keySet();
		for (String palavra : keyset) {
			String recomendacao = mapa.get(palavra);
			if (texto.contains(palavra)) {
				texto = texto.replace(palavra, "<a href='' title='"
						+ recomendacao + "'>" + palavra + "</a>");
			}

			gravaPalavraLog(palavra, diretorio);

		}

		return texto;
	}

	public static void gravaPalavraLog(String palavra, String diretorio) {
		try {
			File arquivo = new File(diretorio + "/" + "frequencias.xml");
			FileOutputStream fos = new FileOutputStream(arquivo);
			String txt = TratamentoTextual
					.executaTratamentosTextoParaFrequencias(palavra) + " /n";
			fos.write(txt.getBytes());
			fos.close();
		} catch (Exception e) {
			System.out.println("Erro ao escrever no log " + e);
		}
	}

	public static String buscaSiglas(String texto) {

		int countUppers = 0;
		String palavraAllUpper = "";
		String[] palavras = texto.split(" ");
		for (String palavra : palavras) {
			if (isAllUpper(palavra) && palavra.length() > 2) {
				countUppers++;
				if (countUppers > 1) {
					return "";
				} else {
					palavraAllUpper = palavra;

				}
			}
		}
		return palavraAllUpper;
	}

	private static boolean isAllUpper(String s) {
		for (char c : s.toCharArray()) {
			if (Character.isLetter(c) && !Character.isUpperCase(c)) {
				return false;
			}
		}
		return true;
	}

}
