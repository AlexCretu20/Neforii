package views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.ApiResult;
import models.user.UserResponseDto;

import java.time.format.DateTimeFormatter;

public class UserView {
    //culori pt afisare mai frumoasa
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String NORMAL = "\u001B[0m";

    //metoda asta nu se apeleaza din afra, e folosita intern in functiile care se ocupa de de entitati ApiResult
    private static void displayUser(UserResponseDto user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String createdAtFormatted = user.createdAt().format(formatter);
        //la afisare am ales ca widthul maxim sa fie de 32 de caractere (+2 caractere padding)
        String[] fields = {
                //TO DO: putem pe viitor sa infrumutesam cu emojiuri gen https://emojipedia.org/people
                "Username:   @" + user.username(),
                "Bio: " + user.description(),
                "Email: " + user.email(),
                "Phone: " + user.phoneNumber(),
                "ID: " + user.id(),
                "Joined: " + createdAtFormatted
        };
        int maxLength = 32;
        for (String line : fields) {
            if (line.length() > maxLength)
                maxLength = line.length();
        }
        //padding pt afisare
        int widthPadding = maxLength + 2;

        String topBorder = "┌" + "─".repeat(widthPadding) + "┐";
        String separator = "├" + "─".repeat(widthPadding) + "┤";
        String bottomBorder = "└" + "─".repeat(widthPadding) + "┘";

        System.out.println(GREEN + topBorder);
        System.out.printf("│ %-"+ (widthPadding - 1) + "s│%n", fields[0]);
        System.out.println(separator);
        for (int i = 1; i < fields.length; i++) {
            System.out.printf("│ %-" + (widthPadding - 1) + "s│%n", fields[i]);
        }
        System.out.println(bottomBorder + NORMAL);
    }

    //metoda asta se foloseste intern, e folosita cand avem getSuccess() fals
    private static void displayError(String message) {
        String prefix = "[ERROR]: ";
        String fullMessage = prefix + message;

        int minWidth = 32;
        int width = Math.max(fullMessage.length(), minWidth) + 2;

        String topBorder = "┌" + "─".repeat(width) + "┐";
        String bottomBorder = "└" + "─".repeat(width) + "┘";

        System.out.println(RED + topBorder);
        System.out.printf("│ %-" + (width - 1) + "s│%n", fullMessage);
        System.out.println(bottomBorder + NORMAL);
    }


    public static void displayUserResult(ApiResult apiResult) {
        if (apiResult.getSuccess()) { // apiResult ce a avut statusCode din clasa 2
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                //findAndRegisterModules e importanta, scaneaza classpathu si incarca modulele jackson, altfel nu stie sa converteasca String-> LocalDateTime
                objectMapper.findAndRegisterModules(); //incarca jackson datatype care stie sa serializeze/deserializeze tipuri java gen LocalDateTime
                UserResponseDto user = objectMapper.readValue(apiResult.getResponseBody(), UserResponseDto.class);
                //exceptii posibile din ObjectMapper.readValue() -> JsonProcessingException(json invalid) si JsonMappingException(nu corespunde json cu DTO)
                displayUser(user);
            } catch (JsonMappingException e) {
                displayError("A problem has appeared while processing data. Please try again later.");
            } catch (JsonProcessingException e) {
                displayError("A problem has appeared while processing data. Please try again later.");
            }
        } else {
            displayError(apiResult.getMessage());
        }
    }
}
