package ro.neforii.model;

public enum VoteType {
    UP,
    DOWN,
    NONE;

    public static VoteType fromString(String value) {
        return switch (value.toLowerCase()) {
            case "up" -> UP;
            case "down" -> DOWN;
            case "none" -> NONE;
            default -> throw new IllegalArgumentException("Invalid vote type: " + value + "Allowed: 'up' or 'down' .");
        };
    }
}

