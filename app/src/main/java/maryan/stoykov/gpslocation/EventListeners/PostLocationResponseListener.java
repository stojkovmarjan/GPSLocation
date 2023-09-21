package maryan.stoykov.gpslocation.EventListeners;

import maryan.stoykov.gpslocation.LocationDbRecord;

public interface PostLocationResponseListener {
    public void onHttpResponse(int responseCode, LocationDbRecord locationDbRecord);
}
