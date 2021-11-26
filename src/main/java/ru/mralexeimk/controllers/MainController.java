package ru.mralexeimk.controllers;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.css.Style;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import ru.mralexeimk.MainApplication;
import ru.mralexeimk.models.Graph;
import ru.mralexeimk.models.GraphListener;
import ru.mralexeimk.others.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MainController {
    @FXML
    private Button start;
    @FXML
    private Label error;
    @FXML
    private TextField nodes, rule, freq;
    @FXML
    private AnchorPane pane;
    @FXML
    private TextField radius;
    @FXML
    private CheckBox showConnects;

    private Group group;

    private double step = 3;
    private boolean isRunning = false;
    private int R = 20;

    public void start() {
        isRunning = true;
        SubScene subScene = (SubScene) pane.getChildren().get(0);
        group = (Group) subScene.getRoot();
        if(nodes.getText().isEmpty() || rule.getText().isEmpty()) {
            GraphListener.initGraph("(1,2);(2,3);(3,4);(2,4)", "(x,y);(x,z)->(x,z);(x,w);(y,w);(z,w)", step);
            start.setText("Стоп");
            nodes.setText("(1,2);(2,3);(3,4);(2,4)");
            rule.setText("(x,y);(x,z)->(x,z);(x,w);(y,w);(z,w)");
            freq.setText(String.valueOf(step));
            error.setText("Загружены стандартные значения");
        }
        else {
            try {
                GraphListener.initGraph(nodes.getText(), rule.getText(), step);
                start.setText("Стоп");
            } catch(Exception e) {
                if(GraphListener.getGraph() != null) {
                    GraphListener.getGraph().stop();
                    GraphListener.getGraph().clear();
                }
                error.setText("Ошибка при внедрении данных");
            }
        }

        TimerService service = new TimerService();
        service.setPeriod(Duration.seconds(step));
        service.setOnSucceeded(t -> {
            if(!isRunning) service.cancel();
            List<Node> list = graphUpdate();
            initRadius();
            if(list != null) {
                for(int i = group.getChildren().size()-1; i > 0; --i) {
                    group.getChildren().remove(i);
                }
                group.getChildren().addAll(list);
            }
        });
        service.start();
    }

    public void stop() {
        isRunning = false;
        if(GraphListener.getGraph() != null) {
            GraphListener.getGraph().stop();
            start.setText("Начать");
            error.setText("");
            start.setDisable(true);
            double finalStep = step;
            Thread th2 = new Thread(() -> {
                try {
                    Thread.sleep((long) (1000 * finalStep));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (start != null) start.setDisable(false);
            });
            th2.start();
        }
    }

    public List<Node> graphUpdate() {
        List<Node> list = new ArrayList<>();
        Set<Integer> nodes = GraphListener.getGraph().getKeys();
        try {
            for (int node : nodes) {
                Point<Double> point = GraphListener.getGraph().getPoint(node);
                Sphere sphere = new Sphere(R);
                sphere.setTranslateX(point.getX());
                sphere.setTranslateY(point.getY());
                sphere.setTranslateZ(point.getZ());
                sphere.setMaterial(new PhongMaterial(Color.BLUE));
                list.add(sphere);
            }
            if(showConnects.isSelected()) {
                for (int node : nodes) {
                    Point<Double> point = GraphListener.getGraph().getPoint(node);
                    List<Integer> connects = point.getConnects();
                    for (int to : connects) {
                        Point<Double> point2 = GraphListener.getGraph().getPoint(to);
                        Cylinder line = createConnection(point, point2);
                        list.add(line);
                    }
                }
            }
        } catch(Exception e) {
            return null;
        }
        return list;
    }

    public void initStep() {
        step = 3;
        if(!freq.getText().isEmpty()) {
            try {
                step = Double.parseDouble(freq.getText());
            } catch(Exception e) {
                freq.setText("3");
            }
        }
    }

    public void initRadius() {
        R = 20;
        if(!radius.getText().isEmpty()) {
            try {
                R = Integer.parseInt(radius.getText());
            } catch(Exception e) {
                radius.setText("20");
            }
        }
    }

    @FXML
    public void buttonStartClick() {
        String text = start.getText();
        initStep();
        initRadius();
        if(text.equals("Начать")) {
            start();
        }
        else if(text.equals("Стоп")) {
            stop();
        }
    }

    public Cylinder createConnection(Point<Double> from, Point<Double> to) {
        Point3D origin = new Point3D(from.getX(), from.getY(), from.getZ());
        Point3D target = new Point3D(to.getX(), to.getY(), to.getZ());
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(origin);

        double height = diff.magnitude();

        Point3D mid = target.midpoint(origin);
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

        Cylinder line = new Cylinder(1, height);

        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);

        return line;
    }

    private static class TimerService extends ScheduledService<Boolean> {
        protected Task<Boolean> createTask() {
            return new Task<>() {
                protected Boolean call() {
                    return null;
                }
            };
        }
    }
}