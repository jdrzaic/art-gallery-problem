package metaheuristics.project.agp.gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Main extends Application{
	
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("ui.fxml"));	
			primaryStage.setTitle("gui");
			primaryStage.setScene(new Scene(root, 800, 600));
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	 
	public static void main(String[] args) {
		launch(args);
	}

}

