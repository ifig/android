package horizon.radio.orange.com.myapplication;

import android.os.Bundle;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import android.util.Log;



public class Moto360Service implements GoogleApiClient.ConnectionCallbacks, MessageApi.MessageListener {
    private static final String TAG = Moto360Service.class.getSimpleName();

    private static Moto360Service moto360ServiceInstance = null;

    private GoogleApiClient mApiClient;

    public Moto360Service() {

        initGoogleApiClient();

    }


    /**
     * <h1><b>getInstance() ()</b></h1>
     * Return the unique instance of Moto 360 service<p>
     * @return PebbleWatchService unique instance of Pebble watch service
     *
     */
    public static Moto360Service getInstance() {
        synchronized (TAG) {
            if (moto360ServiceInstance == null) {
                moto360ServiceInstance = new Moto360Service();
            }
        }
        return moto360ServiceInstance;
    }


    private void initGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder(MainActivity.getInstance().getApplicationContext())
                .addApi( Wearable.API )
                .build();

        if( mApiClient != null && !( mApiClient.isConnected() || mApiClient.isConnecting() ) )
            mApiClient.connect();
    }


    protected void sendMessage(final String path, final String text) {
        if(mApiClient != null) {
            Log.d(TAG, "mApiClient is not null!");
        }
        if (mApiClient.isConnected() ){
            Log.d(TAG, "mApiClient is connected!");
        } else if (mApiClient.isConnecting()) {
            Log.d(TAG, "mApiClient is connecting!");
        }
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mApiClient).await();
                for (Node node : nodes.getNodes()) {
                    Log.d(TAG, "name : " + node.getDisplayName() + "id : " + node.getId());
                    Log.d(TAG, "mApiClient : " + mApiClient);
                    Log.d(TAG, "sending message");
                    Wearable.MessageApi.sendMessage(mApiClient, node.getId(), path, text.getBytes());
                }
            }
        }).start();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "CONNECTED TO THE WATCH");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    //################################ Data receiver ########################################################################################################
    //Reached when some message is received from the android wear
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG,"message received");
    }

}
