package ru.mralexeimk.others;

import javafx.scene.shape.Sphere;

import java.util.ArrayList;
import java.util.List;

public class Point<T extends Number> {
    private T x, y, z;
    private int id;
    private Sphere sphere;
    private List<Integer> connects;
    public Point(T x, T y, T z, int id) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
        sphere = new Sphere(10);
        sphere.setTranslateX((double)x);
        sphere.setTranslateY((double)y);
        connects = new ArrayList<>();
    }
    public void addConnect(int c) {
        connects.add(c);
    }
    public void removeConnect(int c) {
        connects.remove(c);
    }
    public void clearConnects() {
        connects.clear();
    }
    public List<Integer> getConnects() {
        return connects;
    }
    public int getConnect(int index) {
        return connects.get(index);
    }
    public int getId() {
        return id;
    }
    public Sphere getSphere() { return sphere; }
    public T getX() {
        return x;
    }
    public T getY() {
        return y;
    }
    public T getZ() {
        return z;
    }
    public void setX(T x) {
        this.x = x;
    }
    public void setY(T y) {
        this.y = y;
    }
    public void setZ(T z) {
        this.z = z;
    }
}
