package maryan.stoykov.gpslocation;

public interface PostLocationResponseListener {
    public void onHttpResponse(int responseCode, LocationDbRecord locationDbRecord);
}
