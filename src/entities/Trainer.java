/*
 * Class name: Trainer
 * Jake Bode, Frankie Gonzalez
 * Purpose: This class holds the data collected from the Trainer table
 *  within the database. It represents a Fitness Trainer who teaches classes
 *  at the gym, as an entity.
 * Constructor: Trainer(...) takes in all of the attributes from a single
 *  tuple in the Trainer relation and sets all of the values to the
 *  corresponding variables in this class.
 * Public getters and setters for every attribute from the tuples are
 *  defined in this class.
 */

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

    // Getters and setters

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
