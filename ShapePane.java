package physicsdemo;

import java.util.List;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

public class ShapePane extends Pane {
    
    public void setShapes(List<Shape> newShapes) {
        this.getChildren().clear();
        this.getChildren().addAll(newShapes);
    }
}