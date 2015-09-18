package horizon.radio.orange.com.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;


import java.util.ArrayList;

public class MainActivity extends Activity implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks,DataApi.DataListener, WearableListView.ClickListener {
    private GoogleApiClient mApiClient;
    private SimpleListAdapter mAdapter;
    private static final String TAG = MainActivity.class.getSimpleName();

    private ArrayList<String> list = new ArrayList<>();
    private WearableListView mListView;

    private ProgressDialog dialog;
    private String showReceived;
    private Float showSize;
    private Context mContext;

    private Handler myHandler = new Handler();
    private int progressValue = 0;

    protected static final int SELECTION_INDEX=1;
    protected static final int RADIOS_INDEX=2;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mContext = this;
        Log.d(TAG, "on create");

        setContentView(R.layout.activity_main);
        mListView = (WearableListView) findViewById(R.id.wearable_list);
        list.add("Selection");
        list.add("Mes Contenus");
        mAdapter = new SimpleListAdapter(mContext, list);
        mListView.setAdapter(mAdapter);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initGoogleApiClient();
    }

    private void initGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks( this )
                .build();

        if( mApiClient != null && !( mApiClient.isConnected() || mApiClient.isConnecting() ) )
            mApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "ON RESUME");

        mAdapter = new SimpleListAdapter(mContext, list);
        mListView.setAdapter(mAdapter);
        mListView.setClickListener(this);

        if( mApiClient != null && !( mApiClient.isConnected() || mApiClient.isConnecting() ) )
            mApiClient.connect();

        sendMessage("/path", "hahahahah");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "on start");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "on restart");
    }

    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "message received");
                Intent intent = new Intent(mContext, ConfirmationActivity.class);
                intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                        ConfirmationActivity.SUCCESS_ANIMATION);
                intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                        "File received!");
                startActivity(intent);

                /*
                if (messageEvent.getPath().equalsIgnoreCase(WEAR_MESSAGE_NAME)) {
                    showReceived = new String(messageEvent.getData());
                    Log.d(TAG, "name received");
                }
                if (messageEvent.getPath().equalsIgnoreCase(WEAR_MESSAGE_SIZE)) {
                    showSize = Float.parseFloat(new String(messageEvent.getData()));
                    Log.d(TAG, "size received");
                }*/

            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "connected");
        Wearable.DataApi.addListener(mApiClient, this);
        Wearable.MessageApi.addListener(mApiClient, this);
    }

    @Override
    protected void onStop() {
        if ( mApiClient != null ) {
            Wearable.DataApi.removeListener(mApiClient, this);
            Wearable.MessageApi.removeListener(mApiClient, this);

            if ( mApiClient.isConnected() ) {
                mApiClient.disconnect();
            }
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if( mApiClient != null )
            mApiClient.unregisterConnectionCallbacks(this);
        super.onDestroy();
        //unregisterReceiver(onComplete);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "connection suspended");

        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, "CONNECTION SUSPENDED!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {

        Integer tag = (Integer) viewHolder.itemView.getTag();
        Log.d(TAG, "tag : " + tag);

        if (tag == SELECTION_INDEX) {
            //display selection list
        } else if (tag == RADIOS_INDEX) {
            //display content view (favourites)
        }

        //view.setSelected(true);
        //String name = showsList.get(tag);
        //Log.d(TAG, "name is : " + name);
    }

    @Override
    public void onTopEmptyRegionClick() {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        Log.d(TAG, "data changed");
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, "DATA CHANGED!!!", Toast.LENGTH_SHORT).show();
            }
        });
        /*
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/count") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    //updateCount(dataMap.getInt(COUNT_KEY));
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
        */

    }

    private void sendMessage(final String path, final String text) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for (Node node : nodes.getNodes()) {
                    Log.d(TAG, "sending message");
                    Wearable.MessageApi.sendMessage(mApiClient, node.getId(), path, text.getBytes());
                }
            }
        }).start();
    }
}
