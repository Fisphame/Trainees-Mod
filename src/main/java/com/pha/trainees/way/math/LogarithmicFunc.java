package com.pha.trainees.way.math;

public record LogarithmicFunc(double a) {

    public double getX(double y) {
        return Math.pow(a, y);
    }

    public double getY(double x) {
        return MAth.log(x, a);
    }
}
