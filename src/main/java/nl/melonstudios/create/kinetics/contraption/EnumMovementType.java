package nl.melonstudios.create.kinetics.contraption;

public enum EnumMovementType {
    PLACE_WHEN_STOPPED("always", 0),
    PLACE_AT_START("starting_position", 1),
    NEVER_PLACE("never", 2);

    private final String name;
    private final int id;

    EnumMovementType(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return this.name;
    }
    public int getId() {
        return this.id;
    }

    public static EnumMovementType byName(String name) {
        switch (name) {
            case "never": return NEVER_PLACE;
            case "starting_position": return PLACE_AT_START;
            default: return PLACE_WHEN_STOPPED;
        }
    }
    public static EnumMovementType byId(int id) {
        switch (id) {
            case 2: return NEVER_PLACE;
            case 1: return PLACE_AT_START;
            default: return PLACE_WHEN_STOPPED;
        }
    }
}
