package maryan.stoykov.gpslocation.EventListeners;

import maryan.stoykov.gpslocation.LocationDbRecord;
import maryan.stoykov.gpslocation.Models.ParametersResponse;
import maryan.stoykov.gpslocation.Models.ResponseRoot;

public interface PostLocationResponseListener {
    public void onHttpResponse(int responseCode,
                               LocationDbRecord locationDbRecord,
                               ResponseRoot responseRoot);
}
