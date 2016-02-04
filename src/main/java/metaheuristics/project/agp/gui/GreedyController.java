package metaheuristics.project.agp.gui;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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
		heuristics.put("najveća površina", new A7());
		heuristics.put("najveći opseg", new A6());
		
		cover = new HashMap<>();
		cover.put("Vrhovi poligona", InitialSet.VERTEX_COVER);
		cover.put("Triangulacija poligona", InitialSet.TRIANGULATION_COVER);
		cover.put("Unija prve dvije opcije", InitialSet.VERTEX_TRIANGULATION_COVER);
	}
	
	private String heur;
	private String initc;
	
	/**
	 * @param heur
	 * @param initc
	 */
	public GreedyController(String initc, String heur) {
		super();
		this.heur = heur;
		this.initc = initc;
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
	                    System.out.println("Before processed");
	                    try{
	            		progress.setProgress(0);
	                    hg.process(gi); 
	            		progress.setProgress(1);
	                    } catch(Exception e){
	                    	e.printStackTrace();
	                    }
	                    System.out.println("procKKKXessed");
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
		System.out.println("Start");
		service.start();
	}
	
}
