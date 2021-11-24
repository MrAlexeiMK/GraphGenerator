package ru.mralexeimk.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import ru.mralexeimk.models.Graph;

public class MainController {
    @FXML
    private Button start;
    @FXML
    private Label error;
    @FXML
    private TextField nodes, rule, freq;

    private Graph g;

    @FXML
    public void buttonStartClick() {
        String text = start.getText();
        double step = 3;
        if(!freq.getText().isEmpty()) {
            try {
                step = Double.valueOf(freq.getText());
            } catch(Exception e) {
                freq.setText("3");
            }
        }
        if(text.equals("Начать")) {
            if(nodes.getText().isEmpty() || rule.getText().isEmpty()) {
                g = new Graph("(1,2);(2,3);(3,4);(2,4)", "(x,y);(x,z)->(x,z);(x,w);(y,w);(z,w)", step);
                g.print(g.getPoints());
                Thread th = new Thread(g);
                th.start();
                start.setText("Стоп");
                nodes.setText("(1,2);(2,3);(3,4);(2,4)");
                rule.setText("(x,y);(x,z)->(x,z);(x,w);(y,w);(z,w)");
                freq.setText(String.valueOf(step));
                error.setText("Загружены стандартные значения");
            }
            else {
                try {
                    g = new Graph(nodes.getText(), rule.getText(), step);
                    g.print(g.getPoints());
                    Thread th = new Thread(g);
                    th.start();
                    start.setText("Стоп");
                } catch(Exception e) {
                    if(g != null) {
                        g.stop();
                        g.clear();
                    }
                    error.setText("Ошибка при внедрении данных");
                }
            }
        }
        else if(text.equals("Стоп")) {
            g.stop();
            start.setText("Начать");
            error.setText("");
            start.setDisable(true);
            double finalStep = step;
            Thread th2 = new Thread(() -> {
                try {
                    Thread.sleep((long)(1000* finalStep));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(start != null) start.setDisable(false);
            });
            th2.start();
        }
    }
}