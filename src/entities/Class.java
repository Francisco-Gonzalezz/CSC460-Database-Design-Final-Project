package entities;

import java.sql.Date;
import java.sql.Timestamp;

public class Class {

    private int classNum;
    private int courseID;
    private int trainerID;
    private Timestamp startTime;
    private float classDuration;
    private Date startDate;
    private Date endDate;
    private int currentEnrollment;
    private int capacity;

    public Class(
        int classNum,
        int courseID,
        int trainerID,
        Timestamp startTime,
        float classDuration,
        Date startDate,
        Date endDate,
        int currentEnrollment,
        int capacity ) {
        this.classNum = classNum;
        this.courseID = courseID;
        this.trainerID = trainerID;
        this.startTime = startTime;
        this.classDuration = classDuration;
        this.startDate = startDate;
        this.endDate = endDate;
        this.currentEnrollment = currentEnrollment;
        this.capacity = capacity;
    }

    /**
     * Adds a student to the class only if the capacity is able to allow it
     * @return True if student was added successfully and false if class was full
     */
    public boolean addStudent() {
        if ( currentEnrollment + 1 > capacity ) {
            return false;
        }
        currentEnrollment++;
        return true;
    }

    // Getters and setters

    public int getClassNum() {
        return classNum;
    }

    public void setClassNum( int classNum ) {
        this.classNum = classNum;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID( int courseID ) {
        this.courseID = courseID;
    }

    public int getTrainerID() {
        return trainerID;
    }

    public void setTrainerID( int trainerID ) {
        this.trainerID = trainerID;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime( java.sql.Timestamp startTime ) {
        this.startTime = startTime;
    }

    public float getClassDuration() {
        return classDuration;
    }

    public void setClassDuration( float classDuration ) {
        this.classDuration = classDuration;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate( Date startDate ) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate( Date endDate ) {
        this.endDate = endDate;
    }

    public int getCurrentEnrollment() {
        return currentEnrollment;
    }

    public void setCurrentEnrollment( int currentEnrollment ) {
        this.currentEnrollment = currentEnrollment;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity( int capacity ) {
        this.capacity = capacity;
    }

}
