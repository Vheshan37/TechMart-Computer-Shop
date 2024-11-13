package model.validators;

import java.util.regex.Pattern;

public class MobileValidator {

    private static final String MOBILE_NUMBER_REGEX = "^0[7-9][0-9]{8}$";
    private static final Pattern MOBILE_NUMBER_PATTERN = Pattern.compile(MOBILE_NUMBER_REGEX);

    public static boolean validate(String mobileNumber) {
        if (mobileNumber == null || mobileNumber.isEmpty()) {
            return false; // Return false for null or empty strings
        }
        return MOBILE_NUMBER_PATTERN.matcher(mobileNumber).matches();
    }
}
