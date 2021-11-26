package ru.mralexeimk.others;

import javafx.scene.shape.Sphere;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Point<T extends Number> {
    private T x, y, z;
    private int id;
    private List<Integer> connects;
    private boolean isDefined;
    public Point(T x, T y, T z, int id) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
        connects = new ArrayList<>();
        isDefined = true;
    }
    public Point(int id) {
        this.id = id;
        connects = new ArrayList<>();
        isDefined = false;
    }
    public void setPoint(T x, T y, T z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public boolean isDefined() {
        return isDefined;
    }
    public void define() {
        isDefined = true;
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
