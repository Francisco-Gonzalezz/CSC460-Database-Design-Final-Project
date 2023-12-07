package entities;

public class Package {

    private String packageName;
    private float cost;

    public Package( String packageName, float cost ) {
        this.packageName = packageName;
        this.cost = cost;
    }

    // Getters and Setters

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName( String packageName ) {
        this.packageName = packageName;
    }

    public float getCost() {
        return cost;
    }

    public void setCost( float cost ) {
        this.cost = cost;
    }

}
