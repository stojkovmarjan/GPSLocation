package maryan.stoykov.gpslocation.EventListeners;

import maryan.stoykov.gpslocation.Models.ReportResponse;

public interface ReportRequestResponseListener {
    public void onReportResponse(int responseCode, ReportResponse reportResponse);
}
