package me.blueslime.superworldstructures.modules.utls;

import org.bukkit.World;
import org.bukkit.block.Block;

public class WorldBlock {
    private final World world;

    private WorldBlock(World world) {
        this.world = world;
    }

    public Block at(int x, int y, int z) {
        return world.getBlockAt(x, y, z);
    }

    public static WorldBlock build(World world) {
        return new WorldBlock(world);
    }
}
