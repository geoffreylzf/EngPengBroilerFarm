package my.com.engpeng.engpeng.model;

public class FeedDischargeLocation {
    private int id, companyId;
    private String locationCode, locationName, companyCode;
    private boolean isSelect = false;

    public FeedDischargeLocation(int id, int companyId, String locationCode, String locationName, String companyCode) {
        this.id = id;
        this.companyId = companyId;
        this.locationCode = locationCode;
        this.locationName = locationName;
        this.companyCode = companyCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
