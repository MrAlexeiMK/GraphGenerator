package ru.mralexeimk.models;

import ru.mralexeimk.others.Pair;
import ru.mralexeimk.others.Point;

import java.util.*;

//input examples:
//start: (1,2);(2,3);(3,4);(2,4)
//rule: "(x,y);(x,z)->(x,z);(x,w);(y,w);(z,w)"

public class Graph implements Runnable {
    private List<Pair<Integer>> points;
    private List<Pair<String>> input_rule, output_rule;
    private Map<Integer, Point<Double>> point_by_id;
    private int last_id;
    private int step;
    private boolean isRunning;
    public Graph(String start, String rule, int step) {
        this.step = step;
        points = new ArrayList<>();
        input_rule = new ArrayList<>();
        output_rule = new ArrayList<>();
        point_by_id = new HashMap<>();
        isRunning = false;
        last_id = 0;

        String[] spl = start.split(";");
        for(String p : spl) {
            Pair<Integer> pair = new Pair<>(p, Integer.class);
            Point<Double> point1 = new Point<>(new Random().nextDouble()*10, new Random().nextDouble()*10,
                    new Random().nextDouble()*10, pair.getFirst());
            Point<Double> point2 = new Point<>(new Random().nextDouble()*10, new Random().nextDouble()*10,
                    new Random().nextDouble()*10, pair.getSecond());
            point_by_id.put(pair.getFirst(), point1);
            point_by_id.put(pair.getSecond(), point2);
            last_id = Math.max(pair.getFirst(), pair.getSecond());
            points.add(pair);
        }
        String input = rule.split("->")[0];
        String output = rule.split("->")[1];
        spl = input.split(";");
        for(String p : spl) {
            Pair<String> pair = new Pair<>(p, String.class);
            input_rule.add(pair);
        }
        spl = output.split(";");
        for(String p : spl) {
            Pair<String> pair = new Pair<>(p, String.class);
            output_rule.add(pair);
        }
    }

    public int getLastId() {
        return last_id;
    }

    @Override
    public void run() {
        isRunning = true;
        while(isRunning) {
            try {
                Thread.sleep(step*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            doStep();
            print(getPoints());
        }
    }

    public <T> void print(List<T> list) {
        for(T pair : list) {
            System.out.print(pair.toString()+";");
        }
        System.out.println();
    }

    public <T> void out(T msg) {
        System.out.println(msg);
    }

    public class Twice<T, V> {
        private T first;
        private V second;
        public Twice(T first, V second) {
            this.first = first;
            this.second = second;
        }

        public T getFirst() {
            return first;
        }

        public void setFirst(T first) {
            this.first = first;
        }

        public V getSecond() {
            return second;
        }

        public void setSecond(V second) {
            this.second = second;
        }
    }

    //замена вложенных циклов
    public Twice<List<Integer>, Map<String, Integer>> loop(List<Integer> list, int c) {
        if(c >= getInputRuleCount())  {
            Set<Integer> set = new HashSet<>(list);
            if(set.size() != getInputRuleCount()) return null;
            //print(list);
            Map<String, Integer> check = new HashMap<>();
            for(int i = 0; i < getInputRuleCount(); ++i) {
                String left = getInputRuleIndex(i).getFirst();
                int left_node = getConnectedPoint(list.get(i)).getFirst();
                if(check.containsKey(left)) {
                    if(check.get(left) != left_node) return null;
                }
                else check.put(left, left_node);

                String right = getInputRuleIndex(i).getSecond();
                int right_node = getConnectedPoint(list.get(i)).getSecond();
                if(check.containsKey(right)) {
                    if(check.get(right) != right_node) return null;
                }
                else check.put(right, right_node);
            }
            return new Twice<>(list, check);
        }
        for (int i = 0; i < getPointsCount(); ++i) {
            list.set(c, i);
            Twice<List<Integer>, Map<String, Integer>> result = loop(list, c + 1);
            if(result != null) return result;
        }
        return null;
    }

    public boolean doStep() {
        if(getInputRuleCount() <= getPointsCount()) {
            Twice<List<Integer>, Map<String, Integer>> twice = loop(new ArrayList<>(Collections.nCopies(getInputRuleCount(), 0)), 0);
            if(twice != null) {
                List<Integer> nodes_to_remove = twice.getFirst();
                Map<String, Integer> map = twice.getSecond();
                List<Pair<Integer>> new_points = new ArrayList<>();
                for(int i = 0; i < getPointsCount(); ++i) {
                    if(!nodes_to_remove.contains(i)) new_points.add(getConnectedPoint(i));
                }
                for(Pair<String> pair : getOutputRule()) {
                    int first = getLastId()+1;
                    if(map.containsKey(pair.getFirst())) {
                        first = map.get(pair.getFirst());
                    }
                    else {
                        Point<Double> point = new Point<>(new Random().nextDouble()*10, new Random().nextDouble()*10,
                                new Random().nextDouble()*10, first);
                        point_by_id.put(first, point);
                    }
                    int second = getLastId()+1;
                    if(map.containsKey(pair.getSecond())) {
                        second = map.get(pair.getSecond());
                    }
                    else {
                        Point<Double> point = new Point<>(new Random().nextDouble() * 10, new Random().nextDouble() * 10,
                                new Random().nextDouble() * 10, second);
                        point_by_id.put(second, point);
                    }
                    new_points.add(new Pair<>(first, second));
                }
                this.points = new_points;
                return true;
            }
        }
        return false;
    }

    public List<Pair<Integer>> getPoints() {
        return points;
    }

    public int getPointsCount() {
        return getPoints().size();
    }

    public Pair<Integer> getConnectedPoint(int index) {
        return points.get(index);
    }

    public List<Pair<String>> getInputRule() {
        return input_rule;
    }

    public Pair<String> getInputRuleIndex(int index) {
        return getInputRule().get(index);
    }

    public List<Pair<String>> getOutputRule() {
        return output_rule;
    }

    public Pair<String> getOutputRuleIndex(int index) {
        return getOutputRule().get(index);
    }

    public int getInputRuleCount() {
        return getInputRule().size();
    }

    public int getOutputRuleCount() {
        return getOutputRule().size();
    }

    public Point<Double> getPointById(int id) {
        return point_by_id.get(id);
    }

    public Map<Integer, Point<Double>> getPointByIdMap() {
        return point_by_id;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public void stop() {
        isRunning = false;
    }
}
