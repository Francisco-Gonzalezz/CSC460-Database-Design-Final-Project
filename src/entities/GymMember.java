package entities;

public class GymMember {

    private long memberID;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;

    public GymMember( String firstName, String lastName, String phoneNumber, String email ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "Member Name: " + firstName + " " + lastName + "\n" );
        sb.append( "Member Phone#: " + phoneNumber + "\n" );
        sb.append( "Member Email: " + email );
        return sb.toString();
    }

    // Getters and Setters

    public long getMemberID() {
        return memberID;
    }

    public void setMemberID( long memberID ) {
        this.memberID = memberID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName( String firstName ) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName( String lastName ) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber( String phoneNumber ) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

}
