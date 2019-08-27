package com.test.maze;

import java.util.Objects;

/**
 * Point class, which represents coordinates of maze.
 */
public class Point {
    @Override
    public String toString() {
        return "Point{" +
                "row=" + (y+1) +
                ", column=" + (x+1) +
                '}';
    }

    private int x;
    private int y;
    public Point(int y, int x) {
        this.y = y;
        this.x = x;
    }
    int getX() {
        return x;
    }
    int getY() {
        return y;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;
        Point point = (Point) o;
        return getX() == point.getX() &&
                getY() == point.getY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }
}
