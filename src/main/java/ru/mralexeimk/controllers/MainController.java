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
import javafx.scene.control.*;
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
import ru.mralexeimk.models.Rules;
import ru.mralexeimk.others.Point;

import java.util.*;
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
    @FXML
    private CheckBox hashing;
    @FXML
    private ChoiceBox rules;

    private Group group;

    private double step = 0.1;
    private boolean isRunning = false;
    private int R = 5;
    private Map<String, Rules> rule_by_title;

    public void start() {
        isRunning = true;
        boolean doHash = hashing.isSelected();
        SubScene subScene = (SubScene) pane.getChildren().get(0);
        group = (Group) subScene.getRoot();
        if(nodes.getText().isEmpty() || rule.getText().isEmpty()) {
            GraphListener.initGraph("(1,2);(2,3);(3,4);(2,4)", "(x,y);(x,z)->(x,z);(x,w);(y,w);(z,w)", step, doHash);
            start.setText("????????");
            nodes.setText("(1,2);(2,3);(3,4);(2,4)");
            rule.setText("(x,y);(x,z)->(x,z);(x,w);(y,w);(z,w)");
            freq.setText(String.valueOf(step));
            error.setText("?????????????????? ?????????????????????? ????????????????");
        }
        else {
            try {
                GraphListener.initGraph(nodes.getText(), rule.getText(), step, doHash);
                start.setText("????????");
            } catch(Exception e) {
                if(GraphListener.getGraph() != null) {
                    GraphListener.getGraph().stop();
                    GraphListener.getGraph().clear();
                }
                error.setText("???????????? ?????? ?????????????????? ????????????");
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
            start.setText("????????????");
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

    public void initRules() {
        rule_by_title = new HashMap<>();
        for(Rules rule : Rules.values()) {
            rules.getItems().add(rule.getTitle());
            rule_by_title.put(rule.getTitle(), rule);
        }
        rules.setOnAction(e -> {
            Rules rule_obj = rule_by_title.get(rules.getValue().toString());
            nodes.setText(rule_obj.getStart());
            rule.setText(rule_obj.getRule());
            hashing.setSelected(!rule_obj.isRepeat());
        });
    }

    public void initStep() {
        step = 0.1;
        if(!freq.getText().isEmpty()) {
            try {
                step = Double.parseDouble(freq.getText());
            } catch(Exception e) {
                freq.setText("0.1");
            }
        }
    }

    public void initRadius() {
        R = 5;
        if(!radius.getText().isEmpty()) {
            try {
                R = Integer.parseInt(radius.getText());
            } catch(Exception e) {
                radius.setText("5");
            }
        }
    }

    @FXML
    public void initialize() {
        initRules();
    }

    @FXML
    public void buttonStartClick() {
        String text = start.getText();
        initStep();
        initRadius();
        if(text.equals("????????????")) {
            start();
        }
        else if(text.equals("????????")) {
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