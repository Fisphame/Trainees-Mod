package com.pha.trainees.way.math;

public record Pair(double x1, double x2) {

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", x1, x2);
    }
}
