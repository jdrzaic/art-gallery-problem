package metaheuristics.project.agp.gui;

import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ProgressIndicator;
import metaheuristics.project.agp.alg.pso.PSO;
import metaheuristics.project.agp.algorithms.pso.components.Particle;
import metaheuristics.project.agp.instances.GalleryInstance;
import metaheuristics.project.agp.instances.util.BenchmarkFileInstanceLoader;

/**
 * Class represents javafx controller for PSO algorithm.
 * @author gbbanusic
 *
 */
public class PSOController {
	
	/**
	 * Population number for PSO algorithm.
	 */
	private int pop;
	
	/**
	 * Iteration number for PSO algorithm.
	 */
	private int iter;
	
	/**
	 * Toleration number for PSO algorithm.
	 */
	private double tol;
	
	/**
	 * Gallery instance.
	 */
	private GalleryInstance gi;
	
	/**
	 * Number of cameras needed to cover gallery instance.
	 */
	private int n;
	
	/**
	 * Constructor creates a controller with PSO algorithm parameters.
	 * @param pop is the number of population.
	 * @param iter is the number of iteration.
	 * @param tol is the toleration percentage.
	 */
	public PSOController(String pop, String iter, String tol, String indivFact, String socFact) {
		super();
		this.pop = Integer.parseInt(pop);
		this.iter = Integer.parseInt(iter);
		this.tol = Double.parseDouble(tol);
		Particle.c1 = Double.parseDouble(indivFact);
		Particle.c2 = Double.parseDouble(socFact);
		System.out.println("Stvoren");
	}

	/**
	 * Method starts the PSO algorithm and shows results.
	 * @param filename is the gallery instance file name.
	 * @param progress is the progress indicator.
	 */
	public void process(String filename, ProgressIndicator progress) {
		this.gi = new BenchmarkFileInstanceLoader().load(filename);
		onExecPSO(progress);
	}
	
	/**
	 * Method starts PSO algorithm and shows results.
	 * @param progress is the algorithm progress indicator.
	 */
	public void onExecPSO(ProgressIndicator progress) {
		// TODO remove
		System.out.println(iter);
		System.out.println(tol);
		Service<Void> service = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					 final CountDownLatch latch = new CountDownLatch(1);
					@Override
					protected Void call() throws Exception {
						PSO pso = new PSO(); 
						pso.init(tol, iter, pop);
						pso.process(gi);
						n = gi.saveResults("test_results_and_samples/res.txt"); 
						Controller.runVisualisation();
						Platform.runLater(new Runnable() {                          
	                        @Override
	                        public void run() {
	                            try{
	        						Controller.openResult(n);
	                            }finally{
	                                latch.countDown();
	                            }
	                        }
	                    });
						return null;
					}
				};
			}
		};
service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			
			@Override
			public void handle(WorkerStateEvent event) {
		        progress.setProgress(0d);
		        progress.setVisible(false);
			}
		});
		progress.setVisible(true);
	    progress.setProgress(-1d);
		service.start();
	}
	
}
