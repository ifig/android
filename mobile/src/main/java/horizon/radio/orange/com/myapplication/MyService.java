package horizon.radio.orange.com.myapplication;


import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import android.util.Log;

public class MyService extends WearableListenerService implements GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = MyService.class.getSimpleName();


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"Creating service");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        mGoogleApiClient.connect();

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "CONNECTED TO THE WATCH FROM SERVICE");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "DISCONNECTED FROM THE WATCH FROM SERVICE");
    }
}
