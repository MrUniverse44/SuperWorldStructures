package me.blueslime.superworldstructures.modules.listeners.world;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.math.BlockVector3;
import me.blueslime.superworldstructures.SuperWorldStructures;
import me.blueslime.superworldstructures.modules.structures.place.StructurePlace;
import me.blueslime.superworldstructures.modules.structures.space.StructureSpace;
import me.blueslime.superworldstructures.modules.structures.type.Structure;
import me.blueslime.superworldstructures.modules.utils.chunk.ChunkData;
import me.blueslime.superworldstructures.modules.utils.world.WorldBlock;
import me.blueslime.superworldstructures.modules.worlds.Worlds;
import me.blueslime.utilitiesapi.utils.consumer.PluginConsumer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ChunkPopulateListener implements Listener {
    private final SuperWorldStructures plugin;

    public ChunkPopulateListener(SuperWorldStructures plugin) {
        this.plugin = plugin;
    }

    public boolean fetchBlockData(WorldBlock block, List<String> materials) {
        return materials.contains(
            block.atType(block.getDirectionX(), block.getHeight(), block.getDirectionZ())
        ) ||
        materials.contains(
            block.atType(block.getDirectionX() + block.getWidth(), block.getHeight(), block.getDirectionZ())
        ) ||
        materials.contains(
            block.atType(block.getDirectionX(), block.getHeight(), block.getDirectionZ() + block.getLength())
        ) ||
        materials.contains(
            block.atType(block.getDirectionX() + block.getWidth(), block.getHeight(), block.getDirectionZ() + block.getLength())
        );
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean fetchBlockData(WorldBlock block, String... materials) {
        return fetchBlockData(block, new ArrayList<>(Arrays.asList(materials)));
    }

    public boolean fetchBlockLevel(int blacklistedWaterLevel, WorldBlock block) {

        return block.isSolid(blacklistedWaterLevel, block.getDirectionX(), block.getHeight(), block.getDirectionZ())
                || block.isSolid(blacklistedWaterLevel, block.getDirectionX() + block.getWidth(), block.getHeight(), block.getDirectionZ())
                || block.isSolid(blacklistedWaterLevel, block.getDirectionX(), block.getHeight(), block.getDirectionZ() + block.getLength())
                || block.isSolid(blacklistedWaterLevel, block.getDirectionX() + block.getWidth(), block.getHeight(), block.getDirectionZ() + block.getLength());
    }

    public boolean fetchBlockBiome(WorldBlock block, List<String> biome) {
        return biome.contains(block.at(block.getDirectionX(), block.getHeight(), block.getDirectionZ()).getBiome().toString())
            || biome.contains(block.at(block.getDirectionX() + block.getWidth(), block.getHeight(), block.getDirectionZ()).getBiome().toString())
            || biome.contains(block.at(block.getDirectionX(), block.getHeight(), block.getDirectionZ() + block.getLength()).getBiome().toString())
            || biome.contains(block.at(block.getDirectionX() + block.getWidth(), block.getHeight(), block.getDirectionZ() + block.getLength()).getBiome().toString());
    }

    @SuppressWarnings("CallToPrintStackTrace")
    @EventHandler
    public void on(ChunkPopulateEvent event) {

        ChunkData data = ChunkData.build(
                event.getChunk(),
                event.getChunk().getX() * 16,
                event.getChunk().getZ() * 16
        );

        WorldBlock block = WorldBlock.build(event.getWorld(), data);

        Random random = ThreadLocalRandom.current();

        boolean debug = plugin.getSettings().getBoolean("debug-mode", false);

        if (random.nextInt(100) + 1 > plugin.getSettings().getInt("global-configuration.chunk-spawn-chance", 1)) {
            if (debug) {
                plugin.info("&9[Debug Mode] &cNot going to load structures, because the spawn chance failed.");
            }
            return;
        }

        List<Structure> structureList = plugin.getModule(Worlds.class).fetchWorld(block.getWorldName());

        if (structureList == null || structureList.isEmpty()) {
            if (debug) {
                plugin.info("&9[Debug Mode] &cCan't find custom structures for world: &f" + block.getWorldName());
            }
            return;
        }

        block.setDirectionX(random.nextInt(16));
        block.setDirectionZ(random.nextInt(16));

        List<StructurePlace> chosenPossiblePlaces = new ArrayList<>();
        Map<String, Structure> structureIdMap = new HashMap<>();

        for (Structure structure : structureList) {
            if (structure.isEmpty()) {
                continue;
            }
            for (StructurePlace structurePlace : structure.getPlaceList()) {
                if (random.nextInt(100) + 1 <= structurePlace.getSpawnChance()) {
                    if (debug) {
                        plugin.info("&6Added structure of &f" + structurePlace.getStructureId() + "&6 to the possible list for a chunk");
                    }
                    chosenPossiblePlaces.add(structurePlace);

                    if (!structureIdMap.containsKey(structurePlace.getStructureId())) {
                        structureIdMap.put(
                                structurePlace.getStructureId(),
                                structure
                        );
                    }
                }
            }
        }

        StructurePlace structurePlace = chosenPossiblePlaces.remove(
                random.nextInt(chosenPossiblePlaces.size())
        );

        Structure structure = structureIdMap.remove(
                structurePlace.getStructureId()
        );

        String chosenSchematic = structure.getSchematics().get(
                random.nextInt(structure.getSchematics().size())
        );

        if (debug) {
            plugin.info("&6Selected structure id for a chunk: &f" + structure.getId());
        }

        structureIdMap.clear();
        chosenPossiblePlaces.clear();

        File file = new File(plugin.getSchematicFolder(), chosenSchematic);

        if (!file.exists()) {
            if (debug) {
                plugin.info("&6Selected schematic for the check: " + chosenSchematic);
            }
            return;
        }

        if (plugin.getSettings().contains("spawned-structures." + block.getWorldName())) {
            if (structurePlace.getMax() <= plugin.getSettings().getStringList("spawned-structures." + block.getWorldName()).size()) {
                return;
            }
        }

        ClipboardFormat format = ClipboardFormats.findByFile(
                file
        );

        Clipboard clipboard = PluginConsumer.ofUnchecked(
                () -> {
                    if (format != null) {
                        ClipboardReader reader = format.getReader(Files.newInputStream(file.toPath()));

                        return reader.read();
                    }
                    return null;
                },
                e -> {
                    plugin.info("&cCan't load a clipboard because the FastAsyncWorldEdit can't read the clipboard");

                    if (debug) {
                        e.printStackTrace();
                    }
                },
                null
        );

        if (clipboard == null) {
            return;
        }

        int height = clipboard.getHeight();
        int width = clipboard.getWidth();
        int length = clipboard.getLength();
        int maxHeight = event.getWorld().getMaxHeight() - 1;

        block.setWidth(width);
        block.setLength(length);

        if (structurePlace.getSpace() == StructureSpace.ANYWHERE) {
            int minY = structurePlace.getMinY();
            int maxY = structurePlace.getMaxY();

            int calculate = random.nextInt(maxY - minY) + 1 + minY;

            if (calculate > maxHeight - height) {
                return;
            }
        }

        if (structurePlace.getSpace() == StructureSpace.GROUND) {
            int basementDepth = structurePlace.getBasementDepth();

            block.setHeight(maxHeight);

            List<String> blacklistedBlocks = plugin.getSettings().getStringList("global-configuration.space-blacklist.ground");

            if (!blacklistedBlocks.isEmpty()) {
                while (fetchBlockData(block, blacklistedBlocks)) {
                    block.reduce();
                }
            }

            if (block.getHeight() > (maxHeight - height) + basementDepth) {
                return;
            }
            if (fetchBlockLevel(plugin.getSettings().getInt("global-configuration.blacklisted-water-level", 6), block)) {
                return;
            }
            if (!structurePlace.getBiomes().isEmpty()) {
                if (fetchBlockBiome(block, structurePlace.getBiomes())) {
                    return;
                }
            }
        }
        if (structurePlace.getSpace() == StructureSpace.AIR || structurePlace.getSpace() == StructureSpace.SKY) {
            block.setHeight(maxHeight);

            int minArea = maxHeight - height;

            boolean end = false;

            for (int current = maxHeight; current > minArea; current--) {
                if (!fetchBlockData(block, "AIR", "VOID_AIR", "LEGACY_AIR", "CAVE_AIR")) {
                    end = true;
                    break;
                }
            }

            if (end) {
                return;
            }

            if (!fetchBlockData(block, "AIR", "VOID_AIR", "LEGACY_AIR", "CAVE_AIR")) {
                return;
            }
        }
        if (structurePlace.getSpace() == StructureSpace.UNDER_GROUND) {
            int placeY = block.getUnderBlock(random, structurePlace.getMinY());

            List<String> blacklistedBlocks = plugin.getSettings().getStringList("global-configuration.space-blacklist.underground");

            if (!blacklistedBlocks.isEmpty()) {
                if (fetchBlockData(block, blacklistedBlocks)) {
                    return;
                }
            }

            block.setDirectionY(placeY);
            block.setHeight(placeY);
        }

        if (debug) {
            plugin.info("Spawning structure id: " + structure.getId() + " at: x=" + block.getDirectionX() + ", y=" + block.getHeight() + ", z=" + block.getDirectionZ());
        }

        PluginConsumer.process(
            () -> clipboard.paste(
                new BukkitWorld(block.getWorld()),
                BlockVector3.at(block.getDirectionX(), block.getHeight(), block.getDirectionZ()),
                structurePlace.isPasteAir(),
                structurePlace.isPasteEntities(),
                structurePlace.isPasteBiome()
            )
        );

        List<String> locations = new ArrayList<>();

        if (plugin.getSettings().contains("spawned-structures." + block.getWorldName())) {
            locations.addAll(plugin.getSettings().getStringList("spawned-structures." + block.getWorldName()));
        }

        locations.add("Structure: " + structure.getId() + "; x=" + block.getDirectionX() + " y=" + block.getHeight() + " z=" + block.getDirectionZ());

        plugin.getSettings().set(
            "spawned-structures." + block.getWorldName(), locations
        );

        plugin.saveConfiguration(plugin.getSettings(), "settings.yml");
    }
}
