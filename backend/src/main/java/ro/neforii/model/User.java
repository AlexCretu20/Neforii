package ro.neforii.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//equals folosita in User Service sa stim daca modelul de user primit pentru update e la fel cu userul existent
@EqualsAndHashCode
public class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private String phoneNumber;
    private String description;
    private LocalDateTime createdAt = LocalDateTime.now();

    public User(String username, String email, String password, String phoneNumber, String description) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.description = description;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("───────────────────────────────────\n");
        sb.append(" User ID : ").append(id).append("\n");
        sb.append(" Username : ").append(username).append("\n");
        sb.append(" Email : ").append(email).append("\n");
        sb.append(" Phone Number : ").append(phoneNumber).append("\n");
        sb.append(" Description : ").append(description).append("\n");
        sb.append(" Created At : ").append(createdAt).append("\n");
        sb.append("───────────────────────────────────");
        return sb.toString();
    }
}
