package my.com.engpeng.engpeng.model;

public class PersonStaffSelection {
    private int id;
    private String staffCode, staffName;
    private boolean isSelect = false;

    public PersonStaffSelection(int id, String staffCode, String staffName) {
        this.id = id;
        this.staffCode = staffCode;
        this.staffName = staffName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
