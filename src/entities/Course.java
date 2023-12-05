/*
 * Class name: Course
 * Jake Bode, Frankie Gonzalez
 * Utilizes no outside packages
 * Purpose: This class holds the data collected from the Course table
 *  within the database. It represents a specific course that can be
 *  taught in the gym, using instances of Classes, as an entity.
 * Constructor: Course(...) takes in all of the attributes from a single
 *  tuple in the Course relation and sets all of the values to the
 *  corresponding variables in this class.
 * Public getters and setters for every attribute from the tuples are
 *  defined in this class.
 */

package entities;

public class Course {

    private int courseID;

    private String category;

    private int catalogNum;

    // Constructor
    public Course( int courseID, String category, int catalogNum ) {
        this.courseID = courseID;
        this.category = category;
        this.catalogNum = catalogNum;
    }

    /*
     * This method returns the value of the catalog number "1", "2", etc.
     * as a string version, padded with leading zeroes, to meet a length
     * of three ("001", "002").
     */
    public String padCatalogNum() {
        String catNum = String.valueOf( catalogNum );
        String padding = "";
        int i = 0;
        while ( padding.length() + catNum.length() < 3 ) {
            padding += "0";
        }
        return padding + catNum;
    }

    @Override
    public String toString() {
        return category + " " + padCatalogNum();
    }

    // Getters and Setters

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID( int courseID ) {
        this.courseID = courseID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory( String category ) {
        this.category = category;
    }

    public int getCatalogNum() {
        return catalogNum;
    }

    public void setCatalogNum( int catalogNum ) {
        this.catalogNum = catalogNum;
    }

}
