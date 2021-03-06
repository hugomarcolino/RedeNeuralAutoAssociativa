package treinamento;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.metaheuristics.singleObjective.geneticAlgorithm.gGA;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.singleObjective.RedeNeuralProblem;
import jmetal.util.JMException;
import rede.RedeNeural;

public class AlgoritmoGenetico {
	
	private List<double[]> biasTreinamento = new ArrayList<double[]>();
	private List<double[][][]> pesosTreinamento = new ArrayList<double[][][]>();
	
	private List<Double> erros = new ArrayList<Double>();
	
	public RedeNeural treinamentoRede(RedeNeural rede, List<double[][]> entradas, List<double[][]> saidas,
			double taxaCruzamento, double taxaMutacao, int nPopulacao, int nCiclos ) throws ClassNotFoundException, JMException {

		
	    //Problem problem;       
	    Algorithm algorithm;       
	    Operator crossover;       
	    Operator mutation;       
	    Operator selection;       
	    
	    int tam = (int) ((Math.pow(rede.getTamCampo(), 2) + 1)*rede.getCamadas().length);
	    
	    RedeNeuralProblem neuralProblem = new RedeNeuralProblem("Real", tam);
	    
	    neuralProblem.setEntradas(entradas);
	    neuralProblem.setSaidas(saidas);
	    neuralProblem.setRede(rede);
	    
	    algorithm = new gGA(neuralProblem);
	    algorithm.setInputParameter("populationSize", nPopulacao);
	    algorithm.setInputParameter("maxEvaluations", nCiclos*nPopulacao);

	    HashMap parameters = new HashMap();
	    parameters.put("probability", taxaCruzamento);
	    parameters.put("distributionIndex", 20.0) ;
	    crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);                   

	    parameters = new HashMap();
	    parameters.put("probability", taxaMutacao) ;
	    parameters.put("distributionIndex", 20.0) ;
	    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);                    

	    parameters = new HashMap();
	    selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters) ;                            
	    
	    algorithm.addOperator("crossover", crossover);
	    algorithm.addOperator("mutation", mutation);
	    algorithm.addOperator("selection", selection);
	 
	    SolutionSet population = algorithm.execute();
	    
	    System.out.println("Objectives values have been writen to file FUN");
	    population.printObjectivesToFile("FUN");
	    System.out.println("Variables values have been writen to file VAR");
	    population.printVariablesToFile("VAR");          
		
	    List<Solution> solutionsList_ = population.getSolutionsList_();
	    int numberOfVariables = solutionsList_.get(0).getDecisionVariables().length ;
	    double[] position = new double[tam];
	    for (int j = 0; j < numberOfVariables; j++) {
	    	position[j] = solutionsList_.get(0).getDecisionVariables()[j].getValue();
        }
		
	    //SETAR PESOS
  		int  p = 0;
  		
  		List<Double> biasCamada = new ArrayList<Double>();
  		List<double[][]> pesosCamadas = new ArrayList<double[][]>();
  		
  		for (int c = 0; c < rede.getCamadas().length; c++) {			
  			double[][] pesosCamada = new double[rede.getTamCampo()][rede.getTamCampo()];
  			
  			for (int i = 0; i < pesosCamada.length; i++) {
  				for (int j = 0; j < pesosCamada.length; j++) {
  					pesosCamada[i][j] = position[p++]; 
  				}
  			}
  			pesosCamadas.add(pesosCamada);
  		}
  		
  		for (int c = 0; c < rede.getCamadas().length; c++) {			
  			biasCamada.add(position[p++]);
  		}
  		
  		for (int c = 0; c < rede.getCamadas().length; c++) {
  			rede.getCamadas()[c].setPesosCamada(pesosCamadas.get(c));
  			rede.getCamadas()[c].setBiasCamada(biasCamada.get(c));
  		}
  		//---
	    
	    
		return rede;
	}
	
	public List<double[]> getBiasTreinamento() {
		return biasTreinamento;
	}

	public List<double[][][]> getPesosTreinamento() {
		return pesosTreinamento;
	}

	public List<Double> getErros() {
		return erros;
	}
}
