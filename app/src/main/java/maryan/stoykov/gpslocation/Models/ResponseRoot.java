package maryan.stoykov.gpslocation.Models;

public class ResponseRoot {
    //private LocationResponse locationResponse;
    private String message;
    private ParametersResponse parametersResponse;
    private TrackingProfile trackingProfile;
    private WorkDays workDays;
    private WorkTime workTime;
    //    public LocationResponse getLocationResponse() {
//        return locationResponse;
//    }
//
//    public void setLocationResponse(LocationResponse locationResponse) {
//        this.locationResponse = locationResponse;
//    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ParametersResponse getParametersResponse() {
        return parametersResponse;
    }

    public void setParametersResponse(ParametersResponse parametersResponse) {
        this.parametersResponse = parametersResponse;
    }

    public TrackingProfile getTrackingProfile() {
        return trackingProfile;
    }

    public void setTrackingProfile(TrackingProfile trackingProfile) {
        this.trackingProfile = trackingProfile;
    }

    public WorkDays getWorkDays() {
        return workDays;
    }

    public void setWorkDays(WorkDays workDays) {
        this.workDays = workDays;
    }

    public WorkTime getWorkTime() {
        return workTime;
    }

    public void setWorkTime(WorkTime workTime) {
        this.workTime = workTime;
    }

}
