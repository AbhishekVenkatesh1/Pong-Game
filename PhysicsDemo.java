package physicsdemo;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PhysicsDemo extends Application implements constants.GameConstants {
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Parent root = (Parent)loader.load();
           
        Scene scene = new Scene(root);
       
        primaryStage.setScene(scene);
        primaryStage.setTitle("Reservation System");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
