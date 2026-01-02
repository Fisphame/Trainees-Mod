package com.pha.trainees.way.math;

public class MAth {
    // 有关数学的拓展方法

    //实际上应该是387420489F，电脑算力还是有限的（，但其实也受加载距离影响。
    public static final float MATH99 = 38742F;

    public static final int[] POW = {
            1,        // 9^0
            9,        // 9^1
            81,       // 9^2
            729,      // 9^3
            6561,     // 9^4
            59049,    // 9^5
            531441,   // 9^6
            4782969,  // 9^7
            43046721, // 9^8
            387420489 // 9^9
    };

    public static final int MaxCountMove = POW[3];


    public static double log(double value, double base) {
        return Math.log(value) / Math.log(base);
    }
    //    double result = MathUtils.log(8, 2); // log2(8) = 3

    // 计算最大公约数
    public static int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    // 计算最小公倍数
    public static int lcm(int a, int b) {
        return Math.abs(a * b) / gcd(a, b);
    }

    // 计算数组的最小公倍数
    public static int lcmArray(int[] numbers) {
        int result = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            result = lcm(result, numbers[i]);
        }
        return result;
    }
}
