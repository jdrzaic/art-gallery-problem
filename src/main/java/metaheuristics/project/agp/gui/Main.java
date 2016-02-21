package metaheuristics.project.agp.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{
	
	@Override
	public void start(Stage primaryStage) {
		try { 
			Parent root = FXMLLoader.load(getClass().getResource("ui.fxml"));	
			primaryStage.setTitle("Art gallery problem");
			primaryStage.setScene(new Scene(root, 800, 600));
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch(Exception e) {}
	}
	public static void main(String[] args) {
		launch(args);
	}

}

