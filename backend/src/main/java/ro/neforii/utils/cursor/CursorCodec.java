package ro.neforii.utils.cursor;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

public class CursorCodec {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static String encode(LocalDateTime createdAt, UUID id) {
        String raw = createdAt.format(FORMATTER) + "|" + id;
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static Cursor decode(String cursor) {
        String raw = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
        String[] parts = raw.split("\\|", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid cursor");
        }
        LocalDateTime createdAt = LocalDateTime.parse(parts[0], FORMATTER);
        UUID id = UUID.fromString(parts[1]);
        return new Cursor(createdAt, id);
    }

    public record Cursor(LocalDateTime createdAt, UUID id) {}
}