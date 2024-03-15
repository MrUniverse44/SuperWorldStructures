package me.blueslime.superworldstructures.modules.utils.world;

import me.blueslime.superworldstructures.modules.utils.chunk.ChunkData;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;

import java.util.Random;

public class WorldBlock {
    private final ChunkData chunk;
    private final World world;

    private int directionX = 0;
    private int directionY = 0;
    private int directionZ = 0;

    private int length = 0;
    private int height = 0;
    private int width = 0;

    private WorldBlock(World world, ChunkData chunk) {
        this.world = world;
        this.chunk = chunk;
    }

    public Block at(int x, int y, int z) {
        return world.getBlockAt(chunk.getX() + x, y,chunk.getZ() + z);
    }

    public String atType(int x, int y, int z) {
        return world.getBlockAt(chunk.getX() + x, y,chunk.getZ() + z).getType().toString();
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setDirectionZ(int directionZ) {
        this.directionZ = directionZ;
    }

    public void setDirectionX(int directionX) {
        this.directionX = directionX;
    }

    public void setDirectionY(int directionY) {
        this.directionY = directionY;
    }

    public static WorldBlock build(World world, ChunkData chunk) {
        return new WorldBlock(world, chunk);
    }

    public int getDirectionX() {
        return directionX;
    }

    public int getDirectionZ() {
        return directionZ;
    }

    public int getDirectionY() {
        return directionY;
    }

    public int getHeight() {
        return height;
    }

    public String getWorldName() {
        return world.getName();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void reduce() {
        height--;
    }

    public int getConvertedDirectionX() {
        return chunk.getX() + directionX;
    }

    public int getConvertedDirectionZ() {
        return chunk.getZ() + directionZ;
    }

    public boolean isSolid(int blacklistedWaterLevel, int directionX, int height, int directionZ) {
        BlockData data = world.getBlockAt(chunk.getX() + directionX, height,chunk.getZ() + directionZ).getState().getBlockData();

        if (data instanceof Levelled) {
            return ((Levelled)data).getLevel() == blacklistedWaterLevel;
        }
        return false;
    }

    public int getUnderBlock(Random random, int min) {
        return world.getHighestBlockYAt(directionX, directionZ) - random.nextInt(min);
    }

    public World getWorld() {
        return world;
    }
}
