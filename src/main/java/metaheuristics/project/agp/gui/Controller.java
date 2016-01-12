package metaheuristics.project.agp.gui;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import metaheuristics.project.agp.instances.GalleryInstance;
import metaheuristics.project.agp.instances.util.BenchmarkFileInstanceLoader;

import java.io.File;
import java.io.IOException;


public class Controller {

	private File benchmark;
	static BenchmarkFileInstanceLoader bfil = new BenchmarkFileInstanceLoader();

	/**
	 * Menu item in main gui menu.
	 * when clicked, opens file chooser
	 */
	@FXML private MenuItem file_chose;
	/**
	 * select genetic algorithm
	 */
	@FXML private RadioButton radio_gen;
	/**
	 * select pso algorithm
	 */
	@FXML private RadioButton pso_gen;
	/**
	 * select greedy algorithm
	 */
	@FXML private RadioButton heur_ger;
	/**
	 * checked if file is selected
	 */
	@FXML private CheckBox check_dat_sel;
	/**
	 * label displaying selected file
	 */
	@FXML private Label odabr_dat; 
	/**
	 * radio button - picture will be used
	 */
	@FXML private RadioButton radio_tloc;
	/**
	 * radio button - file will be used
	 */
	@FXML private RadioButton radio_dat;
	/**
	 * Button next
	 */
	@FXML private Button button_nast;
	
	@FXML private ImageView canvas;
	
	
	//heuristic greedy
	/**
	 * combobox to chose initial cover in greedy
	 */
	@FXML private ComboBox pokrivac;
	/**
	 * combobox to chose heuristic in greedy
	 */
	@FXML private ComboBox heuristika;
	/**
	 * button to execute algorithm
	 */
	@FXML private Button izvrsi;
	
	public void onFileChooseClicked() {
		FileChooser fc = new FileChooser();
		File file = fc.showOpenDialog(null);
		if(file != null) {
			odabr_dat.setText("odabrana datoteka: " + file.getName());
			this.benchmark = file;
			check_dat_sel.setSelected(true);
		}
	}
	
	public void onButtonNext() {
		if(radio_dat.isSelected()) {
			try {
				GalleryInstance gi = bfil.load(benchmark.getAbsolutePath());
				if(heur_ger.isSelected()) {
					openHeurChoser();
				}//dodati za druge algoritme
			} catch(Exception e) {
				Alert wrongFileAlert = new Alert(AlertType.ERROR, 
						"Odabrana datoteka ne sadrži primjer u korektnom zapisu! Pokušajte ponovo.",
						ButtonType.OK);
				wrongFileAlert.setHeaderText("Greška");
				wrongFileAlert.showAndWait();
			}
			
		}else {
			
		}
	}

	private void onExexGreedy() {
		
	}
	
	private void openHeurChoser() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("greedy.fxml"));
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
