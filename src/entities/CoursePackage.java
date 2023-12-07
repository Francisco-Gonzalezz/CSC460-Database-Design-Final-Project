package entities;

public class CoursePackage {

    private int courseID;
    private String packageName;

    public CoursePackage( int courseID, String packageName ) {
        this.courseID = courseID;
        this.packageName = packageName;
    }

    // Getters and Setters

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID( int courseID ) {
        this.courseID = courseID;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName( String packageName ) {
        this.packageName = packageName;
    }

}
