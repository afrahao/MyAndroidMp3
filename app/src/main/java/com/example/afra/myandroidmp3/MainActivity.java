package com.example.afra.myandroidmp3;

import android.content.Intent;
import android.media.Image;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;



public class MainActivity extends AppCompatActivity {

    private SeekBar mSeekBar;
    private Thread myThread = null;
    //private String path = "http://172.27.63.233:8080/001.mp3";
    private String path = "http://abv.cn/music/光辉岁月.mp3";
    private Player mPlayer;
    private TextView mEditText;
    private boolean playOrPause = false;
    private boolean firstPlay = true;
    private List<Music> musicList = null;
    private int position = 0;

    public static final String CTL_ACTION = "com.trampcr.action.CTL_ACTION";
    public static final String UPDATE_ACTION = "com.trampcr.action.UPDATE_ACTION";

    /*private static final String URL = "jdbc:mysql://127.0.0.1:3306/infodb";
    private static final String URL ="jdbc:mysql://47.95.10.11:3306/rrs_db";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";*/
    private Connection conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mEditText = (TextView) findViewById(R.id.music_name);
        mEditText.setText(path);

        musicList = new ArrayList<Music>();

        mPlayer = new Player(mSeekBar);

        final ImageView mPlay = (ImageView) findViewById(R.id.play);
        ImageView mNext = (ImageView) findViewById(R.id.next);
        ImageView mPrevious = (ImageView) findViewById(R.id.previous);

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               InitMusicList();
                myThread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                       //System.out.println(musicList.get(position).getMUrl());
                        mPlayer.playUrl(musicList.get(position).getMUrl());
                    }
                });

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

    public void InitMusicList()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                conn = Util.getConnection();
                System.out.println("All users info:");
                Connection conn = null;
                PreparedStatement psmt = null;
                ResultSet rs = null;
                conn = Util.getConnection();
                StringBuffer sql = new StringBuffer();
                sql.append("select * from music");
                try {
                    psmt = conn.prepareStatement(sql.toString());
                    rs = psmt.executeQuery();
                    while (rs.next()) {
                        Music music = new Music();
                        music.setMName(rs.getString("mName"));
                        music.setMUrl(rs.getString("mUrl"));
                        music.setMArtist(rs.getString("mArtist"));
                        musicList.add(music);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void firstPlay(View view) {
        myThread.start();
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
