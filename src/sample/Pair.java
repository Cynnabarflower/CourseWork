package sample;

public class Pair<Key, Value> {
    public Key key;
    public Value value;

    public Pair(Key key, Value value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return ""+key+" : "+value;
    }
}


