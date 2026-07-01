package com.pha.trainees.util.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public interface Traversal {
    int[] dx3 = {0, 0, 0, 0, -1, 1};  // X方向
    int[] dy3 = {1, -1, 0, 0, 0, 0};  // Y方向（上下）
    int[] dz3 = {0, 0, -1, 1, 0, 0};  // Z方向
    int[] altarDx = {-3, 3, 0, 0};
    int[] altarDz = {0, 0, -3, 3};
    Vec3i[] altars = {
            new Vec3i(-3, -1, 0),
            new Vec3i(3, -1, 0),
            new Vec3i(0, -1, -3),
            new Vec3i(0, -1, 3)
    };

    default BlockPos getAltarNorth(BlockPos pos) {
        return pos.offset(altars[0]);
    }
    default BlockPos getAltarSouth(BlockPos pos) {
        return pos.offset(altars[1]);
    }
    default BlockPos getAltarWest(BlockPos pos) {
        return pos.offset(altars[2]);
    }
    default BlockPos getAltarEast(BlockPos pos) {
        return pos.offset(altars[3]);
    }



    default BlockPos dfs(Level level, BlockPos pos) {
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.offer(pos);
        visited.add(pos);
        while (!queue.isEmpty()) {
            BlockPos uPos = queue.poll();
            int ux = uPos.getX();
            int uy = uPos.getY();
            int uz = uPos.getZ();

            for (int i = 0; i < 6; i++) {
                int vx = ux + dx3[i];
                int vy = uy + dy3[i];
                int vz = uz + dz3[i];
                BlockPos vPos = new BlockPos(vx, vy, vz);

                if (true) {
                    visited.add(vPos);
//                    if (isTarget(level, vPos)) {
//                        return vPos;
//                    }
                    queue.offer(vPos);
                }
            }
        }
        return null;
    }


}
