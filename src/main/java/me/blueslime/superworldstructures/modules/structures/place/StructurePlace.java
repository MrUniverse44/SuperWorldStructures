package me.blueslime.superworldstructures.modules.structures.place;

import me.blueslime.superworldstructures.modules.structures.space.StructureSpace;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class StructurePlace {
    private final List<String> biomes = new ArrayList<>();
    private final boolean pasteEntities;
    private final StructureSpace space;
    private final boolean pasteBiome;
    private final int basementDepth;
    private final int spawnChance;
    private final String rotation;
    private final boolean air;
    private final String id;
    private final int maxY;
    private final int minY;
    private final int max;

    public StructurePlace(
        String id,
        boolean pasteEntities,
        List<String> biomes,
        boolean pasteBiome,
        int basementDepth,
        int spawnChance,
        String rotation,
        String space,
        boolean air,
        int maxY,
        int minY,
        int max
    ) {
        this.biomes.addAll(biomes);

        this.basementDepth = basementDepth;
        this.pasteEntities = pasteEntities;
        this.spawnChance = spawnChance;
        this.pasteBiome = pasteBiome;
        this.rotation = rotation;
        this.space = StructureSpace.fromString(space);
        this.maxY = maxY;
        this.minY = minY;
        this.air = air;
        this.max = max;
        this.id = id;
    }

    public StructurePlace(String id, ConfigurationSection configuration, String path) {
        this(
            id,
            configuration.getBoolean(path + ".paste-biome", false),
            configuration.getStringList(path + ".biomes"),
            configuration.getBoolean(path + ".paste-entities", true),
            configuration.getInt(path + ".basement-depth", 0),
            configuration.getInt(path + ".spawn-chance", 10),
            configuration.getString(path + ".rotation", "RANDOM"),
            configuration.getString(path + ".space", "GROUND"),
            configuration.getBoolean(path + ".air", false),
            configuration.getInt(path + ".anywhere.maxY", 80),
            configuration.getInt(path + ".anywhere.minY", 80),
            configuration.getInt(path + ".max", -1)
        );
    }

    public static List<StructurePlace> importStructures(
        String id,
        ConfigurationSection configuration,
        ConfigurationSection section,
        String path
    ) {
        List<StructurePlace> list = new ArrayList<>();
        if (section == null) {
            return list;
        }
        for (String key : section.getKeys(false)) {
            list.add(
                new StructurePlace(id, configuration, path + key)
            );
        }
        return list;
    }

    public String getId() {
        return id;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMinY() {
        return minY;
    }

    public String getStructureId() {
        return id;
    }

    public int getMax() {
        return max;
    }

    public boolean isPasteAir() {
        return air;
    }

    public boolean isEmpty() {
        return biomes.isEmpty();
    }

    public boolean isPasteBiome() {
        return pasteBiome;
    }


    public boolean isPasteEntities() {
        return pasteEntities;
    }

    public StructureSpace getSpace() {
        return space;
    }

    public String getRotation() {
        return rotation;
    }

    public int getSpawnChance() {
        return spawnChance;
    }

    public int getBasementDepth() {
        return basementDepth;
    }

    public List<String> getBiomes() {
        return biomes;
    }
}
