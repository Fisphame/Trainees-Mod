package com.pha.trainees.way.math;

public class QuadraticFuncGeneralT {
    private final double a, b, c;

    public QuadraticFuncGeneralT(double a, double b, double c){
        if (a == 0) {
            throw new IllegalArgumentException(" 'a' can't be 0 ");
        }
        this.a = a; this.b = b; this.c = c;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    public double getDiscriminant() {
        return b * b - 4 * a * c;
    }


    public Pair getX(double y){
        final double discriminant = b * b - 4 * a * (c - y);

        if ( discriminant < 0){
            return null;
        }

        double sqrtDiscriminant = java.lang.Math.sqrt(discriminant);
        double denominator = 2 * a;

        return new Pair(
                (-b + sqrtDiscriminant) / denominator,
                (-b - sqrtDiscriminant) / denominator
        );
    }

    public double getY(double x) {
        return a * x * x + b * x + c;
    }

    public QuadraticFuncVertexT toVertexType() {
        double h = -b / (2 * a);
        double k = getY(h);
        return new QuadraticFuncVertexT(a, h, k);
    }
}