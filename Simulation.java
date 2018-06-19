package simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import physics.*;

public class Simulation {

    private Box outer;
    private Ball ball;
    public Box inner;
    public Box inner1;
    private Box goal1;
    private Box goal2;
    private Lock lock;
    public int p1Score;
    public int p2Score;

    public Simulation(int width, int height, int dX, int dY) {
        int goalWidth = 80;
        int paddleWidth = 40;

        outer = new Box(0, 0, width, height, false, "White");
        ball = new Ball(width / 2, height / 2, dX, dY);
        inner = new Box((width - paddleWidth) / 2, height - 60, paddleWidth, 20, true, "Green");
        inner1 = new Box((width - paddleWidth) / 2, 40, paddleWidth, 20, true, "Blue");
        goal1 = new Box((width - goalWidth) / 2, 0, goalWidth, 20, true, "Blue");
        goal2 = new Box((width - goalWidth) / 2, height - 20, goalWidth, 20, true, "Green");
        lock = new ReentrantLock();
    }

    public void evolve(double time) {
        lock.lock();

        Ray newLoc = goal1.bounceRay(ball.getRay(), time);
        if (newLoc != null) {
            if (p1Score < 5 && p2Score < 5) {
                p1Score++;
            }
            ball.setRay(newLoc);
        } else {
            newLoc = goal2.bounceRay(ball.getRay(), time);
            if (newLoc != null) {
                if (p1Score < 5 && p2Score < 5) {
                    p2Score++;
                }
                ball.setRay(newLoc);
            } else {

                newLoc = inner.bounceRay(ball.getRay(), time);
                Ray newLoc1 = inner1.bounceRay(ball.getRay(), time);

                if (newLoc != null) {
                    ball.setRay(newLoc);
                } else if (newLoc1 != null) {
                    ball.setRay(newLoc1);
                } else {
                    newLoc = outer.bounceRay(ball.getRay(), time);
                    if (newLoc != null) {
                        ball.setRay(newLoc);
                    } else {
                        ball.move(time);
                    }
                }
            }

        }
        lock.unlock();
    }

    public void moveInner(Box box, int deltaX, int deltaY) {
        lock.lock();
        int dX = deltaX;
        int dY = deltaY;
        if (box.x + deltaX < 0) {
            dX = -box.x;
        }
        if (box.x + box.width + deltaX > outer.width) {
            dX = outer.width - box.width - box.x;
        }

        if (box.y + deltaY < 0) {
            dY = -box.y;
        }
        if (box.y + box.height + deltaY > outer.height) {
            dY = outer.height - box.height - box.y;
        }

        box.move(dX, dY);
        if (box.contains(ball.getRay().origin)) {
            // If we have discovered that the box has just jumped on top of
            // the ball, we nudge them apart until the box no longer
            // contains the ball.
            int bumpX = -1;
            if (dX < 0) {
                bumpX = 1;
            }
            int bumpY = -1;
            if (dY < 0) {
                bumpY = 1;
            }
            do {
                box.move(bumpX, bumpY);
                ball.getRay().origin.x += -bumpX;
                ball.getRay().origin.y += -bumpY;
            } while (box.contains(ball.getRay().origin));
        }
        lock.unlock();
    }

    public List<Shape> setUpShapes() {
        ArrayList<Shape> newShapes = new ArrayList<Shape>();
        newShapes.add(outer.getShape());

        Shape p1goal = goal1.getShape();
        p1goal.setFill(Color.BLUE);
        p1goal.setStroke(Color.BLUE);
        newShapes.add(p1goal);

        Shape p2goal = goal2.getShape();
        p2goal.setFill(Color.GREEN);
        p2goal.setStroke(Color.GREEN);
        newShapes.add(p2goal);

        Shape p1paddle = inner.getShape();
        p1paddle.setFill(Color.GREEN);
        p1paddle.setStroke(Color.GREEN);
        newShapes.add(p1paddle);

        Shape p2paddle = inner1.getShape();
        p1paddle.setFill(Color.BLUE);
        p1paddle.setStroke(Color.BLUE);
        newShapes.add(p2paddle);

        newShapes.add(ball.getShape());

        return newShapes;
    }

    public ArrayList<String> setUpShapesStrings() {
        ArrayList<String> shapesStrings = new ArrayList<String>();
        shapesStrings.add(outer.toString());
        shapesStrings.add(goal1.toString());
        shapesStrings.add(goal2.toString());
        shapesStrings.add(inner.toString());
        shapesStrings.add(inner1.toString());
        shapesStrings.add(ball.toString());
        return shapesStrings;
    }

    public void updateShapes() {
        inner.updateShape();
        inner1.updateShape();
        ball.updateShape();
    }

    public void reset() {
        //System.out.println("1");
        lock.lock();
        //System.out.println("2");
        p1Score = 0;
        //System.out.println("3");
        p2Score = 0;
        //System.out.println("4");
        ball.reset();
        //System.out.println("5");
        inner.resetPaddle1();
        //System.out.println("6");
        inner1.resetPaddle2();
        //System.out.println("7");
        lock.unlock();
        //System.out.println("8");
    }

    public Box getBox1() {
        return inner;
    }

    public Box getBox2() {
        return inner1;
    }

    public Box getGoal1() {
        return goal1;
    }

    public Box getGoal2() {
        return goal2;
    }
}
