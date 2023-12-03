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
