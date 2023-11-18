package maryan.stoykov.gpslocation.Models;

public class ReportResponse {
    private String device_id;
    private String employee_id;

    public String getDevice_id() {
        return device_id;
    }

    public String getEmployee_id() {
        return employee_id;
    }

    public String getEmployee_name() {
        return employee_name;
    }

    public String getCompany_id() {
        return company_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public String getGeozone_id() {
        return geozone_id;
    }

    public String getGeozone_name() {
        return geozone_name;
    }

    public String getInzone_time() {
        return inzone_time;
    }

    public String getExcuse_time() {
        return excuses_time;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    @Override
    public String toString() {
        return "ReportResponse{" +
                "device_id='" + device_id + '\'' +
                ", employee_id='" + employee_id + '\'' +
                ", employee_name='" + employee_name + '\'' +
                ", company_id='" + company_id + '\'' +
                ", company_name='" + company_name + '\'' +
                ", geozone_id='" + geozone_id + '\'' +
                ", geozone_name='" + geozone_name + '\'' +
                ", inzone_time='" + inzone_time + '\'' +
                ", excuse_time='" + excuses_time + '\'' +
                '}';
    }

    public void setEmployee_id(String employee_id) {
        this.employee_id = employee_id;
    }

    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public void setGeozone_id(String geozone_id) {
        this.geozone_id = geozone_id;
    }

    public void setGeozone_name(String geozone_name) {
        this.geozone_name = geozone_name;
    }

    public void setInzone_time(String inzone_time) {

        this.inzone_time = inzone_time;
    }

    public void setExcuse_time(String excuse_time) {
        this.excuses_time = excuse_time;
    }

    private String employee_name;
    private String company_id;
    private String company_name;
    private String geozone_id;
    private String geozone_name;
    private String inzone_time;
    private String excuses_time;
}
