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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Sphere;
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

    private double step = 3;
    private boolean isRunning = false;

    public void start() {
        isRunning = true;
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
            if(list != null) {
                pane.getChildren().clear();
                pane.getChildren().addAll(list);
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
                Sphere sphere = point.getSphere();
                sphere.setMaterial(new PhongMaterial(Color.BLUE));
                list.add(sphere);
                List<Integer> connects = point.getConnects();
                for (int to : connects) {
                    Sphere sphere2 = GraphListener.getGraph().getPoint(to).getSphere();
                    Line line = new Line();
                    line.setStartX(sphere.getTranslateX());
                    line.setStartY(sphere.getTranslateY());
                    line.setEndX(sphere2.getTranslateX());
                    line.setEndY(sphere2.getTranslateY());
                    list.add(line);
                }
            }
        } catch(Exception e) {
            return null;
        }
        return list;
    }

    public void stepUpdate() {
        step = 3;
        if(!freq.getText().isEmpty()) {
            try {
                step = Double.parseDouble(freq.getText());
            } catch(Exception e) {
                freq.setText("3");
            }
        }
    }

    @FXML
    public void buttonStartClick() {
        String text = start.getText();
        stepUpdate();
        if(text.equals("Начать")) {
            start();
        }
        else if(text.equals("Стоп")) {
            stop();
        }
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