package maryan.stoykov.gpslocation.Models;

public class WorkDays {
    private int workingMon;
    private int workingTue;
    private int workingWed;
    private int workingThu;
    private int workingFri;
    private int workingSat;
    private int workingSun;

    @Override
    public String toString() {
        return "WorkDays{" +
                "workingMon=" + workingMon +
                ", workingTue=" + workingTue +
                ", workingWed=" + workingWed +
                ", workingThu=" + workingThu +
                ", workingFri=" + workingFri +
                ", workingSat=" + workingSat +
                ", workingSun=" + workingSun +
                '}';
    }

    public int getWorkingMon() {
        return workingMon;
    }

    public void setWorkingMon(int workingMon) {
        this.workingMon = workingMon;
    }

    public int getWorkingTue() {
        return workingTue;
    }

    public void setWorkingTue(int workingTue) {
        this.workingTue = workingTue;
    }

    public int getWorkingWed() {
        return workingWed;
    }

    public void setWorkingWed(int workingWed) {
        this.workingWed = workingWed;
    }

    public int getWorkingThu() {
        return workingThu;
    }

    public void setWorkingThu(int workingThu) {
        this.workingThu = workingThu;
    }

    public int getWorkingFri() {
        return workingFri;
    }

    public void setWorkingFri(int workingFri) {
        this.workingFri = workingFri;
    }

    public int getWorkingSat() {
        return workingSat;
    }

    public void setWorkingSat(int workingSat) {
        this.workingSat = workingSat;
    }

    public int getWorkingSun() {
        return workingSun;
    }

    public void setWorkingSun(int workingSun) {
        this.workingSun = workingSun;
    }
}
