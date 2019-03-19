package physicsdemo;

import static constants.GameConstants.DOWN;
import static constants.GameConstants.LEFT;
import static constants.GameConstants.RIGHT;
import static constants.GameConstants.UP;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

public class FXMLDocumentController implements Initializable, constants.GameConstants {
    private GameGateway gateway;
    
    private int player;
    
    private String handle;
    
    @FXML
    private Label title;
    
    @FXML
    private Label player1handle;
    
    @FXML
    private Label player2handle;
    
    @FXML
    private Label player1ready;
    
    @FXML
    private Label player2ready;
    
    @FXML
    private Label waiting;
    
    @FXML
    private Label bothReady;
    
    @FXML
    private void clickReady(ActionEvent event) {
        if (player == PLAYER1) {
            gateway.sendReady1(true);
            player1ready.setText(handle + " is ready to play!");
        } else if (player == PLAYER2) {
            gateway.sendReady2(true);
            player2ready.setText(handle + " is ready to play!");
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gateway = new GameGateway();
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Start Game");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter a handle:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            gateway.sendHandle(name);
            handle = name;
        });
        title.setText("Physics Pong");

        new Thread(() -> {
            player = gateway.getPlayer();
            
            Platform.runLater(()-> {
                if (player == PLAYER1) {
                    player1handle.setText("Player 1: " + handle);
                } else if (player == PLAYER2) {
                    player2handle.setText("Player 2: " + handle);
                }
            });
            
            String otherPlayer = gateway.getOtherHandle();
            
            Platform.runLater(()-> {
                if (player == PLAYER1) {
                    player2handle.setText("Player 2: " + otherPlayer);
                } else if (player == PLAYER2) {
                    player1handle.setText("Player 1: " + otherPlayer);
                }
            });
            new Thread(new ReadyCheck(gateway,player,player1ready,player2ready,waiting,bothReady)).start();
        }).start();
    }    
}

class ReadyCheck implements Runnable, constants.GameConstants {
    private GameGateway gateway;
    private int player;
    private Label player1ready;
    private Label player2ready;
    private Label waiting;
    private Label bothReady;
    private boolean ready1 = false;
    private boolean ready2 = false;
    
    public ReadyCheck(GameGateway gateway, int player, Label player1ready, Label player2ready, Label waiting, Label bothReady) {
        this.gateway = gateway;
        this.player = player;
        this.player1ready = player1ready;
        this.player2ready = player2ready;
        this.waiting = waiting;
        this.bothReady = bothReady;
    }
    
    public void run() {
        while(!ready1 || !ready2) {
            if (player == PLAYER1) {
                ready1 = gateway.getReady1();
                ready2 = gateway.getReady2();
                if (ready2 == true) {
                    Platform.runLater( () -> {
                    player2ready.setText("Player 2 is ready to play!");
                    });
                }
                if (ready1 == false && ready2 == true) {
                    Platform.runLater( () -> {
                    waiting.setText("Waiting for player 1 to ready up...");
                    });
                } else if (ready1 == true && ready2 == false) {
                    Platform.runLater( () -> {
                    waiting.setText("Waiting for player 2 to ready up...");
                    });
                }
            } else if (player == PLAYER2) {
                ready1 = gateway.getReady1();
                ready2 = gateway.getReady2();
                if (ready1 == true) {
                    Platform.runLater( () -> {
                    player1ready.setText("Player 1 is ready to play!");
                    });
                }
                if (ready1 == false && ready2 == true) {
                    Platform.runLater( () -> {
                    waiting.setText("Waiting for player 1 to ready up...");
                    });
                } else if (ready1 == true && ready2 == false) {
                    Platform.runLater( () -> {
                    waiting.setText("Waiting for player 2 to ready up...");
                    });
                }
            }
        }
        Platform.runLater( () -> {
            bothReady.setText("Both players are ready! Game starting in 5 seconds");
        });
        try {
            Thread.sleep(5*1000); // Sleep for 5 seconds
        } catch (InterruptedException ex) { }
        Platform.runLater(() -> {
            GamePane root = new GamePane();
            Scene scene = new Scene(root, 300, 250);
            root.setOnKeyPressed(e -> {
                switch (e.getCode()) {
                    case DOWN: {
                        System.out.println("down");
                        gateway.sendMove(DOWN);
                        break;
                    }
                    case UP: {
                        System.out.println("up");
                        gateway.sendMove(UP);
                        break;
                    }
                    case LEFT: {
                        System.out.println("left");
                        gateway.sendMove(LEFT);
                        break;
                    }
                    case RIGHT: {
                        System.out.println("right");
                        gateway.sendMove(RIGHT);
                        break;
                    }
                }   
            });
            root.requestFocus(); 

            Stage stage = new Stage();
            stage.setTitle("Game Physics");
            stage.setScene(scene);
            stage.setOnCloseRequest((event)->System.exit(0));
            stage.show();

            // This is the main animation thread
            new Thread(() -> {
                while (true) {
                    root.setGateway(gateway);
                    ArrayList<Shape> shapes = gateway.getShapes();
                    int P1Score = gateway.getP1Score();
                    int P2Score = gateway.getP2Score();
                    Platform.runLater(()-> {
                        root.setShapes(shapes);
                        root.setScores(P1Score,P2Score);
                    });

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {

                    }
                }
            }).start();
        });
    }
}
