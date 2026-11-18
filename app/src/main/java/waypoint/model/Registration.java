package waypoint.model;

import java.util.Objects;
import java.util.regex.Pattern;

/** VALUE OBJECT. TODO: validate and normalise so an invalid instance cannot exist. */
public record Registration(String value) {
    private static final Pattern FORMAT = Pattern.compile("^[A-Z]{2}\\d{2}[A-Z]{1,2}\\d{4}$");

    public Registration {
        Objects.requireNonNull(value, "Registration must not be null");
        value = value.trim().toUpperCase();
        if (!FORMAT.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid registration format: " + value);
        }
    }
}