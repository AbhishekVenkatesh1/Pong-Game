package physicsdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import javafx.scene.shape.Shape;
import simulation.Ball;
import simulation.Box;

public class GameGateway implements constants.GameConstants {
    private PrintWriter outputToServer;
    private BufferedReader inputFromServer;
    private ArrayList<Shape> allShapes;
    private String handle = "";
    
    public GameGateway() {
        try {
            Socket socket = new Socket("localhost", 8000);

            outputToServer = new PrintWriter(socket.getOutputStream());

            inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public synchronized void sendHandle(String handle) {
        outputToServer.println(handle);
        outputToServer.flush();
    }
    
    public synchronized String getHandle() {
        outputToServer.println(GET_HANDLE);
        outputToServer.flush();
        try {
            handle = inputFromServer.readLine();          
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return handle;
    }
    
    public synchronized String getOtherHandle() {
        try {
            return inputFromServer.readLine();          
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "";
    }
    
    public synchronized void sendReady1(Boolean ready) {
        outputToServer.println(SEND_READY1);
        outputToServer.println(ready);
        outputToServer.flush();
    }
    
    public synchronized boolean getReady1() {
        outputToServer.println(GET_READY1);
        outputToServer.flush();
        Boolean ready = false;
        try {
            ready = Boolean.parseBoolean(inputFromServer.readLine());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return ready;
    }
    
    public synchronized void sendReady2(Boolean ready) {
        outputToServer.println(SEND_READY2);
        outputToServer.println(ready);
        outputToServer.flush();
    }
    
    public synchronized boolean getReady2() {
        outputToServer.println(GET_READY2);
        outputToServer.flush();
        Boolean ready = false;
        try {
            ready = Boolean.parseBoolean(inputFromServer.readLine());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return ready;
    }
    
    public synchronized void sendMove(int code) {
        //System.out.println("GameGateway.sendMove");
        outputToServer.println(SEND_MOVE);
        outputToServer.println(code);
        outputToServer.flush();
        //System.out.println("post flush");
    }
    
    public synchronized ArrayList<Shape> getShapes() {
        outputToServer.println(GET_SHAPES);
        outputToServer.flush();
        allShapes = new ArrayList<Shape>();
        int size = 0;
        try {
            size = Integer.parseInt(inputFromServer.readLine());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            for(int i=0;i<size;i++) {
                String s = inputFromServer.readLine();
                String [] parts = s.split(",");
                if (parts[0].equals("Ball")) {
                    double currentX = Double.parseDouble(parts[1]);
                    double currentY = Double.parseDouble(parts[2]);
                    int dX = Integer.parseInt(parts[3]);
                    int dY = Integer.parseInt(parts[4]);
                    Ball ball = new Ball(currentX,currentY,dX,dY);
                    allShapes.add(ball.getShape());
                } else {
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    int width = Integer.parseInt(parts[3]);
                    int height = Integer.parseInt(parts[4]);
                    String color = parts[5];
                    Box box = new Box(x,y,width,height,true,color);
                    allShapes.add(box.getShape());
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return allShapes;
    }
    
    public synchronized int getPlayer() {
        int player = 0;
        try {
            player = Integer.parseInt(inputFromServer.readLine());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return player;
    }
    
    public synchronized int getP1Score() {
        outputToServer.println(P1_Score);
        outputToServer.flush();
        int count = 0;
        try {
            count = Integer.parseInt(inputFromServer.readLine());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return count;
    }
    
    public synchronized int getP2Score() {
        outputToServer.println(P2_Score);
        outputToServer.flush();
        int count = 0;
        try {
            count = Integer.parseInt(inputFromServer.readLine());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return count;
    }
    
    public synchronized void sendRestart() {
        //System.out.println("?????");
        outputToServer.println(SEND_RESTART);
        outputToServer.flush();
        //System.out.println("ok");
    }
}