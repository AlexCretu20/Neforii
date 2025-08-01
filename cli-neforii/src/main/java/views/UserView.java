package views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.ViewConfig;
import models.ApiResult;
import models.user.UserResponseDto;

import java.time.format.DateTimeFormatter;

public class UserView {
    //codurile de culori ANSI sunt coduri invizibile care au o lungime care ne incurca cand folosim length() pt afisare
    //cu functia asta border-ul nu mai e stramb
    private static int visibleLength(String text) {
        return text.replaceAll("\u001B\\[[;\\d]*m", "").length();
    }

    //metoda asta nu se apeleaza din afra, e folosita intern in functiile care se ocupa de de entitati ApiResult
    private static void displayUser(UserResponseDto user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ViewConfig.DATE_FORMAT);
        String createdAtFormatted = user.createdAt().format(formatter);
        String[] fields = {
                //TO DO: putem pe viitor sa infrumutesam cu emojiuri gen https://emojipedia.org/people
                "Username:   " + ViewConfig.COLOR_BLUE + "@" + user.username() + ViewConfig.COLOR_GREEN,
                "Bio: " + user.description(),
                "Email: " + user.email(),
                "Phone: " + user.phoneNumber(),
                "ID: " + user.id(),
                "Joined: " + createdAtFormatted
        };
        int maxLength = ViewConfig.DEFAULT_WIDTH;
        for (String line : fields) {
            if (line.length() > maxLength)
                maxLength = line.length();
        }
        int widthPadding = ViewConfig.DEFAULT_WIDTH + ViewConfig.PADDING;

        String topBorder = "┌" + "─".repeat(widthPadding) + "┐";
        String separator = "├" + "─".repeat(widthPadding) + "┤";
        String bottomBorder = "└" + "─".repeat(widthPadding) + "┘";

        System.out.println(ViewConfig.COLOR_GREEN + topBorder);

        int headerVisibleLen = visibleLength(fields[0]);
        int headerSpaces = widthPadding - 1 - headerVisibleLen;
        System.out.printf("│ %s%s│%n", fields[0], " ".repeat(headerSpaces));

        System.out.println(separator);
        for (int i = 1; i < fields.length; i++) {
            System.out.printf("│ %-" + (widthPadding - 1) + "s│%n", fields[i]);
        }
        System.out.println(bottomBorder + ViewConfig.COLOR_NORMAL);
    }

    //metoda asta se foloseste intern, e folosita cand avem getSuccess() fals
    private static void displayError(String message) {
        String prefix = "[ERROR]: ";
        String fullMessage = prefix + message;

        int width = Math.max(fullMessage.length(), ViewConfig.DEFAULT_WIDTH) + ViewConfig.PADDING;

        String topBorder = "┌" + "─".repeat(width) + "┐";
        String bottomBorder = "└" + "─".repeat(width) + "┘";

        System.out.println(ViewConfig.COLOR_RED + topBorder);
        System.out.printf("│ %-" + (width - 1) + "s│%n", fullMessage);
        System.out.println(bottomBorder + ViewConfig.COLOR_NORMAL);
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
