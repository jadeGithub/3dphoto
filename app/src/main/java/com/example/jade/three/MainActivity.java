package com.example.jade.three;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    private SeekBar bar;
    private int imageCout = 100;
    private ThreeSurfaceView mSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSurfaceView = this.findViewById(R.id.three_surfaceview);
        bar = this.findViewById(R.id.sb_player_seek_bar);
        //imageCout = mSurfaceView.getImageCount();
        bar.setMax(imageCout);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i("jade","progress :"+progress/10);
                mSurfaceView.drawImageWithIndex(progress/10);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
