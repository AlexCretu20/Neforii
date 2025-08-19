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
        return UUID.fromString("112af037-9859-41ca-aa4a-1ce6694cec68");
    }

    public void setClientUserId(UUID userId) {
        cliUserId = userId;
    }
}
