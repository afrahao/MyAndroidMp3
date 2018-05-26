package com.example.afra.myandroidmp3;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.sql.Connection;
import java.sql.SQLException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private SeekBar mSeekBar;
    //private String path = "http://172.27.63.233:8080/001.mp3";
    private String path = "http://abv.cn/music/光辉岁月.mp3";
    private Player mPlayer;
    private TextView mEditText;
    private boolean playOrPause = false;
    private boolean firstPlay = true;


    //private static final String URL = "jdbc:mysql://" + "172.27.63.233:8080" + "/infodb";
    private static final String URL ="jdbc:mysql://47.95.10.11:3306/rrs_db";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";
    private Connection conn ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mEditText = (TextView) findViewById(R.id.music_name);
        mEditText.setText(path);

        mPlayer = new Player(mSeekBar);

        final ImageView mPlay = (ImageView) findViewById(R.id.play);
        ImageView mNext = (ImageView) findViewById(R.id.next);
        ImageView mPrevious = (ImageView) findViewById(R.id.previous);




        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        conn = Util.openConnection(URL, USER, PASSWORD);
                        System.out.println("All users info:");
                        Util.query(conn, "select * from business limit 1,5");
                    }
                }).start();


            }
        });

        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firstPlay)
                {
                    firstPlay(v);
                    firstPlay = !firstPlay;
                }
                else
                {
                    if (!playOrPause) {
                        play(v);
                        playOrPause = !playOrPause;
                        mPlay.setBackgroundResource(R.drawable.pause);
                    }
                    else if (playOrPause) {
                        pause(v);
                        playOrPause = !playOrPause;
                        mPlay.setBackgroundResource(R.drawable.play);
                    }
                }
            }
        });
    }

    public void firstPlay(View view) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                mPlayer.playUrl("http://172.27.63.233:8080/001.mp3");
            }
        }).start();
    }

    public void play(View view) {
        mPlayer.play();
    }

    public void pause(View view) {
        mPlayer.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.stop();
    }
}
