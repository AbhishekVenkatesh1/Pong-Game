/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physicsdemo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import simulation.Simulation;

public class PhysicsDemo extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Parent root = (Parent)loader.load();
        Scene scene = new Scene(root);
        Simulation sim = new Simulation(300, 250, 2, 2);
        sim.setUpShapes();
        
        FXMLDocumentController controller = loader.getController();
        controller.setSimulation(sim);
        stage.setScene(scene);
        stage.setTitle("Physics Game Server");
        stage.setOnCloseRequest(event->System.exit(0));
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }

}
