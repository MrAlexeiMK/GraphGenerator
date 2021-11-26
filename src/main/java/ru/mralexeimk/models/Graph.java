package ru.mralexeimk.models;

import ru.mralexeimk.MainApplication;
import ru.mralexeimk.others.Pair;
import ru.mralexeimk.others.Point;

import java.util.*;

public class Graph implements Runnable {
    private List<Pair<Integer>> points; //храним связи между нодами
    private List<Pair<String>> input_rule, output_rule;
    private Set<Integer> unique_hashes; //для отсеивания одинаковых связей
    private Map<Integer, Point<Double>> point_by_id;
    private int last_id;
    private double step;
    private boolean isRunning;
    private boolean hashing;
    private String start_str, rule_str;


    private static final Point<Double> center = new Point<>(420.0, 250.0, 300.0, 0);
    private static final double R = 100;
    public Graph(String start, String rule, double step, boolean hashing) {
        this.step = step;
        this.hashing = hashing;
        this.start_str = start;
        this.rule_str = rule;
        clear();
        initPoints(start);
        initRule(rule);
        update();
    }

    public void clear() {
        points = new ArrayList<>();
        input_rule = new ArrayList<>();
        output_rule = new ArrayList<>();
        unique_hashes = new HashSet<>();
        point_by_id = new HashMap<>();
        isRunning = false;
        last_id = 0;
    }

    public void initPoints(String start) {
        String[] spl = start.split(";");
        for(String p : spl) {
            Pair<Integer> pair = new Pair<>(p, Integer.class);
            last_id = Math.max(pair.getFirst(), pair.getSecond());
            String hash_code1 = "("+pair.getFirst()+";"+pair.getSecond()+")";
            String hash_code2 = "("+pair.getSecond()+";"+pair.getFirst()+")";
            if(!hashing) {
                points.add(pair);
            }
            else {
                if (!containsHash(hash_code1) && !containsHash(hash_code2)) {
                    points.add(pair);
                    addHash(hash_code1);
                    addHash(hash_code2);
                }
            }
        }
    }

    public void initRule(String rule) {
        String input = rule.split("->")[0];
        String output = rule.split("->")[1];
        String[] spl = input.split(";");
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

    public void createPoint(int id) {
        if(!containsPoint(id)) {
            Point<Double> point = new Point<>(id);
            point_by_id.put(id, point);
        }
    }

    public void update() {
        //очистка предыдущих связей
        for(int i = 1; i <= getLastId(); ++i) {
            if(containsPoint(i)) {
                getPoint(i).clearConnects();
            }
        }

        //инициализация точек
        for(Pair<Integer> pair : points) {
            int l = pair.getFirst();
            int r = pair.getSecond();
            createPoint(l);
            createPoint(r);
        }

        //добавление новых связей
        for(Pair<Integer> pair : points) {
            int l = pair.getFirst();
            int r = pair.getSecond();
            getPoint(l).addConnect(r);
            getPoint(r).addConnect(l);
        }

        //задание координат точкам
        for(int node : getKeys()) {
            Point<Double> p = getPoint(node);
            if(!p.isDefined()) {
                double x_m = 0, y_m = 0, z_m = 0;
                int count = 0;
                for(int connect : getPoint(node).getConnects()) {
                    Point<Double> p2 = getPoint(connect);
                    if(p2.isDefined()) {
                        x_m += p2.getX(); y_m += p2.getY(); z_m += p2.getZ();
                        ++count;
                    }
                }
                if(count > 0) {
                    x_m /= count; y_m /= count; z_m /= count;
                    double x = (x_m-R) + new Random().nextDouble()*(2*R);
                    double y = (y_m-R) + new Random().nextDouble()*(2*R);
                    double z = (z_m-R) + new Random().nextDouble()*(2*R);
                    p.setPoint(x, y, z);
                }
                else {
                    double x = (center.getX() - 20) + new Random().nextDouble()*20;
                    double y = (center.getY() - 20) + new Random().nextDouble()*20;
                    double z = (center.getZ() - 20) + new Random().nextDouble()*20;
                    p.setPoint(x, y, z);
                }
                p.define();
            }
        }
    }

    public String getStartStr() {
        return start_str;
    }

    public String getRuleStr() {
        return rule_str;
    }

    public boolean isRepeat() {
        return !hashing;
    }

    public Point<Double> getPoint(int id) {
        return point_by_id.get(id);
    }

    public void removePoint(int id) {
        point_by_id.remove(id);
    }

    public boolean containsPoint(int id) {
        return point_by_id.containsKey(id);
    }

    public Set<Integer> getKeys() {
        return point_by_id.keySet();
    }

    public int getLastId() {
        return last_id;
    }

    public void addLastId() {
        last_id++;
    }

    @Override
    public void run() {
        isRunning = true;
        while(isRunning) {
            try {
                Thread.sleep((long)(step*1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            doStep();
            //print(getPoints());
            update();
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
                    Pair<Integer> point = getConnectedPoint(i);
                    if(!nodes_to_remove.contains(i)) {
                        new_points.add(point);
                    }
                    else {
                        String hash_code1 = "("+point.getFirst()+";"+point.getSecond()+")";
                        String hash_code2 = "("+point.getSecond()+";"+point.getFirst()+")";
                        removeHash(hash_code1);
                        removeHash(hash_code2);
                    }
                }
                for(Pair<String> pair : getOutputRule()) {
                    int first = getLastId()+1;
                    if(map.containsKey(pair.getFirst())) {
                        first = map.get(pair.getFirst());
                    }
                    else {
                        addLastId();
                        map.put(pair.getFirst(), first);
                    }
                    int second = getLastId()+1;
                    if(map.containsKey(pair.getSecond())) {
                        second = map.get(pair.getSecond());
                    }
                    else {
                        addLastId();
                        map.put(pair.getSecond(), second);
                    }
                    String hash_code1 = "("+first+";"+second+")";
                    String hash_code2 = "("+second+";"+first+")";
                    if(!hashing) {
                        new_points.add(new Pair<>(first, second));
                    }
                    else {
                        if (!containsHash(hash_code1) && !containsHash(hash_code2)) {
                            new_points.add(new Pair<>(first, second));
                            addHash(hash_code1);
                            addHash(hash_code2);
                        }
                    }
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

    public double getStep() {
        return step;
    }

    public Set<Integer> getUniqueHashes() {
        return unique_hashes;
    }

    public void addHash(String str) {
        unique_hashes.add(str.hashCode());
    }

    public boolean containsHash(String str) {
        return unique_hashes.contains(str.hashCode());
    }

    public void removeHash(String str) {
        try {
            unique_hashes.remove(str.hashCode());
        } catch(Exception e) {}
    }

    public void setStep(int step) {
        this.step = step;
    }

    public void stop() {
        isRunning = false;
    }
}
