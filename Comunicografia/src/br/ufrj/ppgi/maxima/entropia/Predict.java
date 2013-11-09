///////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2001 Chieu Hai Leong and Jason Baldridge
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////   
package br.ufrj.ppgi.maxima.entropia;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import br.ufrj.ppgi.grafo.entidades.Mensagem;

import opennlp.maxent.BasicContextGenerator;
import opennlp.maxent.ContextGenerator;
import opennlp.maxent.DataStream;
import opennlp.maxent.PlainTextByLineDataStream;
import opennlp.model.GenericModelReader;
import opennlp.model.MaxentModel;
import opennlp.model.RealValueFileEventStream;

/**
 * Test the model on some input.
 * 
 * @author Jason Baldridge
 * @version $Revision: 1.4 $, $Date: 2008/11/06 20:00:34 $
 */
public class Predict {
	MaxentModel _model;
	ContextGenerator _cg = new BasicContextGenerator();

	public Predict(MaxentModel m) {
		_model = m;
	}

	private void eval(String predicates) {
		eval(predicates, false);
	}

	private void eval(String predicates, boolean real) {
		String[] contexts = predicates.split(" ");
		double[] ocs;
		if (!real) {
			ocs = _model.eval(contexts);
		} else {
			float[] values = RealValueFileEventStream.parseContexts(contexts);
			ocs = _model.eval(contexts, values);
		}
		System.out.println("For context: " + predicates + "\n"
				+ _model.getAllOutcomes(ocs) + "\n");

	}

	private static void usage() {

	}

	/**
	 * Main method. Call as follows:
	 * <p>
	 * java Predict dataFile (modelFile)
	 */
	public static void predictMessagesRelations(String dataFileName) {
		
		String modelFileName;
		boolean real = false;

		modelFileName = new String(
				"src/recursos/treinamento/treinamentoModel.txt");
		dataFileName = new String("src/recursos/treinamento/teste.dat");
	
		Predict predictor = null;
		MaxentModel m = null;
		try {
			 m = new GenericModelReader(new File(modelFileName))
					.getModel();
			predictor = new Predict(m);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		try {
			File file = new File(dataFileName);
			FileReader reader = new FileReader(file);
			DataStream ds = new PlainTextByLineDataStream(reader);
			int countAcertos = 0;
			int countErros = 0;
			int countEhparClassificadoEhpar=0;
			int countNaoehParClassificadoPar=0;
			int countNaoEhparClassificadoNaoEhpar=0;
			int countEhParClassificadoNaoEhPar=0;

			while (ds.hasNext()) {
				String s = (String) ds.nextToken();
				String best = m.getBestOutcome(m.eval(s.split(" ")));
				System.out.println(s +" "+best);
				String[] avaliado = s.split(" ");
				
				if(avaliado[0].contains("false")){
					if(best.equals("NaoEhPar"))
					{
						countAcertos++;
						countNaoEhparClassificadoNaoEhpar++;
					}else{
						countErros++;
						countNaoehParClassificadoPar++;
					}
				}else{
					if(best.equals("ehPar"))
					{
						countAcertos++;
						countEhparClassificadoEhpar++;
					}else{
						countErros++;
						countEhParClassificadoNaoEhPar++;
					}
				}
			}
			System.out.println("Acertos="+countAcertos);
			System.out.println("Erros="+countErros);
			System.out.println("Porcentagem="+(countAcertos*100)/(countAcertos+countErros));
			System.out.println("Acerto: Nao eh par classificado como nao é par= "+countNaoEhparClassificadoNaoEhpar);
			System.out.println("Acerto: É par classificado como é par= "+countEhparClassificadoEhpar);
			System.out.println("Erro: Nao eh par classificado como é par= "+countNaoehParClassificadoPar);
			System.out.println("Erro: É par classificado como não é par= "+countEhParClassificadoNaoEhPar);
			return;
		} catch (Exception e) {
			System.out.println("Unable to read from specified file: "
					+ modelFileName);
			System.out.println();
			e.printStackTrace();
		}
	}

}
