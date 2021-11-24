package ru.mralexeimk.others;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RulesParser {
    private String start;
    private String rule;
    public RulesParser(String str) {
        str = str.replaceAll(" ", "");
        this.start = str.split("->")[0];
        this.rule = str.split("->")[1];
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public List<Point<Double>> parse() {
        List<Point<Double>> points = new ArrayList<>();

        return points;
    }

    public void simulate(List<Point<Double>> current) {

    }
}
