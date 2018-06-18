package my.com.engpeng.engpeng.model;

public class FeedItem {

    private int erpId;
    private String skuCode;
    private String skuName;
    private Long docDetailId;
    private double qty;
    private double weight;
    private String itemUomCode;
    private double stdWeight;
    private boolean isSelect = false;

    public int getErpId() {
        return erpId;
    }

    public void setErpId(int erpId) {
        this.erpId = erpId;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public Long getDocDetailId() {
        return docDetailId;
    }

    public void setDocDetailId(Long docDetailId) {
        this.docDetailId = docDetailId;
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

    public String getItemUomCode() {
        return itemUomCode;
    }

    public void setItemUomCode(String itemUomCode) {
        this.itemUomCode = itemUomCode;
    }

    public double getStdWeight() {
        return stdWeight;
    }

    public void setStdWeight(double stdWeight) {
        this.stdWeight = stdWeight;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
