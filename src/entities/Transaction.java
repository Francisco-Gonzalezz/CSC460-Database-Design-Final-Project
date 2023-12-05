/*
 * Class name: Transaction
 * Jake Bode, Frankie Gonzalez
 * Utilizes java.sql.*
 * Purpose: This class holds the data collected from the Transaction table
 *  within the database. It represents a Transaction entity, which describes
 *  an instance of a member purchasing a package or adding funds to their account
 *  balance.
 * Constructor: Transaction(...) takes in all of the attributes from a single
 *  tuple in the Transaction relation and sets all of the values to the
 *  corresponding variables in this class.
 * Public getters and setters for every attribute from the tuples are
 *  defined in this class.
 */

package entities;

import java.sql.Date;

public class Transaction {

    private int transactionID;

    private int memberID;

    private String xactType;

    private Date xactDate;

    private float amount;

    public Transaction( int transactionID, int memberID, String xactType, Date xactDate, float amount ) {
        this.transactionID = transactionID;
        this.memberID = memberID;
        this.xactType = xactType;
        this.xactDate = xactDate;
        this.amount = amount;
    }

    // Getters and setters

    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID( int transactionID ) {
        this.transactionID = transactionID;
    }

    public int getMemberID() {
        return memberID;
    }

    public void setMemberID( int memberID ) {
        this.memberID = memberID;
    }

    public String getXactType() {
        return xactType;
    }

    public void setXactType( String xactType ) {
        this.xactType = xactType;
    }

    public Date getXactDate() {
        return xactDate;
    }

    public void setXactDate( Date xactDate ) {
        this.xactDate = xactDate;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount( float amount ) {
        this.amount = amount;
    }

}
