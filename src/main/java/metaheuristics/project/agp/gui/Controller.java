package metaheuristics.project.agp.gui;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashSet;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import metaheuristics.project.agp.instances.GalleryInstance;
import metaheuristics.project.agp.instances.components.Polygon;
import metaheuristics.project.agp.instances.util.BenchmarkFileInstanceLoader;

/**
 * 
 * @author jdrzaic, gbbanusic
 *
 */
public class Controller implements Initializable {

	public static final String fileResults = "test_results_and_samples/res.txt";
	
	private static Drawing drawing;

	static String algorithm;
	static File benchmark;
	static File other;
	static int draw = 0;

	static BenchmarkFileInstanceLoader bfil = new BenchmarkFileInstanceLoader();

	/**
	 * Menu item in main gui menu.
	 * When clicked, opens file chooser.
	 */
	@FXML private MenuItem file_chose;
	/**
	 * Select genetic algorithm.
	 */
	@FXML private RadioButton radio_gen;
	/**
	 * Select pso algorithm.
	 */
	@FXML private RadioButton pso_gen;
	/**
	 * Select greedy algorithm.
	 */
	@FXML private RadioButton heur_ger;
	
	/**
	 * Select hybrid algorithm.
	 */
	@FXML private RadioButton hybrid;
	
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
	 * Combobox to chose initial cover in greedy.
	 * */
	@FXML private ComboBox<String> pokrivac;
	
	/**
	 * Combobox to chose heuristic in greedy.
	 */
	@FXML private ComboBox<String> heuristika;
	
	/**
	 * Combobox to chose initial cover in greedy. 
	 * */
	@FXML private ComboBox<String> hybPokrivac;
	/**
	 * Combobox to chose heuristic in greedy.
	 */
	@FXML private ComboBox<String> hybHeuristika;
	
	/**
	 * Label for pso iteration input.
	 */
	@FXML private Label pso_iter_txt;
	
	/**
	 * Label for pso population input.
	 */
	@FXML private Label pso_pop_txt;
	
	/**
	 * Label for pso toleration input.
	 */
	@FXML private Label pso_tol_txt;
	
	/**
	 * Label for pso toleration input.
	 */
	@FXML private Label pso_indiv_txt;
	
	/**
	 * Label for pso social factor input.
	 */
	@FXML private Label pso_soc_txt;
	
	/**
	 * Label for pso triangle choosing toleration  input.
	 */
	@FXML private Label pso_triang_tol_txt;
	
	/**
	  * PSO iteration input.
	 */
	@FXML private TextField pso_iter;
	
	/**
	  * PSO population input.
	 */
	@FXML private TextField pso_pop;
	
	/**
	 * PSO toleration input.
	 */
	@FXML private TextField pso_tol;
	
	/**
	 * PSO individual factor input.
	 */
	@FXML private TextField pso_indiv;
	
	/**
	 * PSO social factor input.
	 */
	@FXML private TextField pso_soc;
	
	/**
	 * Greedy toleration input.
	 */
	@FXML private TextField gre_tol;
	
	/**
	 * PSO triangle choosing toleration input.
	 */
	@FXML private TextField pso_triang_tol;
	
	/**
	 * Label for greedy toleration input.
	 */
	@FXML private Label gre_tol_txt;
	
	/**
	 * hybrid toleration input.
	 */
	@FXML private TextField hyb_tol;
	
	/**
	 * Label for hybrid toleration input.
	 */
	@FXML private Label hyb_tol_txt;
	
	/**
	 * Progress indicator whether an algorithm is running.
	 */
	@FXML private ProgressIndicator progress;
	
	/**
	 * Graphics context.
	 */
	GraphicsContext gc;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bindParametersVisibility();
		setPSOParamsInvisible();
		setGreedyParamsInvisible();
		setHybParamsInvisible();
		gc = canvas.getGraphicsContext2D();
		
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(0.5);
		gc.strokeLine(0, 0, canvas.maxWidth(0), 0);
		gc.strokeLine(canvas.maxWidth(0), canvas.maxHeight(0), canvas.maxWidth(0), 0);
		gc.strokeLine(canvas.maxWidth(0), canvas.maxHeight(0), 0, canvas.maxHeight(0));
		gc.strokeLine(0, 0, 0,canvas.maxHeight(0));
		Tooltip.install(odabr_dat, new Tooltip(odabr_dat.getText()));
		onClearClicked();
	}

	/**
	 * Method sets greedy parameters invisible.
	 */
	private void setGreedyParamsInvisible() {
		pokrivac.setVisible(false);
		heuristika.setVisible(false);
		gre_tol.setVisible(false);
		gre_tol_txt.setVisible(false);
	}

	/**
	 * Method sets greedy parameters visible.
	 */
	private void setGreedyParamsVisible() {
		pokrivac.setVisible(true);
		heuristika.setVisible(true);
		gre_tol.setVisible(true);
		gre_tol_txt.setVisible(true);
	}
	
	/**
	 * Method sets gybride parameters invisible.
	 */
	private void setHybParamsInvisible() {
		hybPokrivac.setVisible(false);
		hybHeuristika.setVisible(false);
		hyb_tol.setVisible(false);
		hyb_tol_txt.setVisible(false);
	}

	/**
	 * Method sets hybrid parameters visible.
	 */
	private void setHybParamsVisible() {
		hybPokrivac.setVisible(true);
		hybHeuristika.setVisible(true);
		hyb_tol.setVisible(true);
		hyb_tol_txt.setVisible(true);
	}
	
	/**
	 * Method sets PSO parameters invisible.
	 */
	private void setPSOParamsInvisible() {
		pso_pop.setVisible(false);
		pso_iter.setVisible(false);
		pso_tol_txt.setVisible(false);
		pso_pop_txt.setVisible(false);
		pso_iter_txt.setVisible(false);
		pso_tol.setVisible(false);
		pso_soc_txt.setVisible(false);
		pso_soc.setVisible(false);
		pso_indiv_txt.setVisible(false);
		pso_indiv.setVisible(false);
		pso_triang_tol.setVisible(false);
		pso_triang_tol_txt.setVisible(false);
	}
	
	/**
	 * Method sets PSO parameters visible.
	 */
	private void setPSOParamsVisible() {
		pso_pop.setVisible(true);
		pso_iter.setVisible(true);
		pso_tol_txt.setVisible(true);
		pso_pop_txt.setVisible(true);
		pso_iter_txt.setVisible(true);
		pso_tol.setVisible(true);
		pso_soc_txt.setVisible(true);
		pso_soc.setVisible(true);
		pso_indiv_txt.setVisible(true);
		pso_indiv.setVisible(true);
		pso_triang_tol.setVisible(true);
		pso_triang_tol_txt.setVisible(true);
	}

	/**
	 * Method binds javafx elements to their container depending on their visibility.
	 */
	private void bindParametersVisibility() {
		pso_iter_txt.managedProperty().bind(pso_iter_txt.visibleProperty());
		pso_pop_txt.managedProperty().bind(pso_pop_txt.visibleProperty());
		pso_tol_txt.managedProperty().bind(pso_tol_txt.visibleProperty());
		pso_iter.managedProperty().bind(pso_iter.visibleProperty());
		pso_pop.managedProperty().bind(pso_pop.visibleProperty());
		pso_tol.managedProperty().bind(pso_tol.visibleProperty());
		pso_soc_txt.managedProperty().bind(pso_soc_txt.visibleProperty());
		pso_soc.managedProperty().bind(pso_soc.visibleProperty());
		pso_indiv.managedProperty().bind(pso_indiv.visibleProperty());
		pso_indiv_txt.managedProperty().bind(pso_indiv_txt.visibleProperty());
		pokrivac.managedProperty().bind(pokrivac.visibleProperty());
		heuristika.managedProperty().bind(heuristika.visibleProperty());
		hybPokrivac.managedProperty().bind(hybPokrivac.visibleProperty());
		hybHeuristika.managedProperty().bind(hybHeuristika.visibleProperty());
		gre_tol.managedProperty().bind(gre_tol_txt.visibleProperty());
		gre_tol_txt.managedProperty().bind(gre_tol_txt.visibleProperty());
		hyb_tol.managedProperty().bind(hyb_tol_txt.visibleProperty());
		hyb_tol_txt.managedProperty().bind(hyb_tol_txt.visibleProperty());
		pso_triang_tol.managedProperty().bind(pso_triang_tol.visibleProperty());
		pso_triang_tol_txt.managedProperty().bind(pso_triang_tol_txt.visibleProperty());

	}
	
	/**
	 * File choosing method.
	 */
	public void onFileChooseClicked() {
		FileChooser fc = new FileChooser();
		File file = fc.showOpenDialog(null);
		if(file != null) {
			odabr_dat.setText(file.getAbsolutePath());
			Tooltip.install(odabr_dat, new Tooltip(odabr_dat.getText()));
			benchmark = file;
		}
	}
	
	public void onButtonNext() {
		if(heur_ger.isSelected()) {
			algorithm = "Greedy algoritam";
			GreedyCase();
		}  else if (pso_gen.isSelected()) {
			algorithm = "PSO Algoritam";
			PSOCase();
		} else if (radio_gen.isSelected()) {
			algorithm = "Genetski algoritam";
			GenCase();
		}else if(hybrid.isSelected()) {
			algorithm = "Hibridni algoritam";
			HybridCase();
		} 
	}

	private void HybridCase() {
		if(radio_dat.isSelected()) {
			try {
				if(draw == 1 && other != null) {
					benchmark = new File(other.getAbsolutePath());
				}
				draw = 0;
				HybridController hc = new HybridController(hybPokrivac.getSelectionModel().getSelectedItem().toString(), 
						hybHeuristika.getSelectionModel().getSelectedItem().toString(), Double.valueOf(hyb_tol.getText()));
				hc.process(benchmark.getAbsolutePath(), "test_results_and_samples/res.txt", progress);
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
				drawing.gi.cameras = new HashSet<>();
				HybridController hc = new HybridController(hybPokrivac.getSelectionModel().getSelectedItem().toString(), 
						hybHeuristika.getSelectionModel().getSelectedItem().toString(), Double.valueOf(hyb_tol.getText()));
				generateBenchmarkFromDraw();
				hc.process("test_results_and_samples/pol.txt", "test_results_and_samples/res.txt", progress);
			}
		}
	}

	private void GenCase() {
		if(radio_dat.isSelected()) {
			try {
				if(draw == 1 && other != null) {
					benchmark = new File(other.getAbsolutePath());
				}
				draw = 0;
				GeneticController gc = new GeneticController();
				gc.process(benchmark.getAbsolutePath(), progress);
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
				drawing.gi.cameras = new HashSet<>();
				GeneticController gc = new GeneticController();
				generateBenchmarkFromDraw();
				gc.process("test_results_and_samples/pol.txt", progress);
			}
		}
	}

	private void PSOCase() {
		if(radio_dat.isSelected()) {
			try {
				if(draw == 1 && other != null) {
					benchmark = new File(other.getAbsolutePath());
				}
				draw = 0;
				PSOController psoc = new PSOController(pso_pop.getText(), pso_iter.getText(), pso_tol.getText(),pso_indiv.getText(), pso_soc.getText(), pso_triang_tol.getText());
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
				drawing.gi.cameras = new HashSet<>();
				PSOController psoc = new PSOController(pso_pop.getText(), pso_iter.getText(), pso_tol.getText(), pso_indiv.getText(), pso_soc.getText(),  pso_triang_tol.getText());
				generateBenchmarkFromDraw();
				psoc.process("test_results_and_samples/pol.txt", progress);
			}
		}
	}

	private void GreedyCase() {
		if(radio_dat.isSelected()) {
			try {
				if(draw == 1 && other != null) {
					benchmark = new File(other.getAbsolutePath());
				}
				draw = 0;
				GalleryInstance gi = bfil.load(benchmark.getAbsolutePath());
				System.out.println(gi.getVertices().toString());
				GreedyController gc = new GreedyController(pokrivac.getSelectionModel().getSelectedItem().toString(), 
						heuristika.getSelectionModel().getSelectedItem().toString(), Double.valueOf(gre_tol.getText()));
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
				drawing.gi.cameras = new HashSet<>();
				GreedyController gc = new GreedyController(pokrivac.getSelectionModel().getSelectedItem().toString(),
						heuristika.getSelectionModel().getSelectedItem().toString(), Double.valueOf(gre_tol.getText()));
				gc.process(drawing.gi, "test_results_and_samples/res.txt", progress);
			}
		}
	}
	
	private void WrongFileAlert() {
		Alert wrongFileAlert = new Alert(AlertType.ERROR, 
				"Primjer galerije nije odabran ili odabrana datoteka ne sadrži primjer u korektnom zapisu! Pokušajte ponovo.",
				ButtonType.OK);
		wrongFileAlert.setHeaderText("Greška");
		wrongFileAlert.showAndWait();
	}
	

	private void GalleryError() {
		Alert wrongFileAlert = new Alert(AlertType.ERROR, 
				"Tlocrt galerije nemoguće je obraditi. Pokušajte ponovo.",
				ButtonType.OK);
		wrongFileAlert.setHeaderText("Greška");
		wrongFileAlert.showAndWait();
	}

	
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
	 * Method shows PSO parameters and makes others invisible.
	 */
	public void showPSOParameters(){
		setGreedyParamsInvisible();
		setHybParamsInvisible();
		setPSOParamsVisible();
	}
	
	/**
	 * Method shows greedy parameters and makes others invisible.
	 */
	public void showGreedyParameters(){
		setPSOParamsInvisible();
		setHybParamsInvisible();
		setGreedyParamsVisible();
	}
	
	/**
	 * Method shows genetic parameters and makes others invisible.
	 */
	public void showGenParameters() {
		setGreedyParamsInvisible();
		setHybParamsInvisible();
		setPSOParamsInvisible();
	}
	
	/**
	 * Method shows hybrid parameters and makes others invisible.
	 */
	public void showHybParameters(){
		setGreedyParamsInvisible();
		setPSOParamsInvisible();
		setHybParamsVisible();
	}
	
	
	public void onClearClicked() {
		gc.clearRect(2, 2, canvas.maxWidth(0)-5, canvas.maxHeight(0)-5);
		gc.setFill(Paint.valueOf("white"));
		gc.fillRect(2, 2, canvas.maxWidth(0)-5, canvas.maxHeight(0)-5);
		drawing = new Drawing();

	}
	
	private static class Drawing {
		GalleryInstance gi;
		//int border = 0;
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
			String execName = System.getProperty("os.name").startsWith("Windows") ? "./ArtGallery.exe" : "./ArtGallery";
			String cmd = execName + " \"" +  
					benchmark.getAbsolutePath() +  "\" \"test_results_and_samples/res.txt\"  \"test_results_and_samples/res.png\"";
			Process p = Runtime.getRuntime().exec(cmd);
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				System.err.println("Error wainting bash");
			}
		} catch (IOException e) {
			System.err.println("Error executing bash");
		}
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {}
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
		GalleryInstance gi = new BenchmarkFileInstanceLoader().load(benchmark.getAbsolutePath());
		rv.openWindow(n, benchmark.getName(), gi.getVertices().size(), gi.getHoles().size(), algorithm);
	}
}
