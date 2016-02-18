package metaheuristics.project.agp.gui;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ProgressIndicator;
import metaheuristics.project.agp.alg.greedy.HeuristicGreedy;
import metaheuristics.project.agp.alg.greedy.HeuristicGreedy.InitialSet;
import metaheuristics.project.agp.alg.greedy.heuristics.A6;
import metaheuristics.project.agp.alg.greedy.heuristics.A7;
import metaheuristics.project.agp.alg.greedy.heuristics.Heuristic;
import metaheuristics.project.agp.instances.GalleryInstance;

public class GreedyController {
	
	
	static GalleryInstance gi;
	static String filename;
	
	static HashMap<String, Heuristic> heuristics;
	static HashMap<String, InitialSet> cover;
	
	static {
		heuristics = new HashMap<>();
		//UTF
		heuristics.put("najveća površina", new A7());
		heuristics.put("najveći opseg", new A6());
		
		cover = new HashMap<>();
		cover.put("Vrhovi poligona", InitialSet.VERTEX_COVER);
		cover.put("Triangulacija poligona", InitialSet.TRIANGULATION_COVER);
		cover.put("Unija prve dvije opcije", InitialSet.VERTEX_TRIANGULATION_COVER);
	}
	
	private String heur;
	private String initc;
	private double tol;
	
	/**
	 * @param heur
	 * @param initc
	 */
	public GreedyController(String initc, String heur, double tol) {
		super();
		this.heur = heur;
		this.initc = initc;
		this.tol  = tol;
	}

	public void process(GalleryInstance gi, String filename, ProgressIndicator progress) {
		this.gi = gi;
		System.out.println(gi.getVertices().toString());
		this.filename = filename;
		onExecGreedy(progress);
	}

	public void onExecGreedy(ProgressIndicator progress) {
		System.out.println("Greedy called");
		System.out.println(heur);
		System.out.println(initc);
		Service<Void> service = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						HeuristicGreedy hg = new HeuristicGreedy(
								cover.get(initc), 
								heuristics.get(heur));
						hg.setTol(tol);
	                    hg.process(gi); 
						int n = gi.saveResults(filename);
	                    final CountDownLatch latch = new CountDownLatch(1);
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
