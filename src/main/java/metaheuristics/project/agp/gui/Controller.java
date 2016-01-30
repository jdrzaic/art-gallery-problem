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
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import metaheuristics.project.agp.instances.GalleryInstance;
import metaheuristics.project.agp.instances.components.Polygon;
import metaheuristics.project.agp.instances.util.BenchmarkFileInstanceLoader;


public class Controller implements Initializable {

	public static final String fileResults = "res.txt";
	
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
			if(benchmark != null) System.out.println(benchmark.getName());
			if(radio_dat.isSelected()) {
				
				try {
					if(draw == 1 && other != null) {
						benchmark = new File(other.getAbsolutePath());
					}
					draw = 0;
					GalleryInstance gi = bfil.load(benchmark.getAbsolutePath());
					System.out.println(gi.getVertices().toString());
						GreedyController gc = new GreedyController();
						gc.process(gi, "res.txt");
				} catch(Exception e) {
					e.printStackTrace();
					Alert wrongFileAlert = new Alert(AlertType.ERROR, 
							"Odabrana datoteka ne sadrži primjer u korektnom zapisu! Pokušajte ponovo.",
							ButtonType.OK);
					wrongFileAlert.setHeaderText("Greška");
					wrongFileAlert.showAndWait();
				}
			} else {
				if(draw == 0 && benchmark != null) {
					other = new File(benchmark.getAbsolutePath());
				}
				draw = 1;
				//benchmark = null;
				if(drawing.gi == null || drawing.gi.getVertices().size() < 3) {
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
		}  else if (pso_gen.isSelected()) {
			if(benchmark != null) System.out.println(benchmark.getName());
			if(radio_dat.isSelected()) {
				
				try {
					if(draw == 1 && other != null) {
						benchmark = new File(other.getAbsolutePath());
					}
					draw = 0;
					PSOController psoc = new PSOController();
					psoc.process(null, benchmark.getAbsolutePath());
				} catch(Exception e) {
					Alert wrongFileAlert = new Alert(AlertType.ERROR, 
							"Odabrana datoteka ne sadrži primjer u korektnom zapisu! Pokušajte ponovo.",
							ButtonType.OK);
					wrongFileAlert.setHeaderText("Greška");
					wrongFileAlert.showAndWait();
				}
			} else {
				if(draw == 0 && benchmark != null) {
					other = new File(benchmark.getAbsolutePath());
				}
				draw = 1;
				//benchmark = null;
				if(drawing.gi == null || drawing.gi.getVertices().size() < 3) {
					Alert wrongFileAlert = new Alert(AlertType.ERROR, 
							"Tlocrt galerija nemoguće je obraditi. Pokušajte ponovo.",
							ButtonType.OK);
					wrongFileAlert.setHeaderText("Greška");
					wrongFileAlert.showAndWait();
				} else {
					PSOController psoc = new PSOController();
					generateBenchmarkFromDraw();
					psoc.process(drawing.gi, "cam.txt");
				}
			}
		} else if (radio_gen.isSelected()) {
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
					Alert wrongFileAlert = new Alert(AlertType.ERROR, 
							"Odabrana datoteka ne sadrži primjer u korektnom zapisu! Pokušajte ponovo.",
							ButtonType.OK);
					wrongFileAlert.setHeaderText("Greška");
					wrongFileAlert.showAndWait();
				}
			} else {
				if(draw == 0 && benchmark != null) {
					other = new File(benchmark.getAbsolutePath());
				}
				draw = 1;
				if(drawing.gi == null || drawing.gi.getVertices().size() < 3) {
					Alert wrongFileAlert = new Alert(AlertType.ERROR, 
							"Tlocrt galerija nemoguće je obraditi. Pokušajte ponovo.",
							ButtonType.OK);
					wrongFileAlert.setHeaderText("Greška");
					wrongFileAlert.showAndWait();
				} else {
					GeneticController gc = new GeneticController();
					generateBenchmarkFromDrawGenetic();
					gc.process("cam.txt");
				}
			}
		}else if(hybrid.isSelected()) {
			if(benchmark != null) System.out.println(benchmark.getName());
			if(radio_dat.isSelected()) {
				
				try {
					if(draw == 1 && other != null) {
						benchmark = new File(other.getAbsolutePath());
					}
					draw = 0;
					HybridController hc = new HybridController();
					hc.process(benchmark.getAbsolutePath(), "res.txt");
				} catch(Exception e) {
					e.printStackTrace();
					Alert wrongFileAlert = new Alert(AlertType.ERROR, 
							"Odabrana datoteka ne sadrži primjer u korektnom zapisu! Pokušajte ponovo.",
							ButtonType.OK);
					wrongFileAlert.setHeaderText("Greška");
					wrongFileAlert.showAndWait();
				}
			} else {
				if(draw == 0 && benchmark != null) {
					other = new File(benchmark.getAbsolutePath());
				}
				draw = 1;
				//benchmark = null;
				if(drawing.gi == null || drawing.gi.getVertices().size() < 3) {
					Alert wrongFileAlert = new Alert(AlertType.ERROR, 
							"Tlocrt galerija nemoguće je obraditi. Pokušajte ponovo.",
							ButtonType.OK);
					wrongFileAlert.setHeaderText("Greška");
					wrongFileAlert.showAndWait();
				} else {
					HybridController hc = new HybridController();
					generateBenchmarkFromDrawGenetic();
					hc.process("cam.txt", "res.txt");
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
			if(tmpHole.size() > 2 && new LineSegment(new Coordinate(tmpx, tmpy), tmpHole.get(0)).getLength() < 8) {
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
					benchmark.getAbsolutePath() +  " res.txt  cam.png");
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				System.err.println("Error wainting bash");
			}
		} catch (IOException e) {
			System.err.println("Error executing bash");
		}
	}

	private static void generateBenchmarkFromDrawGenetic() {
		StringBuilder sb = new StringBuilder();
		GalleryInstance gi = drawing.gi;
		sb.append(gi.getVertices().size()).append(" ");
		for(int i = gi.getVertices().size() - 1; i >= 0; --i) {
			Coordinate c = gi.getOnIndex(i);
			sb.append(new Double(c.x).intValue() + "/1 ").append(new Double(c.y).intValue() + "/1 ");
		}
		if(gi.getHoles().size() > 0) sb.append(gi.getHoles().size());
		for(int i = gi.getHoles().size() - 1; i >= 0; --i) {
			Polygon h = gi.getHoleOnIndex(i);
			sb.append(" " + h.getVertices().size() + " ");
			for(Coordinate c : h.getVertices()) {
				sb.append(new Double(c.x).intValue() + "/1 ").append(new Double(c.y).intValue() + "/1 ");
			}
		}
		try {
			FileUtils.writeStringToFile(new File("cam.txt"), sb.toString());
		} catch (IOException ignorable) {
			ignorable.printStackTrace();
		}
		benchmark = new File("cam.txt");
		System.out.println();
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
			FileUtils.writeStringToFile(new File("cam.txt"), sb.toString());
		} catch (IOException ignorable) {
			ignorable.printStackTrace();
		}
		benchmark = new File("cam.txt");
		System.out.println();
	}

	public static void openResult(int n) {
		ResultsView rv = new ResultsView();
		rv.openWindow(n, benchmark.getName());
	}
}
