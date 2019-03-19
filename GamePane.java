package physicsdemo;

import java.util.List;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

public class GamePane extends Pane {

    public Text p1Score;
    public Text p2Score;
    public Text winner;
    public Button restart;
    private GameGateway gateway;
    private ShapePane shapePane;

    public GamePane() {
        p1Score = new Text(20, 50, "Player 1: Score: 0");
        p2Score = new Text(20, 200, "Player 2: Score: 0");
        winner = new Text(80, 125, "");
        restart = new Button("Restart");
        restart.setLayoutX(150);
        restart.setLayoutY(125);
        restart.setVisible(false);
        restart.setOnAction((evt) -> {
            gateway.sendRestart();
        });
        shapePane = new ShapePane();
        this.getChildren().addAll(shapePane, p1Score, p2Score, winner, restart);
    }

    public void setGateway(GameGateway gateway) {
        this.gateway = gateway;
    }

    public void setShapes(List<Shape> newShapes) {
        shapePane.setShapes(newShapes);

    }

    public void setScores(int score1, int score2) {
        p1Score.setText("Player 1: Score: " + score1);
        p2Score.setText("Player 2: Score: " + score2);
        if (score1 == 5) {
            winner.setText("Player 1 has won the game!");
            restart.setVisible(true);
        } else if (score2 == 5) {
            winner.setText("Player 2 has won the game!");
            restart.setVisible(true);
        } else {
            winner.setText("");
            restart.setVisible(false);
        }
    }

}
