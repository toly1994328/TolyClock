package com.toly1994.tolyclock;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    /**
     * 新建Handler
     */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mView.invalidate();//处理：刷新视图
        }
    };

    private View mView;
    private Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        mView = findViewById(R.id.id_toly_clock);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);//发送消息
            }
        };
        //定时任务
        timer.schedule(timerTask, 0, 1000);
    }
}
