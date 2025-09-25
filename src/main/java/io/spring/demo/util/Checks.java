package io.spring.demo.util;

import io.spring.demo.error.ValidationException;
import org.apache.commons.validator.routines.EmailValidator;

public class Checks {

    public static void validate(boolean condition, String message) {
        if (!condition)
            throw new ValidationException(message);
    }

    public static boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }
}
