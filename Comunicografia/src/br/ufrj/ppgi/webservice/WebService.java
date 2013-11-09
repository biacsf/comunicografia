package br.ufrj.ppgi.webservice;



import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import br.ufrj.ppgi.recomendacao.GeraRecomendacoes;

@Path("recomendacao")
public class WebService {

	@GET
	@Produces("text/plain")
	@Path("{texto}/{usuario}/{idDiscussao}")
	public Response getRecomendacao(@PathParam("texto") String texto,
			@PathParam("usuario") String usuario,
			@PathParam("idDiscussao") String idDiscussao, @Context ServletContext context) {
		
		String diretorio = context.getRealPath("/");
		diretorio= diretorio+ "WEB-INF/classes/";
		
		return Response.ok(GeraRecomendacoes.geraRecomendacao(texto, usuario, idDiscussao,diretorio)).header("Access-Control-Allow-Origin", "*").header("content-type", "text/plain").build();
	}
	
	



}
