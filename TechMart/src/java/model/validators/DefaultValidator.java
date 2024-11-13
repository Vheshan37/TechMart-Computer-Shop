package model;

import java.util.regex.Pattern;

public class DefaultValidator {

    private static final String DOUBLE_REGEX = "^\\d+(\\.\\d{2})?$";
    private static final Pattern DOUBLE_PATTERN = Pattern.compile(DOUBLE_REGEX);

    public static boolean isDouble(String value) {
        if (value == null || value.isEmpty()) {
            return false; // null or empty strings are not valid
        }
        return DOUBLE_PATTERN.matcher(value).matches();
    }

    private static final String INTEGER_REGEX = "^-?\\d+$";
    private static final Pattern INTEGER_PATTERN = Pattern.compile(INTEGER_REGEX);

    public static boolean isInteger(String value) {
        if (value == null || value.isEmpty()) {
            return false; // null or empty strings are not valid
        }
        return INTEGER_PATTERN.matcher(value).matches();
    }
}
