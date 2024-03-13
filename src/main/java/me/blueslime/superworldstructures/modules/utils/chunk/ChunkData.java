package me.blueslime.superworldstructures.modules.utils.chunk;

import org.bukkit.Chunk;
import org.bukkit.Location;

public class ChunkData {
    private final Chunk chunk;
    private final int x;
    private final int z;

    private ChunkData(ChunkData data) {
        this.chunk = data.getChunk();
        this.x = data.getX();
        this.z = data.getZ();
    }

    private ChunkData(Chunk chunk, int x, int z) {
        this.chunk = chunk;
        this.x = x;
        this.z = z;
    }

    private ChunkData(Chunk chunk, Location location) {
        this.chunk = chunk;
        this.x = location.getBlockX();
        this.z = location.getBlockZ();
    }

    public static ChunkData build(Chunk chunk, int x, int y) {
        return new ChunkData(chunk, x, y);
    }

    public static ChunkData build(Chunk chunk, Location location) {
        return new ChunkData(chunk, location);
    }

    public static ChunkData build(ChunkData data) {
        return new ChunkData(data);
    }

    public int getZ() {
        return z;
    }

    public int getX() {
        return x;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
