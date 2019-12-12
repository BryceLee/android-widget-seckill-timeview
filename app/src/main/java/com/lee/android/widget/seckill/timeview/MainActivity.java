package com.lee.android.widget.seckill.timeview;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SeckillTimeView timeView=(SeckillTimeView) findViewById(R.id.timeview);
        long l = System.currentTimeMillis() + 1235;
        timeView.setEndTime(l);
    }
}
