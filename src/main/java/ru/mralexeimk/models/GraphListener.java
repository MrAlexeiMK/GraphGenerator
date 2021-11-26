package ru.mralexeimk.models;

import ru.mralexeimk.others.Point;

public class GraphListener {
    private static Graph g;

    public static Graph getGraph() {
        return g;
    }

    public static void initGraph() {
        initGraph("(1,2);(2,3);(3,4);(2,4)", "(x,y);(x,z)->(x,z);(x,w);(y,w);(z,w)", 3);
    }

    public static void initGraph(double step) {
        initGraph("(1,2);(2,3);(3,4);(2,4)", "(x,y);(x,z)->(x,z);(x,w);(y,w);(z,w)", step);
    }

    public static void initGraph(String start, String rule) {
        initGraph(start, rule, 3);
    }

    public static void initGraph(String nodes, String rule, double step) {
        try {
            if(g != null) g.clear();
            g = new Graph(nodes, rule, step);
            Thread th = new Thread(g);
            th.start();
        } catch(Exception e) {}
    }
}
