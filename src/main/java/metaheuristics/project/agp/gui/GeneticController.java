 package metaheuristics.project.agp.gui;

import java.nio.file.Files;
import java.nio.file.Paths;
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
	                    ga.process(filename, "res.txt");
	                    Thread.sleep(1000);
	                    System.out.println("processed");
						Controller.runVisualisation();
						System.out.println(Files.readAllLines(Paths.get("cam.txt")));
						System.out.println(Files.readAllLines(Paths.get("res.txt")));
						Platform.runLater(new Runnable() {                          
	                        @Override
	                        public void run() {
	                            try{
	        						Controller.openResult(0);
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

