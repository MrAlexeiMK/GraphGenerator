package ru.mralexeimk.models;

public enum Rules {
    STAR("star", true, "(1,1)", "(x,x)->(x,y);(x,x)"),
    POLYLINE("polyline", true, "(1,1)", "(x,x)->(x,y);(y,y)"),
    POLYLINE2("polyline2", true, "(1,1);(1,2)", "(x,x);(x,y)->(y,y);(y,z);(x,y);(y,x)"),
    WORMS("worms", true, "(1,2)", "(x,y)->(x,z);(z,x)"),
    WORM("worm", false, "(1,2)", "(x,y)->(y,z);(y,z)"),
    EXAMPLE_1("example-1", false, "(1,2);(2,3);(3,4);(2,4)", "(x,y);(x,z)->(x,z);(x,w);(y,w);(z,w)"),
    EXAMPLE_2("example-2", true, "(1,1);(1,1)", "(x,y);(y,z)->(w,w);(w,y);(w,x);(x,z)"),
    EXAMPLE_3("example-3", true, "(1,2);(2,2);(3,1);(1,4)", "(x,y);(y,y);(z,x);(x,u)->(y,v);(v,y);(y,z);(z,v);(u,v);(v,v)"),
    EXAMPLE_4("example-4", true, "(1,2);(2,3);(4,2);(2,5)", "(x,y);(y,z);(u,y);(y,v)->(w,z);(z,x);(z,w);(w,u);(x,y);(y,w)"),
    EXAMPLE_5("example-5", true, "(1,2);(2,3)", "(x,y);(y,z)->(x,u);(u,w);(y,v);(v,u);(z,w);(w,v)"),
    EXAMPLE_6("example-6", true, "(1,2);(2,2);(2,3);(3,4)", "(x,y);(y,y);(y,z);(z,u)->(u,z);(z,z);(u,x);(x,v);(y,u);(u,v)"),
    EXAMPLE_7("example-7", true, "(1,1)", "(x,x)->(x,y);(x,x);(y,y)");

    private String title;
    private boolean repeat;
    private String start;
    private String rule;
    Rules(String title, boolean repeat, String start, String rule) {
        this.title = title;
        this.repeat = repeat;
        this.start = start;
        this.rule = rule;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }
}
