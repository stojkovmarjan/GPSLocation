package maryan.stoykov.gpslocation.Models;

public class ResponseRoot {
    private LocationResponse locationResponse;
    private ParametersResponse parametersResponse;

    public LocationResponse getLocationResponse() {
        return locationResponse;
    }

    public void setLocationResponse(LocationResponse locationResponse) {
        this.locationResponse = locationResponse;
    }

    public ParametersResponse getParametersResponse() {
        return parametersResponse;
    }

    public void setParametersResponse(ParametersResponse parametersResponse) {
        this.parametersResponse = parametersResponse;
    }
}
