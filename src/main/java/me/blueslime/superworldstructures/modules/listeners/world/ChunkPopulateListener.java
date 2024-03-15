package me.blueslime.superworldstructures.modules.listeners.world;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import me.blueslime.superworldstructures.SuperWorldStructures;
import me.blueslime.superworldstructures.modules.structures.place.StructurePlace;
import me.blueslime.superworldstructures.modules.structures.space.StructureSpace;
import me.blueslime.superworldstructures.modules.structures.type.Structure;
import me.blueslime.superworldstructures.modules.utils.chunk.ChunkData;
import me.blueslime.superworldstructures.modules.utils.world.WorldBlock;
import me.blueslime.superworldstructures.modules.worlds.Worlds;
import me.blueslime.utilitiesapi.tools.PluginTools;
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
                plugin.info("&9[Debug Mode] &7Not going to load structures, because the spawn chance failed.");
            }
            return;
        }

        List<Structure> structureList = plugin.getModule(Worlds.class).fetchWorld(block.getWorldName());

        if (structureList == null || structureList.isEmpty()) {
            if (debug) {
                plugin.info("&9[Debug Mode] &eCan't find custom structures for world: &a" + block.getWorldName());
                if (structureList == null) {
                    plugin.info("&9[Debug Mode] &7Null Structures");
                } else {
                    plugin.info("&9[Debug Mode] &7No Structures");
                }
            }
            return;
        }

        block.setDirectionX(random.nextInt(16));
        block.setDirectionZ(random.nextInt(16));
        if (debug) {
            plugin.info("&9[Debug Mode] &eTrying to paste structure");
        }

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

        if (chosenPossiblePlaces.isEmpty()) {
            return;
        }

        StructurePlace structurePlace = chosenPossiblePlaces.size() >= 2 ? chosenPossiblePlaces.remove(
            random.nextInt(chosenPossiblePlaces.size())
        ) : chosenPossiblePlaces.getFirst();

        Structure structure = structureIdMap.remove(
            structurePlace.getStructureId()
        );

        String chosenSchematic = structure.getSchematics().size() >= 2 ? structure.getSchematics().get(
            random.nextInt(structure.getSchematics().size())
        ) : structure.getSchematics().getFirst();

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
            if (structurePlace.getMax() != -1 && structurePlace.getMax() != 0) {
                if (structurePlace.getMax() <= plugin.getSettings().getStringList("spawned-structures." + block.getWorldName()).size()) {
                    if (debug) {
                        plugin.info("&9[Debug Mode] &6The plugin will not paste this schematic because the max is supposed to be overpassed.");
                    }
                    return;
                }
            }
        }

        if (!file.exists()) {
            plugin.info("&7Can't find structure: &f" + file.getName());
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

        BukkitWorld bukkitWorld = new BukkitWorld(block.getWorld());

        EditSession session = PluginConsumer.ofUnchecked(
                () -> WorldEdit.getInstance().newEditSessionBuilder().world(bukkitWorld).build(),
                e -> {
                    plugin.info("&cCan't initialize the session event.");
                    e.printStackTrace();
                },
                null
        );

        if (session == null) {
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
        } else if (structurePlace.getSpace() == StructureSpace.GROUND) {
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
        } else if (structurePlace.getSpace() == StructureSpace.AIR || structurePlace.getSpace() == StructureSpace.SKY) {
            block.setHeight(maxHeight);

            int minArea = maxHeight - height;

            boolean end = false;

            for (int current = maxHeight; current >= minArea; current--) {
                block.reduce();
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
        } else if (structurePlace.getSpace() == StructureSpace.UNDER_GROUND) {
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
            plugin.info("Checking collision for this location.");
        }

        if (structurePlace.isCollision() && plugin.getSettings().contains("spawned-structures." + block.getWorldName())) {
            if (isCorrectDistance(structurePlace, block)) {
                if (debug) {
                    plugin.info("Plugin decided to cancel the placement of a structure due to a collision with other structure.");
                }
                return;
            }
        }

        if (debug) {
            plugin.info("Spawning structure id: " + structure.getId() + " at: x=" + block.getDirectionX() + ", y=" + block.getHeight() + ", z=" + block.getDirectionZ());
        }

        PluginConsumer.process(
            () -> {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(session)
                        .to(BlockVector3.at(block.getDirectionX(), block.getHeight(), block.getDirectionZ()))
                        .ignoreAirBlocks(!structurePlace.isPasteAir())
                        .copyBiomes(structurePlace.isPasteBiome())
                        .copyEntities(structurePlace.isPasteEntities())
                        .build();

                Operations.complete(
                    operation
                );
                session.close();
            }
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

    public boolean isCorrectDistance(StructurePlace place, WorldBlock block) {
        for (String location : plugin.getSettings().getStringList("spawned-structures." + block.getWorldName())) {
            String[] split = location.replace(" ", "").split(";", 2);

            String id = split[0].replace("Structure:", "");

            if (place.isCheck() && !id.equals(place.getStructureId())) {
                continue;
            }

            String defValues = split.length >= 2 ? split[1] : "x=0y=0z=0";

            defValues = defValues.replace("x=", ",")
                    .replace("y=", ",")
                    .replace("z=", ",");

            String[] splitValues = defValues.split(",", 3);

            int x2 = splitValues.length >= 1 && !splitValues[0].isEmpty() ? PluginTools.isNumber(splitValues[0]) ? Integer.parseInt(splitValues[0]) : 0 : 0;
            int y2 = splitValues.length >= 2 && !splitValues[1].isEmpty() ? PluginTools.isNumber(splitValues[1]) ? Integer.parseInt(splitValues[1]) : 0 : 0;
            int z2 = splitValues.length >= 3 && !splitValues[1].isEmpty() ? PluginTools.isNumber(splitValues[2]) ? Integer.parseInt(splitValues[2]) : 0 : 0;

            double distance = Math.sqrt(Math.pow(x2 - block.getDirectionX(), 2) + Math.pow(y2 - block.getHeight(), 2) + Math.pow(z2 - block.getDirectionZ(), 2));

            if (distance <= place.getRadius()) {
                return true;
            }
        }
        return false;
    }
}
