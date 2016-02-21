package metaheuristics.project.agp.alg.hybrid;

import metaheuristics.project.agp.alg.genetic.GeneticAlgorthm;
import metaheuristics.project.agp.alg.greedy.HeuristicGreedy;
import metaheuristics.project.agp.alg.greedy.HeuristicGreedy.InitialSet;
import metaheuristics.project.agp.alg.greedy.heuristics.Heuristic;
import metaheuristics.project.agp.instances.GalleryInstance;
import metaheuristics.project.agp.instances.util.BenchmarkFileInstanceLoader;

/**
 * Hybrid algorithm.
 * Calls genetic algorithm with results generated from Greedy algorithm as initial cover.
 * @author jelenadrzaic
 *
 */
public class HybridAlgorithm {

	/**
	 * Type of initial cover used by greedy algorithm.
	 */
	InitialSet is;
	
	/**
	 * Heuristic algorithm used by greedy algorithm.
	 */
	Heuristic h;
	
	/**
	 * Tolerance used by algorithm.
	 */
	double tol;
	
	/**
	 * Class constructor.
	 * 
	 * @param is type of initial cover.
	 * @param h heuristic used
	 * @param tol tolerance
	 */
	public HybridAlgorithm(InitialSet is, Heuristic h, double tol) {
		this.is = is;
		this.h= h;
		this.tol = tol;
	}
	
	public int process(String filePolygon, String fileToSaveFin) {
		BenchmarkFileInstanceLoader bfil = new BenchmarkFileInstanceLoader();
		GalleryInstance gi = bfil.load(filePolygon);
		HeuristicGreedy hg = new HeuristicGreedy(is, h);
		hg.setTol(tol);
		gi.setCameras(hg);
		gi.saveResults("test_results_and_samples/geninit.txt");
		GeneticAlgorthm ga = new GeneticAlgorthm();
		return ga.process(filePolygon, fileToSaveFin, "test_results_and_samples/geninit.txt");
	}
}
