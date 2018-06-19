package physicsdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import simulation.Box;
import simulation.Simulation;

public class FXMLDocumentController implements Initializable,constants.GameConstants {

    @FXML
    private TextArea textArea;
    
    private int playerNumber1 = 1;
    
    private int playerNumber2 = 2;
    
    private int sessionNumber = 1;
    
    private Simulation sim;
    
    private Box paddle1;
    
    private Box paddle2;
    
    public void setSimulation(Simulation sim) {
        this.sim = sim;
        paddle1 = sim.getBox1();
        paddle2 = sim.getBox2();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        new Thread( () -> {
            try {
                ServerSocket serverSocket = new ServerSocket(8000);
        
                while (true) {
                    Platform.runLater( () -> {
                        textArea.appendText(new Date() + ": Waiting for two players to join session "
                            + sessionNumber + "\n");
                    });

                    Socket player1 = serverSocket.accept();

                    Platform.runLater( () -> {
                        textArea.appendText(new Date() + ": Player " + playerNumber1
                            + " has joined session " + sessionNumber + "\n");
                    });
                    
                    Platform.runLater( () -> {
                        textArea.appendText(new Date() + ": Waiting for player " 
                            + playerNumber2 + " to join" + "\n");
                    });

                    PrintWriter outputToPlayer1 = new PrintWriter(player1.getOutputStream());
                    outputToPlayer1.println(PLAYER1);
                    outputToPlayer1.flush();
                    
                    BufferedReader inputFromPlayer1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
                    String p1handle = inputFromPlayer1.readLine();

                    Socket player2 = serverSocket.accept();

                    Platform.runLater( () -> {
                        textArea.appendText(new Date() + ": Player " + playerNumber2
                            + " has joined session " + sessionNumber + "\n");
                    });

                    PrintWriter outputToPlayer2 = new PrintWriter(player2.getOutputStream());
                    outputToPlayer2.println(PLAYER2);
                    outputToPlayer2.flush();
                    
                    BufferedReader inputFromPlayer2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));
                    String p2handle = inputFromPlayer2.readLine();
                    
                    outputToPlayer1.println(p2handle);
                    outputToPlayer1.flush();
                    outputToPlayer2.println(p1handle);
                    outputToPlayer2.flush();
                    
                    Platform.runLater( () -> {
                        textArea.appendText(new Date() + ": Starting session " + sessionNumber + "\n");
                        sessionNumber++;
                    });

                    new Thread(new HandleASession(player1,textArea,sim,paddle1)).start();
                    new Thread(new HandleASession(player2,textArea,sim,paddle2)).start();
                    // This is the main animation thread
                    new Thread(() -> {
                        while (true) {
                            sim.evolve(1.0);
                            Platform.runLater(()->sim.updateShapes());
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
            catch(IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }     
}

class HandleASession implements Runnable,constants.GameConstants {
    private Socket player;
    private TextArea textArea;
    private Simulation simulation;
    private Box paddle;
    private String handle;
    private static boolean player1ready;
    private static boolean player2ready;
    
    public HandleASession(Socket player, TextArea textArea, Simulation simulation, Box paddle) {
        this.player = player;
        this.textArea = textArea;
        this.simulation = simulation;
        this.paddle = paddle;
    }
    
    public void run() {
        try {
            BufferedReader inputFromPlayer = new BufferedReader(new InputStreamReader(player.getInputStream()));
            PrintWriter outputToPlayer = new PrintWriter(player.getOutputStream());
            
            while(true) {
                int request = Integer.parseInt(inputFromPlayer.readLine());
                
                switch(request) {
                    case SEND_HANDLE: {
                        handle = inputFromPlayer.readLine();
                        break;
                    }
                    case GET_HANDLE: {
                        outputToPlayer.println(handle);
                        outputToPlayer.flush();
                        break;
                    }
                    case SEND_READY1: {
                        player1ready = Boolean.parseBoolean(inputFromPlayer.readLine());
                        break;
                    }
                    case GET_READY1: {
                        outputToPlayer.println(player1ready);
                        outputToPlayer.flush();
                        break;
                    }
                    case SEND_READY2: {
                        player2ready = Boolean.parseBoolean(inputFromPlayer.readLine());
                        break;
                    }
                    case GET_READY2: {
                        outputToPlayer.println(player2ready);
                        outputToPlayer.flush();
                        break;
                    }
                    case SEND_MOVE: {
                        int code = Integer.parseInt(inputFromPlayer.readLine());
                        System.out.println("Moving: " + code);
                        switch(code) {
                            case DOWN: {
                                simulation.moveInner(paddle,0, 3);
                                break;
                            }
                            case UP: {
                                simulation.moveInner(paddle,0, -3);
                                break;
                            }
                            case LEFT: {
                                simulation.moveInner(paddle,-3, 0);
                                break;
                            }
                            case RIGHT: {
                                simulation.moveInner(paddle,3, 0);
                                break;
                            }
                        }
                        break;
                    }
                    case GET_SHAPES: {
                        simulation.updateShapes();
                        ArrayList<String> shapeStrings = simulation.setUpShapesStrings();
                        outputToPlayer.println(shapeStrings.size());
                        for(String s: shapeStrings) {
                            outputToPlayer.println(s);
                        }
                        outputToPlayer.flush();
                        break;
                    }
                    case P1_Score: {
                        outputToPlayer.println(simulation.p1Score);
                        outputToPlayer.flush();
                        break;
                    }
                    case P2_Score: {
                        outputToPlayer.println(simulation.p2Score);
                        outputToPlayer.flush();
                        break;
                    }
                    case SEND_RESTART: {
                        System.out.println("Resetting...");
                        simulation.reset();
                        System.out.println("Reset done");
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            Platform.runLater(()->textArea.appendText("Exception in client thread: "+ex.toString()+"\n"));
        }
    }
}
