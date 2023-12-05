/*
 * Class name: RentalItem
 * Jake Bode, Frankie Gonzalez
 * Purpose: This class holds the data collected from the RentalItem table
 *  within the database. It represents an item that can be rented by members
 *  at the gym while they are on the premises, as an entity.
 * Constructor: RentalItem(...) takes in all of the attributes from a single
 *  tuple in the RentalItem relation and sets all of the values to the
 *  corresponding variables in this class.
 * Public getters and setters for every attribute from the tuples are
 *  defined in this class.
 */

package entities;

public class RentalItem {

    private int itemNum;

    private String itemName;

    private int quantityInStock;

    public RentalItem( int itemNum, String itemName, int quantityInStock ) {
        this.itemNum = itemNum;
        this.itemName = itemName;
        this.quantityInStock = quantityInStock;
    }

    @Override
    public String toString() {
        return itemName + " " + quantityInStock;
    }

    // Getters and setters

    public int getItemNum() {
        return itemNum;
    }

    public void setItemNum( int itemNum ) {
        this.itemNum = itemNum;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName( String itemName ) {
        this.itemName = itemName;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock( int quantityInStock ) {
        this.quantityInStock = quantityInStock;
    }

}
