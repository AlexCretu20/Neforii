package validation;

public class UserValidator {

    public boolean isEmailValid(String email) {
        return email.matches("^\\S+@\\S+\\.\\S+$");
    }

    public boolean isPasswordValid(String password) {
        return password.length() >= 8;
    }

    public boolean isPhoneNumberValid(String phoneNumber) {
        return phoneNumber.matches("^07[0-9]{8}$");
    }
}
