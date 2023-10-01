package maryan.stoykov.gpslocation.EventListeners;

import maryan.stoykov.gpslocation.LocationDbRecord;
import maryan.stoykov.gpslocation.Models.ParametersResponse;

public interface PostLocationResponseListener {
    public void onHttpResponse(int responseCode,
                               LocationDbRecord locationDbRecord,
                               ParametersResponse parametersResponse);
}
