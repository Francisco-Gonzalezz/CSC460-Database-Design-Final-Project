/*
 * Class name: RentalLogEntry
 * Jake Bode, Frankie Gonzalez
 * Utilizes java.sql.*
 * Purpose: This class holds the data collected from the RentalLog table
 *  within the database. It represents an instance of the RentalLog, which represents
 *  the relationship of a member renting an RentalItem, as an entity.
 * Constructor: RentalLogEntry(...) takes in all of the attributes from a single
 *  tuple in the RentalLog relation and sets all of the values to the
 *  corresponding variables in this class.
 * Public getters and setters for every attribute from the tuples are
 *  defined in this class.
 */

package entities;

import java.sql.Date;

public class RentalLogEntry {

    private int rentalID;
    private int memberID;
    private int itemNum;
    private Date outTime;
    private boolean isReturned;
    private int quantityBorrowed;

    public RentalLogEntry(
        int rentalID,
        int memberID,
        int itemNum,
        Date outTime,
        boolean isReturned,
        int quantityBorrowed ) {
        this.rentalID = rentalID;
        this.memberID = memberID;
        this.itemNum = itemNum;
        this.outTime = outTime;
        this.isReturned = isReturned;
        this.quantityBorrowed = quantityBorrowed;
    }

    // Getters and setters

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

    public boolean isReturned() {
        return isReturned;
    }

    public void setReturnStatus( boolean isReturned ) {
        this.isReturned = isReturned;
    }

    public int getQuantityBorrowed() {
        return quantityBorrowed;
    }

    public void setQuantityBorrowed( int quantityBorrowed ) {
        this.quantityBorrowed = quantityBorrowed;
    }

}
