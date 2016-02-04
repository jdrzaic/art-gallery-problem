package metaheuristics.project.agp.gui;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import metaheuristics.project.agp.instances.GalleryInstance;
import metaheuristics.project.agp.instances.components.Polygon;
import metaheuristics.project.agp.instances.util.BenchmarkFileInstanceLoader;


public class Controller implements Initializable {

	public static final String fileResults = "test_results_and_samples/res.txt";
	
	private static Drawing drawing;

	static File benchmark;
	static File other;
	static int draw = 0;

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
	
	@FXML private RadioButton hybrid;
	
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
	
	@FXML private Canvas canvas;
	
	@FXML private MenuItem ocisti;
	

	
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
	 * 
	 */
	@FXML private Label pso_iter_txt;
	
	/**
	 * 
	 */
	@FXML private Label pso_pop_txt;
	
	/**
	 * 
	 */
	@FXML private Label pso_tol_txt;
	
	/**
	 * 
	 */
	@FXML private TextField pso_iter;
	
	/**
	 * 
	 */
	@FXML private TextField pso_pop;
	
	/**
	 * 
	 */
	@FXML private TextField pso_tol;
	
	@FXML private ProgressIndicator progress;
	
	GraphicsContext gc;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bindParametersVisibility();
		setPSOParamsInvisible();
		setGreedyParamsInvisible();
		
		gc = canvas.getGraphicsContext2D();
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(0.5);
		gc.strokeLine(0, 0, canvas.maxWidth(0), 0);
		gc.strokeLine(canvas.maxWidth(0), canvas.maxHeight(0), canvas.maxWidth(0), 0);
		gc.strokeLine(canvas.maxWidth(0), canvas.maxHeight(0), 0, canvas.maxHeight(0));
		gc.strokeLine(0, 0, 0,canvas.maxHeight(0));
		
		onClearClicked();
	}

	private void setGreedyParamsInvisible() {
		pokrivac.setVisible(false);
		heuristika.setVisible(false);
	}

	private void setGreedyParamsVisible() {
		pokrivac.setVisible(true);
		heuristika.setVisible(true);
	}
	
	private void setPSOParamsInvisible() {
		pso_pop.setVisible(false);
		pso_iter.setVisible(false);
		pso_tol_txt.setVisible(false);
		pso_pop_txt.setVisible(false);
		pso_iter_txt.setVisible(false);
		pso_tol.setVisible(false);
	}
	
	private void setPSOParamsVisible() {
		pso_pop.setVisible(true);
		pso_iter.setVisible(true);
		pso_tol_txt.setVisible(true);
		pso_pop_txt.setVisible(true);
		pso_iter_txt.setVisible(true);
		pso_tol.setVisible(true);
	}

	private void bindParametersVisibility() {
		pso_iter_txt.managedProperty().bind(pso_iter_txt.visibleProperty());
		pso_pop_txt.managedProperty().bind(pso_iter_txt.visibleProperty());
		pso_tol_txt.managedProperty().bind(pso_iter_txt.visibleProperty());
		pso_iter.managedProperty().bind(pso_iter_txt.visibleProperty());
		pso_pop.managedProperty().bind(pso_iter_txt.visibleProperty());
		pso_tol.managedProperty().bind(pso_iter_txt.visibleProperty());
		pokrivac.managedProperty().bind(pokrivac.visibleProperty());
		heuristika.managedProperty().bind(heuristika.visibleProperty());
	}
	
	/**
	 * Method for choosing file for algorithms.
	 */
	public void onFileChooseClicked() {
		FileChooser fc = new FileChooser();
		File file = fc.showOpenDialog(null);
		if(file != null) {
			odabr_dat.setText("odabrana datoteka: " + file.getName());
			benchmark = file;
			check_dat_sel.setSelected(true);
		}
	}
	
	public void onButtonNext() {
		if(heur_ger.isSelected()) {
			GreedyCase();
		}  else if (pso_gen.isSelected()) {
			PSOCase();
		} else if (radio_gen.isSelected()) {
			GenCase();
		}else if(hybrid.isSelected()) {
			HybridCase();
		} 
	}

	private void HybridCase() {
		if(benchmark != null) System.out.println(benchmark.getName());
		if(radio_dat.isSelected()) {
			
			try {
				if(draw == 1 && other != null) {
					benchmark = new File(other.getAbsolutePath());
				}
				draw = 0;
				HybridController hc = new HybridController();
				hc.process(benchmark.getAbsolutePath(), "test_results_and_samples/res.txt");
			} catch(Exception e) {
				WrongFileAlert();
			}
		} else {
			if(draw == 0 && benchmark != null) {
				other = new File(benchmark.getAbsolutePath());
			}
			draw = 1;
			//benchmark = null;
			if(drawing.gi == null || drawing.gi.getVertices().size() < 3) {
				GalleryError();
			} else {
				HybridController hc = new HybridController();
				generateBenchmarkFromDraw();
				hc.process("test_results_and_samples/pol.txt", "test_results_and_samples/res.txt");
			}
		}
	}

	private void GenCase() {
		if(benchmark != null) System.out.println(benchmark.getName());
		if(radio_dat.isSelected()) {
			
			try {
				if(draw == 1 && other != null) {
					benchmark = new File(other.getAbsolutePath());
				}
				draw = 0;
				GeneticController gc = new GeneticController();
				System.out.println(benchmark.getAbsolutePath());
				gc.process(benchmark.getAbsolutePath());
			} catch(Exception e) {
				WrongFileAlert();
			}
		} else {
			if(draw == 0 && benchmark != null) {
				other = new File(benchmark.getAbsolutePath());
			}
			draw = 1;
			if(drawing.gi == null || drawing.gi.getVertices().size() < 3) {
				GalleryError();
			} else {
				GeneticController gc = new GeneticController();
				generateBenchmarkFromDraw();
				gc.process("test_results_and_samples/pol.txt");
			}
		}
	}

	private void PSOCase() {
		if(benchmark != null) System.out.println(benchmark.getName());
		if(radio_dat.isSelected()) {
			
			try {
				if(draw == 1 && other != null) {
					benchmark = new File(other.getAbsolutePath());
				}
				draw = 0;
				PSOController psoc = new PSOController(pso_pop.getText(), pso_iter.getText(), pso_tol.getText());
				psoc.process(benchmark.getAbsolutePath(), progress);
			} catch(Exception e) {
				WrongFileAlert();
			}
		} else {
			if(draw == 0 && benchmark != null) {
				other = new File(benchmark.getAbsolutePath());
			}
			draw = 1;
			if(drawing.gi == null || drawing.gi.getVertices().size() < 3) {
				GalleryError();
			} else {
				PSOController psoc = new PSOController(pso_pop.getText(), pso_iter.getText(), pso_tol.getText());
				generateBenchmarkFromDraw();
				psoc.process("test_results_and_samples/pol.txt", progress);
			}
		}
	}

	private void GreedyCase() {
		if(benchmark != null) System.out.println(benchmark.getName());
		if(radio_dat.isSelected()) {
			try {
				if(draw == 1 && other != null) {
					benchmark = new File(other.getAbsolutePath());
				}
				draw = 0;
				GalleryInstance gi = bfil.load(benchmark.getAbsolutePath());
				System.out.println(gi.getVertices().toString());
				GreedyController gc = new GreedyController(pokrivac.getSelectionModel().getSelectedItem().toString(), heuristika.getSelectionModel().getSelectedItem().toString());
				gc.process(gi, "test_results_and_samples/res.txt", progress);
			} catch(Exception e) {
				WrongFileAlert();
			}
		} else {
			if(draw == 0 && benchmark != null) {
				other = new File(benchmark.getAbsolutePath());
			}
			draw = 1;
			if(drawing.gi == null || drawing.gi.getVertices().size() < 3) {
				GalleryError();
			} else {
				GreedyController gc = new GreedyController(pokrivac.getSelectionModel().getSelectedItem().toString(), heuristika.getSelectionModel().getSelectedItem().toString());
				gc.process(drawing.gi, "test_results_and_samples/res.txt", progress);
			}
		}
	}
	
	private void WrongFileAlert() {
		Alert wrongFileAlert = new Alert(AlertType.ERROR, 
				"Odabrana datoteka ne sadrži primjer u korektnom zapisu! Pokušajte ponovo.",
				ButtonType.OK);
		wrongFileAlert.setHeaderText("Greška");
		wrongFileAlert.showAndWait();
	}
	

	private void GalleryError() {
		Alert wrongFileAlert = new Alert(AlertType.ERROR, 
				"Tlocrt galerija nemoguće je obraditi. Pokušajte ponovo.",
				ButtonType.OK);
		wrongFileAlert.setHeaderText("Greška");
		wrongFileAlert.showAndWait();
	}

	/**
	 * 
	 */
	public void onImageViewClicked() {
	    canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if(drawing.tmpy == -1) {
					drawing.tmpx = event.getX();
					drawing.tmpy = event.getY();
				}
				gc.setFill(null);
				gc.setStroke(Color.BLUE);
				gc.setLineWidth(1);
				gc.strokeLine(drawing.tmpx, drawing.tmpy, event.getX(), event.getY());
				drawing.add(event.getX(), event.getY());
			}
	    	
		}); 	    	
	}
	
	/**
	 * 
	 */
	public void showPSOParameters(){
		setGreedyParamsInvisible();
		setPSOParamsVisible();
	}
	
	/**
	 * 
	 */
	public void showGreedyParameters(){
		setPSOParamsInvisible();
		setGreedyParamsVisible();
	}
	
	
	public void onClearClicked() {
		gc.clearRect(2, 2, canvas.maxWidth(0)-5, canvas.maxHeight(0)-5);
		drawing = new Drawing();

	}
	
	private static class Drawing {
		GalleryInstance gi;
		int border = 0;
		ArrayList<Coordinate> tmpHole = new ArrayList<>();
		
		double tmpx = -1;
		double tmpy = -1;
		
		int curr = 0;
		
		public void add(double x, double y) {
			this.tmpx = x;
			this.tmpy = y;
			if(tmpHole.size() > 2 && new LineSegment(new Coordinate(tmpx, tmpy), tmpHole.get(0)).getLength() < 8) {
				tmpx = tmpy = -1;
				if(curr == 0) {
					gi = new GalleryInstance((new ArrayList<>(tmpHole)));
					System.out.println(drawing.curr);
					System.out.println(drawing.gi.getVertices().toString());
				}else {
					gi.addHole(new Polygon(new ArrayList<Coordinate>(tmpHole)));
				}
				tmpHole.clear();
				curr++;
			}else{
				tmpHole.add(new Coordinate(x, y));
			}
		}
	}

	public static void runVisualisation() {
		//nacrtana galerija
		if(draw == 1) {
			generateBenchmarkFromDraw();
		}
		try {
			Process p = Runtime.getRuntime().exec("./ArtGallery " +  
					benchmark.getAbsolutePath() +  " test_results_and_samples/res.txt  test_results_and_samples/res.png");
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				System.err.println("Error wainting bash");
			}
		} catch (IOException e) {
			System.err.println("Error executing bash");
		}
	}
	
	private static void generateBenchmarkFromDraw() {
		StringBuilder sb = new StringBuilder();
		GalleryInstance gi = drawing.gi;
		sb.append(gi.getVertices().size()).append(" ");
		for(Coordinate c : gi.getVertices()) {
			sb.append(new Double(c.x).intValue() + "/1 ").append(new Double(c.y).intValue() + "/1 ");
		}
		if(gi.getHoles().size() > 0) sb.append(gi.getHoles().size());
		for(Polygon h : gi.getHoles()) {
			sb.append(" " + h.getVertices().size() + " ");
			for(Coordinate c : h.getVertices()) {
				sb.append(new Double(c.x).intValue() + "/1 ").append(new Double(c.y).intValue() + "/1 ");
			}
		}
		try {
			FileUtils.writeStringToFile(new File("test_results_and_samples/pol.txt"), sb.toString());
		} catch (IOException ignorable) {}
		benchmark = new File("test_results_and_samples/pol.txt");
	}

	public static void openResult(int n) {
		ResultsView rv = new ResultsView();
		rv.openWindow(n, benchmark.getName());
	}
}
