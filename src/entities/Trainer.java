package entities;

public class Trainer {

    private int trainerID;
    private String firstName;
    private String lastName;
    private String phoneNum;

    public Trainer( int trainerID, String firstName, String lastName, String phoneNum ) {
        this.trainerID = trainerID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNum = phoneNum;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getTrainerID() {
        return trainerID;
    }

    public void setTrainerID( int trainerID ) {
        this.trainerID = trainerID;
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

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum( String phoneNum ) {
        this.phoneNum = phoneNum;
    }

}
