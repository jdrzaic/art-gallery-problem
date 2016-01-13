package metaheuristics.project.agp.gui;


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
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import metaheuristics.project.agp.instances.GalleryInstance;
import metaheuristics.project.agp.instances.components.Polygon;
import metaheuristics.project.agp.instances.util.BenchmarkFileInstanceLoader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;


public class Controller implements Initializable {

	public static final String fileResults = "res.txt";
	
	private static Drawing drawing;

	static File benchmark;
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
	
	@FXML private Canvas canvas;
	
	@FXML private MenuItem ocisti;
	
	GraphicsContext gc;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		gc = canvas.getGraphicsContext2D();
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(0.5);
		gc.strokeLine(0, 0, canvas.maxWidth(0), 0);
		gc.strokeLine(canvas.maxWidth(0), canvas.maxHeight(0), canvas.maxWidth(0), 0);
		gc.strokeLine(canvas.maxWidth(0), canvas.maxHeight(0), 0, canvas.maxHeight(0));
		gc.strokeLine(0, 0, 0,canvas.maxHeight(0));
		onClearClicked();
	}
	
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
			if(radio_dat.isSelected()) {
				try {
					GalleryInstance gi = bfil.load(benchmark.getAbsolutePath());
						GreedyController gc = new GreedyController();
						gc.process(gi, "res.txt");
				} catch(Exception e) {
					Alert wrongFileAlert = new Alert(AlertType.ERROR, 
							"Odabrana datoteka ne sadrži primjer u korektnom zapisu! Pokušajte ponovo.",
							ButtonType.OK);
					wrongFileAlert.setHeaderText("Greška");
					wrongFileAlert.showAndWait();
				}
			} else {
				//benchmark = null;
				if(drawing.gi.getVertices().size() < 3) {
					Alert wrongFileAlert = new Alert(AlertType.ERROR, 
							"Tlocrt galerija nemoguće je obraditi. Pokušajte ponovo.",
							ButtonType.OK);
					wrongFileAlert.setHeaderText("Greška");
					wrongFileAlert.showAndWait();
				} else {
					
					GreedyController gc = new GreedyController();
					gc.process(drawing.gi, "res.txt");
				}
			}
		}
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
			tmpHole.add(new Coordinate(x, y));
			if(tmpHole.size() > 2 && new LineSegment(new Coordinate(tmpx, tmpy), tmpHole.get(0)).getLength() < 5) {
				tmpx = tmpy = -1;
				if(curr == 0) {
					gi = new GalleryInstance((new ArrayList<>(tmpHole)));
					System.out.println(drawing.curr);
					System.out.println(drawing.gi.getVertices().toString());
				}else {
					gi.addHole(new Polygon(new ArrayList<Coordinate>(tmpHole)));
					System.out.println(drawing.curr);
					System.out.println(drawing.gi.getVertices().toString());
					for(Polygon h: drawing.gi.getHoles()) {
						System.out.println("rupa" + h.getVertices().toString());
					}
				}
				tmpHole.clear();
				curr++;
			}
		}
	}

	public static void generateVisualisation() {
		//if()
		try {
			Runtime.getRuntime().exec("/Users/jelenadrzaic/Documents/repos/art-gallery-problem/ArtGallery " +  
					benchmark.getAbsolutePath() +  " /Users/jelenadrzaic/Documents/repos/art-gallery-problem/res.txt  /Users/jelenadrzaic/Documents/repos/art-gallery-problem/cam.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		ResultsView rv = new ResultsView();
		rv.openWindow();
	}

	
}
