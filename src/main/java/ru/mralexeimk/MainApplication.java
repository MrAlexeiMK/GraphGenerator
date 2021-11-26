package ru.mralexeimk;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Line;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;
import ru.mralexeimk.models.Graph;
import ru.mralexeimk.models.GraphListener;
import ru.mralexeimk.others.Pair;
import ru.mralexeimk.others.Point;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MainApplication extends Application {
    private double startX = 0, startY = 0;
    @Override
    public void start(Stage stage) throws IOException {
        VBox root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
        AnchorPane pane = (AnchorPane) root.getChildren().get(0);
        stage.setTitle("Graph modeling");

        Translate pivot = new Translate();
        Rotate yRotate = new Rotate(0, Rotate.Y_AXIS);
        PerspectiveCamera camera = new PerspectiveCamera(false);
        camera.getTransforms().addAll (
                pivot,
                yRotate
        );
        //camera.setTranslateZ(10000);

        Group group = new Group();
        group.getChildren().add(camera);;

        SubScene subScene = new SubScene(
                group,
                pane.getPrefWidth(), pane.getPrefHeight(),
                true,
                SceneAntialiasing.BALANCED
        );
        subScene.setFill(Color.ALICEBLUE);
        subScene.setCamera(camera);
        pane.getChildren().add(subScene);
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
        pane.setOnMousePressed(mouseEvent -> {
            startX = camera.getLayoutX() - mouseEvent.getSceneX();
            startY = camera.getLayoutY() - mouseEvent.getSceneY();
            pane.setCursor(Cursor.MOVE);
        });
        pane.setOnMouseReleased(mouseEvent -> pane.setCursor(Cursor.HAND));
        pane.setOnMouseDragged(mouseEvent -> {
            camera.setLayoutX(mouseEvent.getSceneX() + startX);
            camera.setLayoutY(mouseEvent.getSceneY() + startY);
        });
        pane.setOnMouseEntered(mouseEvent -> pane.setCursor(Cursor.HAND));
        pane.setOnScroll(e -> {
            int add = ((int)(Math.abs(camera.getTranslateZ())/500) + 1) * 250;
            if (e.getDeltaY() > 0) {
                camera.setTranslateZ(camera.getTranslateZ() + add);
            } else {
                camera.setTranslateZ(camera.getTranslateZ() - add);
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}