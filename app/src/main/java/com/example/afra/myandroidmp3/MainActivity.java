package com.example.afra.myandroidmp3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView mStart;
    private ImageView mNext;
    private ImageView mPrevious;
    private ImageView mStop;
    private TextView mMusicName;
    private TextView mSongerName;
    private ActivityReceiver mActivityReceiver;
    private List<Music> musicList = new ArrayList<Music>();
    public static final String CTL_ACTION = "CTL_ACTION";
    public static final String UPDATE_ACTION = "UPDATE_ACTION";

    //定义音乐播放状态，0x11代表没有播放，0x12代表正在播放，0x13代表暂停
    int status = 0x11;
    String[] musicNames = new String[]{"完美生活", "那一年", "故乡"};
    String[] songerNames = new String[]{"许巍", "许巍", "许巍"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStart = (ImageView) findViewById(R.id.play);
        mNext = (ImageView) findViewById(R.id.next);
        mStop = (ImageView) findViewById(R.id.download);
        mPrevious = (ImageView) findViewById(R.id.previous);
        mMusicName = (TextView) findViewById(R.id.music_name);
        mSongerName = (TextView) findViewById(R.id.stateText);

        mStart.setOnClickListener(this);
        mStop.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mPrevious.setOnClickListener(this);

        mActivityReceiver = new ActivityReceiver();
        //创建IntentFilter
        IntentFilter filter = new IntentFilter();
        //指定BroadcastReceiver监听的Action
        filter.addAction(UPDATE_ACTION);
        //注册BroadcastReceiver
        registerReceiver(mActivityReceiver, filter);

        Intent intent = new Intent(MainActivity.this, MusicService.class);
        //启动后台Service
        startService(intent);
    }
    public class ActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取Intent中的update消息，update代表播放状态
            int update = intent.getIntExtra("update", -1);
            //获取Intent中的current消息，current代表当前正在播放的歌曲
            int current = intent.getIntExtra("current", -1);
            if (current >= 0){
                mMusicName.setText(musicNames[current]);
                mSongerName.setText(songerNames[current]);
            }
            switch (update){
                case 0x11:
                    mStart.setBackgroundResource(R.drawable.play);
                    status = 0x11;
                    break;
                //控制系统进入播放状态
                case 0x12:
                    //在播放状态下设置使用暂停图标
                    mStart.setBackgroundResource(R.drawable.pause);
                    status = 0x12;
                    break;
                case 0x13:
                    //在暂停状态下设置使用播放图标
                    mStart.setBackgroundResource(R.drawable.play);
                    status = 0x13;
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(CTL_ACTION);
        switch (v.getId()){
            case R.id.play:
                intent.putExtra("control", 1);
                break;
            case R.id.next:
                intent.putExtra("control", 3);
                break;
            case R.id.previous:
                intent.putExtra("control", 4);
                break;
            case R.id.download:
                intent.putExtra("control", 2);
                break;
        }
        //发送广播，将被Service中的BroadcastReceiver接收到
        sendBroadcast(intent);
    }

    public void InitMusicList()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection conn = Util.getConnection();
                System.out.println("All users info:");

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
    /*public void firstPlay(View view) {
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
    }*/

}
