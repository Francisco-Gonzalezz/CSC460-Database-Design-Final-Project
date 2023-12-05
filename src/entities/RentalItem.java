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
