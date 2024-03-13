package me.blueslime.superworldstructures.modules.structures.space;

import me.blueslime.superworldstructures.modules.structures.place.StructurePlace;

import java.util.Locale;

public enum StructureSpace {
    GROUND,
    SKY,
    ANYWHERE,
    AIR,
    UNDER_GROUND;

    public String toLowerString() {
        return toString().toLowerCase(Locale.ENGLISH);
    }

    public static StructureSpace fromString(String value) {
        value = value.toLowerCase(Locale.ENGLISH).replace("-", "_").replace(" ", "_");

        for (StructureSpace space : values()) {
            if (space.toLowerString().equals(value)) {
                return space;
            }
        }
        return ANYWHERE;
    }


}
