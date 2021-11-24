package ru.mralexeimk.models;

import ru.mralexeimk.others.Point;
import ru.mralexeimk.others.RulesParser;

import java.util.List;

public class Graph implements Runnable {
    private List<Point<Double>> points;
    private RulesParser parser;
    private static int STEP = 5;
    private boolean isRunning;
    //rules examples:
    //"{{x, y}, {x, z}} -> {{x, z}, {x, w}, {y, w}, {z, w}, ...}"
    public Graph(String input) {
        parser = new RulesParser(input);
        points = parser.parse();
        isRunning = false;
    }

    public void run() {
        isRunning = true;
        while(isRunning) {
            try {
                Thread.sleep(STEP*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            parser.simulate(points);
        }
    }

    public void stop() {
        isRunning = false;
    }

    public List<Point<Double>> getPoints() {
        return points;
    }

    public Point<Double> getPoint(int index) {
        return points.get(index);
    }

    public long getPointsCount() {
        return getPoints().size();
    }

    public <T> void addPoint(Point<T> point) {
        points.add((Point<Double>) point);
    }
}
