package entities;

public class Course {

    private int courseID;

    private String category;

    private int catalogNum;

    public Course( int courseID, String category, int catalogNum ) {
        this.courseID = courseID;
        this.category = category;
        this.catalogNum = catalogNum;
    }

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
