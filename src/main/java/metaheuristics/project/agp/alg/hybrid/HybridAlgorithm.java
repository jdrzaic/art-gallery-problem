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
	
	public HybridAlgorithm(InitialSet is, Heuristic h) {
		this.is = is;
		this.h= h;
	}
	
	public void process(String filePolygon, String fileToSaveFin) {
		System.out.println("processing");
		BenchmarkFileInstanceLoader bfil = new BenchmarkFileInstanceLoader();
		GalleryInstance gi = bfil.load(filePolygon);
		HeuristicGreedy hg = new HeuristicGreedy(is, h);
		gi.setCameras(hg);
		gi.saveResults("test_results_and_samples/geninit.txt");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("calling genetic");
		GeneticAlgorthm ga = new GeneticAlgorthm();
		ga.process(filePolygon, fileToSaveFin, "test_results_and_samples/geninit.txt");
	}
	
	public static void main(String[] args) {
		HybridAlgorithm ha = new HybridAlgorithm(InitialSet.TRIANGULATION_COVER, new A7());
		ha.process("cam.txt", "res.txt");
	}
}
