/**
 * Class: MembershipLevelEnum.java
 * Purpose: This class holds the different membership levels available. Each enum represents a seperate level.
 * 
 * Constructor(String level): Is private and should only be called by the enums themselves and it sets the text that is to be returned when 
 * the string literal is needed.
 * 
 * Public getter to get the string literal from the enum
 * @author Francisco Gonzalez
 * @author Jake Bode
 * @version 1.0
 */
package enums;

public enum MembershipLevelEnum {
    BASIC( "BASIC" ), GOLD( "GOLD" ), DIAMOND( "DIAMOND" );

    private String text; // String literal to return

    private MembershipLevelEnum( String level ) {
        this.text = level;
    }

    /**
     * Retrieve the string representation of the enum
     * @return String representation of the enum
     */
    public String getLevel() {
        return text;
    }
}
