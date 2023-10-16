package maryan.stoykov.gpslocation;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ServiceToActivityChannel extends Application {
    private MutableLiveData<String> liveData = new MutableLiveData<>();
    public LiveData<String> getLiveData() {
        return liveData;
    }

    public void setLiveData(String data) {
        liveData.postValue(data);
    }
}
