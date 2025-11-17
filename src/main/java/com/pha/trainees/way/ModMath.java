package com.pha.trainees.way;

public class ModMath {
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
}
