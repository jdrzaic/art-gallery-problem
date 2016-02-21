package metaheuristics.project.agp.gui;

import java.io.File;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ResultsView {
	
	public void openWindow(int n, String benchmark, int vertices, int holes, String algorithm) {

		System.out.println("opening");
		Stage primaryStage  = new Stage();		
		primaryStage.setResizable(true);
		primaryStage.setTitle("Rezultat - "+ benchmark);
        Group root = new Group();
        Scene scene = new Scene(root, 1000, 500, Color.WHITE);
        File f = new File("css/resultview.css");
        scene.getStylesheets().clear();
        scene.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));
        final ImageView imv = new ImageView();
        final Image image2 = new Image("file:test_results_and_samples/res.png");
        imv.setImage(image2);
        imv.setRotationAxis(Rotate.Y_AXIS);
        imv.setRotate(180);
        imv.setRotationAxis(Rotate.X_AXIS);
        imv.fitWidthProperty().bind(image2.widthProperty()); 
        imv.setRotate(180);
        final BorderPane pictureRegion = new BorderPane();
        final VBox info = new VBox(20);
        info.getStyleClass().add("vbox");
        imv.getStyleClass().add("image");
        Label title = new Label("Podaci o galeriji");
        title.getStyleClass().add("infotitle");
        Label verticesN = new Label("Broj vrhova       " + vertices);
        Label holesN = new Label("Broj rupa            " + holes);
        Label algorithmN = new Label("Algoritam          " + algorithm);
        Label cameraN = new Label("Broj kamera       " + n);
        info.getChildren().addAll(title, verticesN, holesN, algorithmN, cameraN);
        pictureRegion.setCenter(imv);
        pictureRegion.setRight(info);
        BorderPane.setMargin(imv, new Insets(30));
        BorderPane.setMargin(info, new Insets(30));
        HBox.setHgrow(imv, Priority.ALWAYS);        
        root.getChildren().add(pictureRegion);
        primaryStage.setScene(scene);
        primaryStage.setHeight(500);
        primaryStage.setWidth(imv.getFitWidth() + 350);
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.show();

	}

}
