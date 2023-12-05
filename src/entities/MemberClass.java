/*
 * Class name: MemberClass
 * Jake Bode, Frankie Gonzalez
 * Utilizes no external packages
 * Purpose: This class holds the data collected from the MemberClass table
 *  within the database. It represents the relationship between members and
 *  the classes they are enrolled in at the gym, as an entity.
 * Constructor: MemberClass(...) takes in all of the attributes from a single
 *  tuple in the MemberClass relation and sets all of the values to the
 *  corresponding variables in this class.
 * Public getters and setters for every attribute from the tuples are
 *  defined in this class.
 */

package entities;

public class MemberClass {

    private int memberID;

    private int classNum;

    public MemberClass( int memberID, int classNum ) {
        this.memberID = memberID;
        this.classNum = classNum;
    }

    // Getters and setters

    public int getMemberID() {
        return memberID;
    }

    public void setMemberID( int memberID ) {
        this.memberID = memberID;
    }

    public int getClassNum() {
        return classNum;
    }

    public void setClassNum( int classNum ) {
        this.classNum = classNum;
    }

}
