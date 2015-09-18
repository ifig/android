package horizon.radio.orange.com.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;


import java.util.ArrayList;


public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button textButton;
    private Context mContext;
    private static MainActivity mMainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mContext = this;
        Log.d(TAG, "on create");

        mMainActivity = this;
        Moto360Service.getInstance();
        setContentView(R.layout.activity_main);

        textButton = (Button) findViewById(R.id.textButton);
        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Moto360Service.getInstance().sendMessage("/myPath", "this is a message");
            }
        });
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static MainActivity getInstance() {
        return mMainActivity;
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "ON RESUME");
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
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}