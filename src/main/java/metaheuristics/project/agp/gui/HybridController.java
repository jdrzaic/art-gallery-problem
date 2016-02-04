package metaheuristics.project.agp.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import metaheuristics.project.agp.alg.greedy.HeuristicGreedy;
import metaheuristics.project.agp.alg.greedy.HeuristicGreedy.InitialSet;
import metaheuristics.project.agp.alg.greedy.heuristics.A6;
import metaheuristics.project.agp.alg.greedy.heuristics.A7;
import metaheuristics.project.agp.alg.greedy.heuristics.Heuristic;
import metaheuristics.project.agp.alg.hybrid.HybridAlgorithm;
import metaheuristics.project.agp.instances.GalleryInstance;

public class HybridController {
	
	static GalleryInstance gi;
	static String polygonFile;
	static String toSaveIn;
	
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
	
	//heuristic greedy fxml
	/**
	 * combobox to chose initial cover in greedy
	 * 		 
	 * */
	@FXML private ComboBox<String> pokrivac;
	/**
	 * combobox to chose heuristic in greedy
	 */
	@FXML private ComboBox<String> heuristika;
	/**
	 * button to execute algorithm
	 */
	@FXML private Button izvrsi;
		
	@FXML private ProgressBar progres; 
	
	public void process(String polygonFile, String toSaveIn) {
		HybridController.polygonFile = polygonFile;
		HybridController.toSaveIn = toSaveIn;
		System.out.println(polygonFile + "  " + toSaveIn);
		openHeurChoser();
	}

	public void onExecHybrid() {
		System.out.println("Greedy called");
		String heur = (String) heuristika.getSelectionModel().getSelectedItem().toString();
		String initc = (String) pokrivac.getSelectionModel().getSelectedItem().toString();
		System.out.println(heur);
		System.out.println(initc);
		Service<Void> service = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						HybridAlgorithm ha = new HybridAlgorithm(cover.get(initc), heuristics.get(heur));
	                    final CountDownLatch latch = new CountDownLatch(1);
	                    ha.process(polygonFile, toSaveIn);
	                    System.out.println("processed");
						Controller.runVisualisation();
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
		Stage toClose = (Stage)heuristika.getScene().getWindow();
		toClose.close();
	}
	
	public void openHeurChoser() {
		System.out.println("Open heur chooser called");
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hybrid.fxml"));
        Parent root1;
		try {
			root1 = (Parent) fxmlLoader.load();
	        Stage stage = new Stage();
	        stage.initModality(Modality.APPLICATION_MODAL);
	        stage.initStyle(StageStyle.UNDECORATED);
	        stage.setTitle("ABC");
	        stage.setScene(new Scene(root1));  
	        stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
