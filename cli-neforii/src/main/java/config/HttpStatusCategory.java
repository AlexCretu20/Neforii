package config;

public enum HttpStatusCategory {
    SUCCESS(200, 299),
    CLIENT_ERROR(400, 499),
    SERVER_ERROR(500, 599),
    OTHER(0, 999);

    private final int min;
    private final int max;

    HttpStatusCategory(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public static HttpStatusCategory from(int code) {
        for (HttpStatusCategory category : values()) {
            if (code >= category.min && code <= category.max) {
                return category;
            }
        }
        return OTHER;
    }
}
