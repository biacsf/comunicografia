package br.ufrj.ppgi.recomendacao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import br.ufrj.ppgi.util.PosTagger;
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

	private static BigDecimal semanticSimilarity(String a, String b) {

		if (b != null && !(b.equals("")) && a != null && !(a.equals(""))) {
			b = b.replace(",", "").replace(".", "").replace("?", "")
					.replace("!", "").trim().replace("\"", "\\\"");
			a = a.replace(",", "").replace(".", "").replace("?", "")
					.replace("!", "").trim().replace("\"", "\\\"");

			b = Normalizer.normalize(b, Normalizer.Form.NFD);
			b = b.replaceAll("[^\\p{ASCII}]", "").trim();

			a = Normalizer.normalize(a, Normalizer.Form.NFD);
			a = a.replaceAll("[^\\p{ASCII}]", "").trim();

			if (a.length() >= 120) {
				a = a.substring(0, 120);
			}
			if (b.length() >= 120) {
				b = b.substring(0, 120);
			}

			URIBuilder builder = new URIBuilder();
			builder.setScheme("https").setHost(
					"amtera.p.mashape.com/relatedness/pt");

			String texto = "1";

			URI uri = null;
			try {
				uri = builder.build();

			} catch (URISyntaxException e1) {
				System.out.println("Erro ao conectar na URI");
				return null;
			}

			HttpPost httpget = new HttpPost(uri);
			httpget.setHeader("X-Mashape-Authorization",
					"");
			httpget.setHeader("Content-type", "application/json");

			StringEntity params = null;
			try {
				params = new StringEntity("{\"t1\":\"" + a + "\",\"t2\":\"" + b
						+ "\"}");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			httpget.setEntity(params);

			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is
			// established.
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
				System.out.println("Erro ao recuperar resposta " + e);
				return null;
			}

			HttpEntity entity = response.getEntity();

			JsonElement jelement = null;
			try {
				jelement = new JsonParser().parse(EntityUtils.toString(entity,
						HTTP.UTF_8).trim());
				JsonObject jobject = jelement.getAsJsonObject();
				JsonPrimitive textoJson = jobject.getAsJsonPrimitive("v");
				texto = textoJson.getAsString();
			} catch (Exception e) {
				System.out.println("Excecao na similaridade semantica " + e);
				return new BigDecimal(1);
			}

			return new BigDecimal(texto);
		} else {
			return new BigDecimal(1);
		}

	}

	private static Map<String, String> buscaDadosLexml(String filtro,
			Map<String, String> mapa, String mensagem, String textoAnterior,
			String entidadesAnteriores) {

		URIBuilder builder = new URIBuilder();
		
		if(filtro.contains("PEC") || filtro.contains("PL"))
		{
			builder.setScheme("http")
			.setHost("www.lexml.gov.br/busca")
			.setPath("/SRU")
			.setParameter("operation", "searchRetrieve")
			.setParameter("maximumRecords", "1")
			.setParameter(
					"query",
					"( dc.subject = \""
							+ filtro
							+ "\" or dc.title any \""
							+ filtro
							+ "\" or dc.description any \""
							+ filtro
							+ "\" ) ");
		}else{
			
		
		builder.setScheme("http")
				.setHost("www.lexml.gov.br/busca")
				.setPath("/SRU")
				.setParameter("operation", "searchRetrieve")
				.setParameter("maximumRecords", "1")
				.setParameter(
						"query",
						"( dc.subject = \""
								+ filtro
								+ "\" or dc.title any \""
								+ filtro
								+ "\" or dc.description any \""
								+ filtro
								+ "\" ) and ( urn any projeto urn any proposta or urn any lei or urn any decreto or urn any portaria or urn any emenda or urn any resolucao )");
		}
		URI uri = null;
		try {
			uri = builder.build();

		} catch (URISyntaxException e1) {
			System.out.println("Erro lexml " + e1);
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
			return mapa;
		}
		HttpEntity entity = response.getEntity();

		String xmlString = "";

		try {
			xmlString = EntityUtils.toString(entity, HTTP.UTF_8).trim();
		} catch (Exception e) {
			System.out.println("Erro lexml " + e);
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
		Map<BigDecimal, String> similaridade = new HashMap<BigDecimal, String>();

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
				BigDecimal valor = semanticSimilarity(descricao, textoAnterior)
						.add(semanticSimilarity(descricao, entidadesAnteriores));
				similaridade.put(valor, titulo + " - " + descricao);

			}
		}
		BigDecimal valor = null;

		for (BigDecimal key : similaridade.keySet()) {
			if (valor == null || (key.compareTo(valor) == 1)) {
				if (key.compareTo(new BigDecimal(0.5)) == 1) {
					valor = key;
				}
			}
		}
		String resumo = similaridade.get(valor);
		Boolean salvou = false;
		if (resumo != null && !resumo.equals("")) {
			if (mapa.containsKey(filtro)) {
				String textoAntigo = mapa.get(filtro);
				if (!textoAntigo.equals("")) {
					textoAntigo = textoAntigo + " / Legislação relacionada: "
							+ resumo;
					mapa.put(filtro, textoAntigo);
				} else {
					textoAntigo = textoAntigo + " Legislação relacionada: "
							+ resumo;

					mapa.put(filtro, textoAntigo);
				}
			} else {

				for (String key : mapa.keySet()) {
					if (filtro.toLowerCase().contains(key.toLowerCase())) {
						String textoAntigo = mapa.get(key);
						if (!textoAntigo.equals("")) {
							textoAntigo = textoAntigo
									+ " / Legislação relacionada: " + resumo;
							mapa.put(key, textoAntigo);
							salvou = true;
						}
						if (key.toLowerCase().contains(filtro.toLowerCase())) {
							salvou = true;
						}
					}

				}
				if (!salvou) {
					mapa.put(filtro, resumo);
				}
			}
		}
		return mapa;

	}
	

	private static Map<String, String> buscaDadosLexmlTermosUteis(
			String filtro, String palavras, Map<String, String> mapa,
			String mensagem, String textoAnterior, String entidadesAnteriores) {

		URIBuilder builder = new URIBuilder();
		builder.setScheme("http")
				.setHost("www.lexml.gov.br/busca")
				.setPath("/SRU")
				.setParameter("operation", "searchRetrieve")
				.setParameter("maximumRecords", "1")
				.setParameter(
						"query",
						"( dc.title any \""
								+ filtro
								+ "\" ) ");

		URI uri = null;
		try {
			uri = builder.build();

		} catch (URISyntaxException e1) {
			System.out.println("Erro lexml " + e1);
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
			return mapa;
		}
		HttpEntity entity = response.getEntity();

		String xmlString = "";

		try {
			xmlString = EntityUtils.toString(entity, HTTP.UTF_8).trim();
		} catch (Exception e) {
			System.out.println("Erro lexml " + e);
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
		String resumo = "";
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
					resumo = titulo + " - " + descricao;
				}

			}
		}
		Boolean salvou = false;
		if (resumo != null && !resumo.equals("")) {
			if (mapa.containsKey(palavras)) {
				String textoAntigo = mapa.get(palavras);
				if (!textoAntigo.equals("")) {
					textoAntigo = textoAntigo + " / Legislação relacionada: "
							+ resumo;
					mapa.put(palavras, textoAntigo);
				} else {
					textoAntigo = textoAntigo + " Legislação relacionada: "
							+ resumo;

					mapa.put(palavras, textoAntigo);
				}
			} else {

				for (String key : mapa.keySet()) {
					if (palavras.toLowerCase().contains(key.toLowerCase())) {
						String textoAntigo = mapa.get(key);
						if (!textoAntigo.equals("")) {
							textoAntigo = textoAntigo
									+ " / Legislação relacionada: " + resumo;
							mapa.put(key, textoAntigo);
							salvou = true;
						}
						if (key.toLowerCase().contains(palavras.toLowerCase())) {
							salvou = true;
						}
					}

				}
				if (!salvou) {
					mapa.put(palavras, resumo);
				}
			}
		}
		return mapa;

	}

	private static Map<String, String> retornaRecomendacoesDbpedia(
			String mensagem, Map<String, String> mapa, String textoAnterior,
			String entidadesAnteriores) {

		URIBuilder builder = new URIBuilder();
		builder.setScheme("http").setHost("spotlight.sztaki.hu:2228/rest")
				.setPath("/annotate").setParameter("text", mensagem)
				.setParameter("confidence", "0.1").setParameter("support", "4")
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
		// Timeout para o caso do servidor estar fora do ar
		int timeoutConnection = 3000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
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

				mapa = atualizaMapa(palavra, resumo, texto, mapa,
						textoAnterior, entidadesAnteriores);

			}
		}
		return mapa;

	}

	private static Map<String, String> atualizaMapa(String palavra,
			String resumo, String texto, Map<String, String> mapa,
			String textoAnterior, String entidadesAnteriores) {
		if (semanticSimilarity(resumo, textoAnterior).compareTo(
				new BigDecimal(0.05)) == 1
				&& semanticSimilarity(resumo, entidadesAnteriores).compareTo(
						new BigDecimal(0.05)) == 1) {

			Boolean salvou = false;

			if (texto.contains(palavra) && resumo != null && !resumo.equals("")) {

				if (!mapa.containsKey(palavra)) {

					for (String key : mapa.keySet()) {
						if (palavra.toLowerCase().contains(key.toLowerCase())) {
							salvou = true;
							if (palavra.length() > key.length()) {
								mapa.remove(key);
								mapa.put(palavra, resumo);
							}
						}
						if (key.toLowerCase().contains(palavra.toLowerCase())) {
							salvou = true;
						}
					}
					if (!salvou) {
						mapa.put(palavra, resumo);
					}
				}

			}

		}
		return mapa;
	}

	public static String recuperaRecomendacoes(String texto, String diretorio,
			String textoAnterior, String entidadesAnteriores) {
		Map<String, String> mapa = new HashMap<String, String>();

		// Busca as recomendacoes da dbpedia
		mapa = retornaRecomendacoesDbpedia(texto, mapa, textoAnterior,
				entidadesAnteriores);
		
		// Busca as recomendacoes do LEXML com palavras comuns as leis
		String palavraAposTermos = TermosUteis
				.palavraAposTermosLegislacao(texto);
		if (!palavraAposTermos.equals("")) {
			// Recomendacao a partir da palavra apos o termo legislativo

			mapa = buscaDadosLexml(palavraAposTermos, mapa, texto,
					textoAnterior, entidadesAnteriores);

		}
		
		if (!buscaSiglas(texto).equals("")) {
			for (String sigla : buscaSiglas(texto)) {
				mapa = recomendacoesNomesProprios(mapa, texto, sigla,
						textoAnterior, entidadesAnteriores);
				mapa = buscaDadosLexml(sigla, mapa, texto, textoAnterior,
						entidadesAnteriores);

			}

		}
		
		try {
			PosTagger ex = new PosTagger();
			List<String> nomesProprios = ex.analyzeAndGetNames(texto);

			for (String nome : nomesProprios) {
				if (!mapa.containsKey(nome)) {
					mapa = recomendacoesNomesProprios(mapa, texto, nome,
							textoAnterior, entidadesAnteriores);
					mapa = buscaDadosLexml(nome, mapa, texto, textoAnterior,
							entidadesAnteriores);
				}

			}
		} catch (Exception e) {
			System.out.println("Erro " + e.getMessage());
		}

		String textoRecomendacao = "";
		Set<String> keyset = mapa.keySet();
		for (String palavra : keyset) {
			String recomendacao = mapa.get(palavra);
			if (recomendacao.contains("'")) {
				recomendacao = recomendacao.replaceAll("[\"\']", "");
			}
			if (palavra.contains("'")) {
				palavra = palavra.replaceAll("[\"\']", "");
			}
			if (!recomendacao.equals("")) {
				textoRecomendacao += "<a href='' title='" + recomendacao + "'>"
						+ palavra + "</a>" + " <br/>";
				
	
			}
			gravaPalavraLog(palavra, diretorio);


		}

		return textoRecomendacao;
	}

	private static Map<String, String> recomendacoesNomesProprios(
			Map<String, String> mapa, String texto, String nomeProprio,
			String textoAnterior, String entidadesAnteriores) {

		String sparqlQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "
				+

				"SELECT ?comment ?label WHERE { "
				+ "?entity rdfs:label ?label . "
				+ "?entity dbpedia-owl:abstract ?abstract . "
				+ "?entity rdfs:comment ?comment . "
				+ "?label <bif:contains> \"'"
				+ nomeProprio
				+ "'\". "
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

		Map<BigDecimal, String> similaridade = new HashMap<BigDecimal, String>();

		for (; resultSet.hasNext();) {
			QuerySolution soln = resultSet.nextSolution();
			Literal l = soln.getLiteral("comment");
			BigDecimal valor = semanticSimilarity(l.getString(), textoAnterior)
					.add(semanticSimilarity(l.getString(), entidadesAnteriores));
			similaridade.put(valor, l.getString());

		}

		BigDecimal valor = null;

		for (BigDecimal key : similaridade.keySet()) {
			if (valor == null || (key.compareTo(valor) == 1)) {
				if (key.compareTo(new BigDecimal(0.5)) == 1) {
					valor = key;
				}
			}
		}
		resumo = similaridade.get(valor);

		qexec.close();

		Boolean salvou = false;

		if (texto.contains(nomeProprio) && resumo != null && !resumo.equals("")) {

			if (!mapa.containsKey(nomeProprio)) {

				for (String key : mapa.keySet()) {
					if (nomeProprio.toLowerCase().contains(key.toLowerCase())) {
						salvou = true;
						if (nomeProprio.length() > key.length()) {
							mapa.remove(key);
							mapa.put(nomeProprio, resumo);
						}
						if (key.toLowerCase().contains(nomeProprio.toLowerCase())) {
							salvou = true;
						}
					}
				}
				if (!salvou) {
					mapa.put(nomeProprio, resumo);
				}
			}

		}

		return mapa;

	}

	
	public static void gravaPalavraLog(String palavra, String diretorio) {
		try {
			File arquivo = new File(diretorio + "/palavras.xml");
			FileOutputStream fos = new FileOutputStream(arquivo,true);
			String txt = palavra + " ";
			fos.write(txt.getBytes());
			fos.close();
		} catch (Exception e) {
			System.out.println("Erro ao escrever no log " + e);
		}
	}

	public static List<String> buscaSiglas(String texto) {

		List<String> siglas = new ArrayList<String>();
		List<String> siglasFim = new ArrayList<String>();

		int countUppers = 0;
		String[] palavras = texto.split(" ");
		for (String palavra : palavras) {
			if (isAllUpper(palavra) && palavra.length() > 2) {
				countUppers++;

				siglas.add(palavra);

			}
		}
		if (countUppers < palavras.length) {
			for (String sigla : siglas) {
				if (sigla.matches(".*\\d.*")) {
					if (!sigla.startsWith("20") && !sigla.startsWith("19")
							&& sigla.length() > 4) {
						siglasFim.add(sigla);
					}

						siglasFim.add(sigla);
					
				}
			}
			return siglasFim;
		} else {
			return new ArrayList<String>();
		}
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
