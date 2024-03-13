package me.blueslime.superworldstructures.modules.structures.type;

import me.blueslime.superworldstructures.modules.structures.place.StructurePlace;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class Structure {
    private final List<StructurePlace> placeList = new ArrayList<>();
    private final List<String> schematics = new ArrayList<>();
    private final String id;

    public Structure(String id, List<StructurePlace> placeList, List<String> schematics) {
        this.placeList.addAll(placeList);
        this.schematics.addAll(schematics);
        this.id = id;
    }

    public Structure(
        ConfigurationSection configuration,
        String path,
        String id
    ) {
        this(
            id,
            StructurePlace.importStructures(
                id,
                configuration,
                configuration.getConfigurationSection(path + ".places"),
                path + ".places."
            ),
            configuration.getStringList(path + ".schematics")
        );
    }

    public String getId() {
        return id;
    }

    public boolean isEmpty() {
        return placeList.isEmpty() || schematics.isEmpty();
    }

    public List<String> getSchematics() {
        return schematics;
    }

    public List<StructurePlace> getPlaceList() {
        return placeList;
    }
}
