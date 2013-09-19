package br.ufrj.ppgi.recomendacao;

import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
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

	private static Map<String, String> recuperaEntidades(String mensagem,Map<String, String> mapa) {
		DefaultHttpClient httpclient = new DefaultHttpClient();

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
		}

		HttpGet httpget = new HttpGet(uri);
		httpget.setHeader("Accept", "application/xml");

		HttpResponse response = null;
		try {
			response = httpclient.execute(httpget);
		} catch (Exception e) {
		}
		HttpEntity entity = response.getEntity();
		

		String xmlString = "";

		try {
			xmlString = EntityUtils.toString(entity, HTTP.UTF_8).trim();
		} catch (Exception e) {
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		org.w3c.dom.Document doc = null;
		try {
			DocumentBuilder db = factory.newDocumentBuilder();
			InputSource inStream = new InputSource();
			inStream.setCharacterStream(new StringReader(xmlString));
			doc = db.parse(inStream);
		} catch (Exception e) {

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
					
					
					String sparqlQuery =
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
						"PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "+

						"SELECT ?comment WHERE { "+
						"?entity rdfs:label ?label . "+
						"?entity dbpedia-owl:abstract ?abstract . "+
						"?entity rdfs:comment ?comment . "+
						"?label <bif:contains> \"'"+palavras + "'\". "+
						"?abstract <bif:contains> \"'"+palavras + "'\". "+
						"?comment <bif:contains> \"'"+palavras + "'\". "+
						"FILTER (lang(?abstract) = 'pt'). "+ 
						"FILTER (lang(?comment) = 'pt'). "+
						"} LIMIT 1";

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
							Literal l = soln.getLiteral("comment");
							resumo = l.getString();

						}
						qexec.close();

						if (mensagem.contains(palavras)) {

							if (!mapa.containsKey(palavras)) {
								mapa.put(palavras, resumo);
							}
						}	
				}
			}
		}
		return mapa;
	}

	private static Map<String, String> buscaDadosLexml(Map<String, String> mapa) {

		DefaultHttpClient httpclient = new DefaultHttpClient();

		for (String key : mapa.keySet()) {

			String chave = TratamentoTextual.converteParaMinuscula(key);

			URIBuilder builder = new URIBuilder();
			builder.setScheme("http")
					.setHost("www.lexml.gov.br/busca")
					.setPath("/SRU")
					.setParameter("operation", "searchRetrieve")
					.setParameter("maximumRecords", "1")
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
			}

			HttpGet httpget = new HttpGet(uri);
			httpget.setHeader("Accept", "application/xml");

			HttpResponse response = null;
			try {
				response = httpclient.execute(httpget);
			} catch (Exception e) {
			}
			HttpEntity entity = response.getEntity();

			String xmlString = "";

			try {
				xmlString = EntityUtils.toString(entity, HTTP.UTF_8).trim();
			} catch (Exception e) {
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

			}

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
					if (mapa.containsKey(key)) {
						String textoAntigo = mapa.get(key);
						if(!textoAntigo.equals(""))
						{
							textoAntigo = textoAntigo
									+ " / Legislação relacionada: " + titulo
									+ " - " + descricao;
							mapa.put(key, textoAntigo);
						}else{
							textoAntigo = textoAntigo
							+ " Legislação relacionada: " + titulo
							+ " - " + descricao;
					mapa.put(key, textoAntigo);
						}
					} else {
						mapa.put(key, titulo + " - " + descricao);
					}
				}
			}
		}

		return mapa;

	}

	private static Map<String, String> buscaDadosLexml(String filtro,
			Map<String, String> mapa) {

		DefaultHttpClient httpclient = new DefaultHttpClient();

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
		}

		HttpGet httpget = new HttpGet(uri);
		httpget.setHeader("Accept", "application/xml");

		HttpResponse response = null;
		try {
			response = httpclient.execute(httpget);
		} catch (Exception e) {
		}
		HttpEntity entity = response.getEntity();

		String xmlString = "";

		try {
			xmlString = EntityUtils.toString(entity, HTTP.UTF_8).trim();
		} catch (Exception e) {
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		org.w3c.dom.Document doc = null;
		try {
			DocumentBuilder db = factory.newDocumentBuilder();
			InputSource inStream = new InputSource();
			inStream.setCharacterStream(new StringReader(xmlString));
			doc = db.parse(inStream);
		} catch (Exception e) {

		}

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
				if (mapa.containsKey(filtro)) {
					String textoAntigo = mapa.get(filtro);
					textoAntigo.concat(" " + titulo + " - " + descricao);
					mapa.put(filtro, textoAntigo);
				} else {
					mapa.put(filtro, titulo + " - " + descricao);
				}
			}
		}
		return mapa;

	}

	private static Map<String, String> retornaRecomendacoesDbpedia(
			String mensagem, Map<String, String> mapa) {
		DefaultHttpClient httpclient = new DefaultHttpClient();

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

				if (texto.contains(palavra)) {

					if (!mapa.containsKey(palavra)) {
						mapa.put(palavra, resumo);
					}
				}

			}
		}

		return mapa;

	}

	public static String recuperaRecomendacoes(String texto) {
		Map<String, String> mapa = new HashMap<String, String>();

		mapa = recuperaEntidades(texto,mapa);
		// Busca as recomendacoes da dbpedia
		mapa = retornaRecomendacoesDbpedia(texto, mapa);
		// Busca as recomendacoes do LEXM
		mapa = buscaDadosLexml(mapa);

		// Busca as recomendacoes do LEXM com palavras comuns as leis
		if (TermosUteis.contemTermosLegislacao(texto)) {
			String palavra = TermosUteis.palavraAposTermosLegislacao(texto);
			if (!palavra.equals("")) {
				// Recomendacao a partir da palavra apos o termo legislativo

				mapa = buscaDadosLexml(palavra, mapa);
			}

		}

		Set<String> keyset = mapa.keySet();
		for (String palavra : keyset) {
			String recomendacao = mapa.get(palavra);
			if (texto.contains(palavra)) {
				texto = texto.replace(palavra, "<a href='' title='"
						+ recomendacao + "'>" + palavra + "</a>");
			}

		}

		return texto;
	}

}
