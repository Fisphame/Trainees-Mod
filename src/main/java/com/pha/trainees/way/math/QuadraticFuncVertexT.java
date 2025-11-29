package com.pha.trainees.way.math;

public class QuadraticFuncVertexT {
    private final double a, h, k;

    public QuadraticFuncVertexT(double a, double h, double k) {
        if (a == 0) {
            throw new IllegalArgumentException(" 'a' can't be 0 ");
        }
        this.a = a;
        this.h = h;
        this.k = k;
    }

    public double getA() {
        return a;
    }

    public double getH() {
        return h;
    }

    public double getK() {
        return k;
    }

    public Pair getX(double y) {
        double rightSide = (y - k) / a;

        if (rightSide < 0) {
            return null;
        }

        double sqrtRightSide = java.lang.Math.sqrt(rightSide);
        return new Pair(h + sqrtRightSide, h - sqrtRightSide);
    }

    public double getY(double x) {
        return a * (x - h) * (x - h) + k;
    }

    public QuadraticFuncGeneralT toGeneralType() {
        double b = -2 * a * h;
        double c = a * h * h + k;
        return new QuadraticFuncGeneralT(a, b, c);
    }
}
