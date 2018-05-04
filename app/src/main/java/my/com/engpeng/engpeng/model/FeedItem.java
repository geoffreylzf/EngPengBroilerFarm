package my.com.engpeng.engpeng.model;

public class FeedItem {

    private int erp_id;
    private String sku_code;
    private String sku_name;
    private boolean is_select = false;

    public void setErpId(int erp_id){
        this.erp_id = erp_id;
    }

    public void setSkuCode(String sku_code){
        this.sku_code = sku_code;
    }

    public void setSkuName(String sku_name){
        this.sku_name = sku_name;
    }

    public void setIsSelect(boolean is_select){
        this.is_select = is_select;
    }

    public int getErpId(){
        return this.erp_id;
    }

    public String getSkuCode(){
        return this.sku_code;
    }

    public String getSkuName(){
        return this.sku_name;
    }

    public boolean getIsSelect(){
        return this.is_select;
    }
}
