package metaheuristics.project.agp.gui;

import java.util.concurrent.CountDownLatch;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ProgressIndicator;
import metaheuristics.project.agp.alg.pso.PSO;
import metaheuristics.project.agp.instances.GalleryInstance;
import metaheuristics.project.agp.instances.util.BenchmarkFileInstanceLoader;

public class PSOController {
	
	private int pop;
	
	private int iter;
	
	private double tol;
	
	static GalleryInstance gi;
	
	static String filename; 
	
	static int n;
	
	private ProgressIndicator progress;
	
	/**
	 * @param pop
	 * @param iter
	 * @param tol
	 */
	public PSOController(String pop, String iter, String tol) {
		super();
		this.pop = Integer.parseInt(pop);
		this.iter = Integer.parseInt(iter);
		this.tol = Double.parseDouble(tol);
		System.out.println("Stvoren");
	}

	public void process(String filename, ProgressIndicator progress) {
		this.gi = new BenchmarkFileInstanceLoader().load(filename);
		this.filename = filename;
		onExecPSO(progress);
	}
	
	public void onExecPSO(ProgressIndicator progress) {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ui.fxml"));
		System.out.println(iter);
		System.out.println(tol);
		Service<Void> service = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						PSO pso = new PSO(); 
	            		progress.setProgress(0);
						pso.process(gi);
	            		progress.setProgress(1);
						n = gi.saveResults("/home/gbbanusic/Programiranje/PIOA/AGP/art-gallery-problem2/test_results_and_samples/res.txt"); 
						Controller.runVisualisation();
						 final CountDownLatch latch = new CountDownLatch(1);
		                    latch.await();  
						return null;
					}
				};
			}
		};
		service.start();
	}
}
