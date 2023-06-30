package com.example.finant;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

    private ProgressBar pgb;

    private  int pgbstate = 0;

    private Handler pHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this line hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        pgb = (ProgressBar) findViewById(R.id.progressBar);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(pgbstate < 100){
                    pgbstate ++;
                    android.os.SystemClock.sleep(20);
                    pHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            pgb.setProgress(pgbstate);
                        }
                    });
                }
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        }).start();

    }
}