package my.com.engpeng.engpeng.model;

public class HouseCode {

    private int house_code;
    private boolean is_select = false;

    public void setHouseCode(int house_code){
        this.house_code = house_code;
    }

    public void setIsSelect(boolean is_select){
        this.is_select = is_select;
    }

    public int getHouseCode(){
        return house_code;
    }

    public boolean getIsSelect(){
        return this.is_select;
    }
}
