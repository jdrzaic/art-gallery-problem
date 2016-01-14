package metaheuristics.project.agp.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ResultsView {
	
	public void openWindow(int n, String benchmark) {
		
		Stage primaryStage  = new Stage();
		primaryStage.setWidth(700);
		primaryStage.setHeight(500);
		primaryStage.setTitle("Rezultat-"+ benchmark);
        Group root = new Group();
        Scene scene = new Scene(root, 700, 500, Color.WHITE);
        
        GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(5));
        gridpane.setHgap(10);
        gridpane.setVgap(10);
        
        final ImageView imv = new ImageView();
        final Image image2 = new Image("file:cam.png");
        imv.setImage(image2);

        final HBox pictureRegion = new HBox();
        
        pictureRegion.getChildren().add(imv);
        gridpane.add(pictureRegion, 1, 1);
        gridpane.add(new Label("broj kamera: " + n), 1, 2);
        
        root.getChildren().add(gridpane);        
        primaryStage.setScene(scene);
        primaryStage.show();

	}

}
