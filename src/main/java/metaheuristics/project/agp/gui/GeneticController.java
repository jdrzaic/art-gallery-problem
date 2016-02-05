 package metaheuristics.project.agp.gui;

import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import metaheuristics.project.agp.alg.genetic.GeneticAlgorthm;

public class GeneticController {
	
	static String filename;
	
	public void process(String filename) {
		System.out.println("in genetic controller");
		this.filename = filename;
		onExecGenetic();
	}

	public void onExecGenetic() {
		Service<Void> service = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						GeneticAlgorthm ga = new GeneticAlgorthm();
	                    final CountDownLatch latch = new CountDownLatch(1);
	                    System.out.println("Before processed");
	                    System.out.println("benchmark: " + filename);
	                    int n = ga.process(filename, "test_results_and_samples/res.txt");
	                    //Thread.sleep(1000);
	                    System.out.println("processed");
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
		service.start();
	}
}

