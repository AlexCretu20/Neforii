package ro.neforii.service;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FakeUserAuthService {
    private static UUID cliUserId = null; // sa tinem minte, in main dupa login: FakeAuthService.setCliUserId(user.getId());

    public UUID getCurrentUserId() {
        if (cliUserId != null){
            return cliUserId;
        }
        return UUID.fromString("550e8400-e29b-41d4-a716-446655440098");
    }

    public static void setClientUserId(UUID userId) {
        cliUserId = userId;
    }
}
