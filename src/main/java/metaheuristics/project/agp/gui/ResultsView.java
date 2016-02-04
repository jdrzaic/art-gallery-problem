package metaheuristics.project.agp.gui;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class ResultsView {
	
	public void openWindow(int n, String benchmark) {
		System.out.println("opening");
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
        final Image image2 = new Image("file:test_results_and_samples/res.png");
        imv.setImage(image2);
        imv.setRotationAxis(Rotate.Y_AXIS);
        imv.setRotate(180);
        imv.setRotationAxis(Rotate.X_AXIS);
        imv.fitWidthProperty().bind(image2.widthProperty()); 
        imv.setRotate(180);
        final HBox pictureRegion = new HBox();
        
        pictureRegion.getChildren().add(imv);
        gridpane.add(pictureRegion, 1, 1);
        if(n > 0) gridpane.add(new Label("broj kamera: " + n), 1, 2);
        
        root.getChildren().add(gridpane);        
        primaryStage.setScene(scene);
        primaryStage.show();

	}

}
