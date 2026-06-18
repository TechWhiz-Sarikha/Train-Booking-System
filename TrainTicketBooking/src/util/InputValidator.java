package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class InputValidator {

    private InputValidator() {}

    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.matches("[a-zA-Z ]+");
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\d{10}");
    }

    public static boolean isValidTrainNumber(String trainNo) {
        return trainNo != null && !trainNo.trim().isEmpty();
    }

    /**
     * Validates travel date: must be a future date within 3 months.
     * Format: yyyy-MM-dd
     */
    public static boolean isValidTravelDate(String dateStr) {
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(dateStr, fmt);
            LocalDate today = LocalDate.now();
            LocalDate maxDate = today.plusMonths(3);
            return !date.isBefore(today) && !date.isAfter(maxDate);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isValidTime(String time) {
        return time != null && time.matches("([01]\\d|2[0-3]):[0-5]\\d");
    }
}
