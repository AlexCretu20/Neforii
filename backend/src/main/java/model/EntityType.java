package model;

public enum EntityType {
    POST("post"),
    COMMENT("comment");

    private final String dbValue;

    EntityType(String dbValue){
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static EntityType fromDbValue(String dbValue) {
        for (EntityType type : EntityType.values()) {
            if (type.getDbValue().equals(dbValue)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown db value: " + dbValue);
    }
}
