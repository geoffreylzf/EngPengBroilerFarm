package my.com.engpeng.engpeng.model;

public class TempBtaWorker {
    private int id;
    private Integer personStaffId;
    private String workerName;

    public TempBtaWorker(int id, Integer personStaffId, String workerName) {
        this.id = id;
        this.personStaffId = personStaffId;
        this.workerName = workerName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getPersonStaffId() {
        return personStaffId;
    }

    public void setPersonStaffId(Integer personStaffId) {
        this.personStaffId = personStaffId;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }
}
