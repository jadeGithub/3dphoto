package com.example.jade.three;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;


/**
 * 这是个线程测试类，和3d没有关系
 */
public class ThreadTestActivity extends Activity {
    Handler handler;
    Handler handler1;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("chan", "onCreate: ");
        //--------------测试子线程给主线程发消息-----------------------
        frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        Button bt = new Button(this);
        bt.setText("点击触发子线程1给主线程发消息：更新ui");
        bt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        //子线程发消息
                        handler.sendEmptyMessage(2);
                    }
                }.start();

            }
        });
        frameLayout.addView(bt);
        this.setContentView(frameLayout);
        //主线程处理子线程的消息
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                frameLayout.setBackgroundColor(getResources().getColor(R.color.colorAccent));

            }
        };

        //--------------测试主线程给子线程1发消息-----------------------
        final Thread thread1 = new HandlerThread("thread1") {
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                notifyAll();
                Log.i("chan", "thread1 looper =" + getLooper().toString());
                Looper.loop();
            }
        };
        thread1.start();
        //子线程1的handler
        handler1 = new Handler(((HandlerThread) thread1).getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1){
                    Toast.makeText(ThreadTestActivity.this, "主线程发来的消息,发给子线程，当前线程是 " + Thread.currentThread().getName(), Toast.LENGTH_SHORT).show();
                }else if(msg.what ==3){
                    Toast.makeText(ThreadTestActivity.this, "子线程2发来的消息,发给子线程，当前线程是 " + Thread.currentThread().getName(), Toast.LENGTH_SHORT).show();

                }
            }
        };
        //主线程在发消息
        handler1.sendEmptyMessage(1);


        //--------------测试子线程2给子线程1发消息-----------------------
        new Thread("thread2") {
            @Override
            public void run() {
                super.run();
               // Toast.makeText(ThreadTestActivity.this, "子线程2是 " + Thread.currentThread().getName(), Toast.LENGTH_SHORT).show();
                handler1.sendEmptyMessageDelayed(3,5000);
            }
        }.start();


    }
}
