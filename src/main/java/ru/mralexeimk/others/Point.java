package ru.mralexeimk.others;

import java.util.ArrayList;
import java.util.List;

public class Point<T extends Number> {
    private T x, y, z;
    private int id;
    private List<Point<T>> connects;
    public Point(T x, T y, T z, int id) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
        connects = new ArrayList<>();
    }
    public void addConnect(Point<T> point) {
        connects.add(point);
    }
    public void removeConnect(Point<T> point) {
        connects.remove(point);
    }
    public void clearConnect() {
        connects.clear();
    }
    public List<Point<T>> getConnects() {
        return connects;
    }
    public Point<T> getConnect(int index) {
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
