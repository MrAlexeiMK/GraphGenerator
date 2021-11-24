package ru.mralexeimk.others;

public class Pair<T> {
    private T first, second;
    public Pair(T first, T second) {
        this.first = first;
        this.second = second;
    }

    public Pair(String pair, Class<T> classT) {
        pair = pair.replaceAll("\\(", "");
        pair = pair.replaceAll("\\)", "");
        try {
            if(Integer.class.isAssignableFrom(classT)) {
                this.first = classT.cast(Integer.valueOf(pair.split(",")[0]));
                this.second = classT.cast(Integer.valueOf(pair.split(",")[1]));
            }
            else {
                this.first = classT.cast(pair.split(",")[0]);
                this.second = classT.cast(pair.split(",")[1]);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public T getFirst() {
        return first;
    }
    public T getSecond() {
        return second;
    }
    public void setFirst(T first) {
        this.first = first;
    }
    public void setSecond(T second) {
        this.second = second;
    }
    public String toString() {
        return "("+getFirst()+";"+getSecond()+")";
    }
}
