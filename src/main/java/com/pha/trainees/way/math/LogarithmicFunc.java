package com.pha.trainees.way.math;

import com.pha.trainees.Main;

public class LogarithmicFunc {
    private final double a;

    public LogarithmicFunc(double a){
        this.a = a;
    }

    public double getA() {
        return a;
    }

    public double getX(double y){
        return java.lang.Math.pow(a, y);
    }

    public double getY(double x) {
        return Math.log(x, a);
    }
}
