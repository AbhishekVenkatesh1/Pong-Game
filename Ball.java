package simulation;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import physics.*;

public class Ball {
    private Ray r;
    private Circle c;
    private int startX;
    private int startY;
    private int dX;
    private int dY;
    
    public Ball(int startX,int startY,int dX,int dY)
    {
        Vector v = new Vector(dX,dY);
        double speed = v.length();
        r = new Ray(new Point(startX,startY),v,speed);
        this.startX = startX;
        this.startY = startY;
        this.dX = dX;
        this.dY = dY;
    }
    
    public Ray getRay()
    {
        return r;
    }
    
    public void setRay(Ray r)
    {
        this.r = r;
    }
    
    public void move(double time)
    {
        r = new Ray(r.endPoint(time),r.v,r.speed);
    }
    
    public Shape getShape()
    {
        c = new Circle(r.origin.x,r.origin.y,4);
        c.setFill(Color.RED);
        return c;
    }
    
    public Point getPoint() {
        return new Point(r.origin.x, r.origin.y);
    }
    
    public void reset() {
        r = new Ray(new Point(startX,startY),r.v,r.speed);
    }
    
    public void updateShape()
    {
        c.setCenterX(r.origin.x);
        c.setCenterY(r.origin.y);
    }
    
    public String toString() {
        return "Ball," + r.origin.x + "," + r.origin.y + "," + dX + "," + dY;
    }
}
