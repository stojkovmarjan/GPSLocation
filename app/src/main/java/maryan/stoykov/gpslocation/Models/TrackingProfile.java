package maryan.stoykov.gpslocation.Models;

public class TrackingProfile {
    //        "employeeId": "string",
//                "companyId": "string",
//                "companyName": "string",
//                "geozoneId": "string",
//                "geozoneName": "string",
//                "startBtnEnabled": 0,
//                "stopBtnEnabled": 0
    private String employeeId;
    private String companyId;
    private String companyName;
    private String geozoneId;
    private String geozoneName;
    private int startBtnEnabled;
    private int stopBtnEnabled;

    @Override
    public String toString() {
        return "TrackingProfile{" +
                "employeeId='" + employeeId + '\'' +
                ", companyId='" + companyId + '\'' +
                ", companyName='" + companyName + '\'' +
                ", geozoneId='" + geozoneId + '\'' +
                ", geozoneName='" + geozoneName + '\'' +
                ", startBtnEnabled=" + startBtnEnabled +
                ", stopBtnEnabled=" + stopBtnEnabled +
                '}';
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getGeozoneId() {
        return geozoneId;
    }

    public void setGeozoneId(String geozoneId) {
        this.geozoneId = geozoneId;
    }

    public String getGeozoneName() {
        return geozoneName;
    }

    public void setGeozoneName(String geozoneName) {
        this.geozoneName = geozoneName;
    }

    public int getStartBtnEnabled() {
        return startBtnEnabled;
    }

    public void setStartBtnEnabled(int startBtnEnabled) {
        this.startBtnEnabled = startBtnEnabled;
    }

    public int getStopBtnEnabled() {
        return stopBtnEnabled;
    }

    public void setStopBtnEnabled(int stopBtnEnabled) {
        this.stopBtnEnabled = stopBtnEnabled;
    }

}
