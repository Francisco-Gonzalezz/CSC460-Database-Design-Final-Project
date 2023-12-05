package entities;

import java.sql.Date;

public class RentalLogEntry {

    private int rentalID;
    private int memberID;
    private int itemNum;
    private Date outTime;
    private Date returnTime;
    private int quantityBorrowed;

    public RentalLogEntry(
        int rentalID,
        int memberID,
        int itemNum,
        Date outTime,
        Date returnTime,
        int quantityBorrowed ) {
        this.rentalID = rentalID;
        this.memberID = memberID;
        this.itemNum = itemNum;
        this.outTime = outTime;
        this.returnTime = returnTime;
        this.quantityBorrowed = quantityBorrowed;
    }

    public int getRentalID() {
        return rentalID;
    }

    public void setRentalID( int rentalID ) {
        this.rentalID = rentalID;
    }

    public int getMemberID() {
        return memberID;
    }

    public void setMemberID( int memberID ) {
        this.memberID = memberID;
    }

    public int getItemNum() {
        return itemNum;
    }

    public void setItemNum( int itemNum ) {
        this.itemNum = itemNum;
    }

    public Date getOutTime() {
        return outTime;
    }

    public void setOutTime( Date outTime ) {
        this.outTime = outTime;
    }

    public Date getReturnTime() {
        return returnTime;
    }

    public void setReturnTime( Date returnTime ) {
        this.returnTime = returnTime;
    }

    public int getQuantityBorrowed() {
        return quantityBorrowed;
    }

    public void setQuantityBorrowed( int quantityBorrowed ) {
        this.quantityBorrowed = quantityBorrowed;
    }

}
