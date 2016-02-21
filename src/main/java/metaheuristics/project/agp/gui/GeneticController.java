 package metaheuristics.project.agp.gui;

import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ProgressIndicator;
import metaheuristics.project.agp.alg.genetic.GeneticAlgorthm;

public class GeneticController {
	
	static String filename;
	
	public void process(String filename, ProgressIndicator progress) {
		GeneticController.filename = filename;
		onExecGenetic(progress);
	}

	public void onExecGenetic(ProgressIndicator progress) {
		Service<Void> service = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						GeneticAlgorthm ga = new GeneticAlgorthm();
	                    final CountDownLatch latch = new CountDownLatch(1);
	                    int n = ga.process(filename, "test_results_and_samples/res.txt");
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

