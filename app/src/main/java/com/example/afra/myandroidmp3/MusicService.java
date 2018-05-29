package com.example.afra.myandroidmp3;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.SeekBar;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by afra on 2018/5/27.
 */

public class MusicService extends Service {

    MyReceiver serviceReceiver;
    AssetManager mAssetManager;
    String[] musics = new String[]{"http://172.27.63.233:8080/music/havana.mp3", "thttp://172.27.63.233:8080/music/Problem.mp3", "http://172.27.63.233:8080/music/Shape of You.mp3"};
    MediaPlayer mMediaPlayer;
    private SeekBar seekBar; // 拖动条
    private Timer mTimer = new Timer(); // 计时器

    int status = 0x11;
    int current = 0; // 记录当前正在播放的音乐

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAssetManager = getAssets();
        serviceReceiver = new MyReceiver();
        //创建IntentFilter
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.CTL_ACTION);
        registerReceiver(serviceReceiver, filter);
        //创建MediaPlayer
        mMediaPlayer = new MediaPlayer();
        //为MediaPlayer播放完成事件绑定监听器
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                current++;
                if (current >= 3) {
                    current = 0;
                }
                //发送广播通知Activity更改文本框
                Intent sendIntent = new Intent(MainActivity.UPDATE_ACTION);
                sendIntent.putExtra("current", current);
                //发送广播，将被Activity中的BroadcastReceiver接收到
                sendBroadcast(sendIntent);
                //准备并播放音乐
                prepareAndPlay(musics[current]);
            }
        });

    }



    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int control = intent.getIntExtra("control", -1);
            switch (control){
                case 1: // 播放或暂停
                    //原来处于没有播放状态
                    if (status ==0x11){
                        //准备播放音乐
                        prepareAndPlay(musics[current]);
                        status = 0x12;
                    }
                    //原来处于播放状态
                    else if (status == 0x12){
                        //暂停
                        mMediaPlayer.pause();
                        status = 0x13; // 改变为暂停状态
                    }
                    //原来处于暂停状态
                    else if (status == 0x13){
                        //播放
                        mMediaPlayer.start();
                        status = 0x12; // 改变状态
                    }
                    break;
                //停止声音
                case 2:
                    //如果原来正在播放或暂停
                    if (status == 0x12 || status == 0x13){
                        //停止播放
                        mMediaPlayer.stop();
                        status = 0x11;
                    }
                    break;
                case 3:
                    current++;
                    if (current >= 3) {
                        current = 0;
                    }
                    prepareAndPlay(musics[current]);
                    status = 0x12;
                    break;
                case 4:
                    current--;
                    if (current < 0) {
                        current = 3;
                    }
                    prepareAndPlay(musics[current]);
                    status = 0x12;
                    break;

            }
            //广播通知Activity更改图标、文本框
            Intent sendIntent = new Intent(MainActivity.UPDATE_ACTION);
            sendIntent.putExtra("update", status);
            sendIntent.putExtra("current", current);
            //发送广播，将被Activity中的BroadcastReceiver接收到
            sendBroadcast(sendIntent);
        }
    }

    private void prepareAndPlay(String music) {
        try {
            mMediaPlayer.reset();
            //使用MediaPlayer加载指定的声音文件
            mMediaPlayer.setDataSource(music);
            mMediaPlayer.prepare(); // 准备声音
            mMediaPlayer.start(); // 播放
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}