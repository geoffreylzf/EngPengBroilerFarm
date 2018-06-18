package my.com.engpeng.engpeng.model;

public class Compartment {
    private String compartmentNo;
    private double qty;
    private double weight;
    private boolean isSelect = false;

    public String getCompartmentNo() {
        return compartmentNo;
    }

    public void setCompartmentNo(String compartmentNo) {
        this.compartmentNo = compartmentNo;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
