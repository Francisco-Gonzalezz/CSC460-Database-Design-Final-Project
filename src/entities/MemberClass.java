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
