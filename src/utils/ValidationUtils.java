package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {

    private static Pattern phoneValidation = Pattern
        .compile( "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$" );

    private static Pattern emailValidation = Pattern.compile( ".+\\@.+\\..+" ); // will match an email in format x@y.z

    private ValidationUtils() {
    }

    /**
     * Validates that a phone number is in a valid format
     * @param phoneNumber Phone number as a string to validate
     * @return True if it is in a valid form, and False if not
     */
    public static boolean validatePhoneNumber( String phoneNumber ) {
        Matcher matcher = phoneValidation.matcher( phoneNumber );
        boolean isValid = matcher.find();
        return isValid;
    }

    /**
     * Validates that an email is in the following form
     * username@host.x
     * Very simple validation so a lot will pass through this
     * @param email
     * @return True if a valid email, False otherwise
     */
    public static boolean validateEmail( String email ) {
        Matcher matcher = emailValidation.matcher( email );
        boolean isValid = matcher.find();
        return isValid;
    }
}
