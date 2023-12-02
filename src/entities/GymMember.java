package entities;

import enums.MembershipLevelEnum;

public class GymMember {

    private int memberID;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;

    private MembershipLevelEnum membershipLevel;

    public GymMember( String firstName, String lastName, String phoneNumber, String email ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.membershipLevel = MembershipLevelEnum.BASIC;
    }

    public GymMember(
        int memberId,
        String firstName,
        String lastName,
        String phoneNumber,
        String email,
        MembershipLevelEnum membershipLevel ) {
        this.memberID = memberId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.membershipLevel = membershipLevel;
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

    public int getMemberID() {
        return memberID;
    }

    public void setMemberID( int memberID ) {
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

    public void setMembershipLevel( MembershipLevelEnum level ) {
        this.membershipLevel = level;
    }

    public String getMembershipLevel() {
        return membershipLevel.getLevel();
    }

}
