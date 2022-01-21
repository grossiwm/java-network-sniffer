package result;

import java.util.ArrayList;

public class PerSecondRateResult {

    private ArrayList<Coordinate> coordinates;

    public double getAverage() {
        return coordinates.stream().mapToDouble(c->c.y.getValue()).sum()/coordinates.size();
    }

    public PerSecondRateResult() {
        coordinates = new ArrayList<>();
    }

    public void addCordinate(Coordinate coordinate) {
        coordinates.add(coordinate);
    }

    public static class X {

        public X(int value) {
            this.value = value;
        }

        private final int value;

        public int getValue() {
            return value;
        }

    }

    public static class Y {

        public Y(double value) {
            this.value = value;
        }

        private final double value;

        public double getValue() {
            return value;
        }
    }

    public static class Coordinate {

        public Coordinate(X x, Y y) {
            this.x = x;
            this.y = y;
        }

        private final X x;
        private final Y y;

        public X getX() {
            return x;
        }

        public Y getY() {
            return y;
        }
    }
}
