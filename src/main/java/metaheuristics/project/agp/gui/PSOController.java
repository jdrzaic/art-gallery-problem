package metaheuristics.project.agp.gui;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import metaheuristics.project.agp.alg.greedy.PSO;
import metaheuristics.project.agp.instances.GalleryInstance;
import metaheuristics.project.agp.instances.util.BenchmarkFileInstanceLoader;

public class PSOController {

	@FXML private TextField iteracija;
	@FXML private TextField tolerancija;
	@FXML private TextField populacija;
	@FXML private Button kreni;
	@FXML private Label rezultat;
	@FXML private Button zatvori;
	
	static GalleryInstance gi;
	static String filename; 
	static int n;
	static Stage stage;
	
	public void process(GalleryInstance gi, String filename) {
		this.gi = gi;
		this.filename = filename;
		openHeurChoser();
	}
	
	public void openHeurChoser() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("pso.fxml"));
        Parent root1;
		try {
			root1 = (Parent) fxmlLoader.load();
	        stage = new Stage();
	        stage.initModality(Modality.APPLICATION_MODAL);
	        stage.initStyle(StageStyle.UNDECORATED);
	        stage.setTitle("ABC");
	        stage.setScene(new Scene(root1));  
	        stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void onExecPSO() {
		String iter = iteracija.getText();
		String tol = tolerancija.getText();
		String pop = populacija.getText();
		System.out.println(iter);
		System.out.println(tol);
		Service<Void> service = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						GalleryInstance gi = new BenchmarkFileInstanceLoader().load(filename);
						PSO pso = new PSO(); 
						pso.process(gi);
						n = pso.cover.size(); 
						 final CountDownLatch latch = new CountDownLatch(1);
							Platform.runLater(new Runnable() {                          
		                        @Override
		                        public void run() {
		                            try{
		                        		rezultat.setText(String.valueOf(n));
		                            }finally{
		                                latch.countDown();
		                            }
		                        }
		                    });
		                    latch.await();  
						return null;
					}
				};
			}
		};
		service.start();
	}
	
	public void onClose() {
		stage.close();
	}
}
