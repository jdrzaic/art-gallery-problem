package metaheuristics.project.agp.alg.hybrid;

import metaheuristics.project.agp.alg.genetic.GeneticAlgorthm;
import metaheuristics.project.agp.alg.greedy.HeuristicGreedy;
import metaheuristics.project.agp.alg.greedy.HeuristicGreedy.InitialSet;
import metaheuristics.project.agp.alg.greedy.heuristics.A7;
import metaheuristics.project.agp.alg.greedy.heuristics.Heuristic;
import metaheuristics.project.agp.instances.GalleryInstance;
import metaheuristics.project.agp.instances.util.BenchmarkFileInstanceLoader;

public class HybridAlgorithm {

	InitialSet is;
	Heuristic h;
	double tol;
	
	public HybridAlgorithm(InitialSet is, Heuristic h, double tol) {
		this.is = is;
		this.h= h;
		this.tol = tol;
	}
	
	public int process(String filePolygon, String fileToSaveFin) {
		System.out.println("processing");
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
