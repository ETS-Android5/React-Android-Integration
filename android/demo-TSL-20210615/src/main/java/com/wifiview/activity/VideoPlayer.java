package com.wifiview.activity;

import java.io.File;

import com.molink.demotsl.R;
import com.wifiview.images.PlaybackSurfaceView;
import com.wifiview.nativelibs.NativeLibs;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class VideoPlayer extends Activity {
    private PlaybackSurfaceView mSurfaceView;
    private TextView tv_Player_Time;
    private TextView tv_Player_Title;
    private ImageView iv_Player_Play;
    private SeekBar seekBar_Player;
    private RelativeLayout layout_Top_Menubar;
    private RelativeLayout layout_Bottom_Menubar;

    private boolean isOptBarShow = true;
    private boolean isPlaying = false;

    private Thread playThread;
    private Thread audioThread;
    private boolean isThreadStop = false;

    private static final int GET_VIDEO_PARAMS = 0;
    private static final int UPDATE_FRAME = 1;
    private static final int PLAYER_PAUSE = 2;

    public static final int STREAM_AUDIO = 1;
    public static final int STREAM_VIDEO = 2;

    private double videoPlayNowTime = 0;    //AVI播放的当期时间

    private String videoName;

    private String videoTimeString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_player);

        videoName = this.getIntent().getStringExtra("videoPath").replace(".jpg", ".avi");

        initWidget();
        initVideo();
    }

    private void initWidget() {
        mSurfaceView = (PlaybackSurfaceView) findViewById(R.id.playSurfaceView);
        mSurfaceView.setOnClickListener(clickListener);
        layout_Top_Menubar = (RelativeLayout) findViewById(R.id.layout_player_top_menubar);
        layout_Bottom_Menubar = (RelativeLayout) findViewById(R.id.layout_player_bottom_menubar);
        iv_Player_Play = (ImageView) findViewById(R.id.iv_player_play);
        iv_Player_Play.setOnClickListener(clickListener);
        tv_Player_Time = (TextView) findViewById(R.id.tv_player_bottom_time);
        tv_Player_Title = (TextView) findViewById(R.id.tv_player_top_title);
        tv_Player_Title.setText(new File(videoName).getName());
        seekBar_Player = (SeekBar) findViewById(R.id.seekbar_player_play);
        seekBar_Player.setOnSeekBarChangeListener(seekBarChangeListener);
    }

    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.playSurfaceView:
                    if (isOptBarShow) {
                        isOptBarShow = false;
                        layout_Top_Menubar.setVisibility(View.GONE);
                        layout_Bottom_Menubar.setVisibility(View.GONE);
                    } else {
                        isOptBarShow = true;
                        layout_Top_Menubar.setVisibility(View.VISIBLE);
                        layout_Bottom_Menubar.setVisibility(View.VISIBLE);
                    }
                    break;

                case R.id.iv_player_play:
                    if (isPlaying) {
                        isPlaying = false;
                        iv_Player_Play.setImageResource(R.drawable.player_play);
                    } else {
                        isPlaying = true;
                        iv_Player_Play.setImageResource(R.drawable.player_pause);
                    }
                    break;

                default:
                    break;
            }
        }
    };

    private OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
            int progress = seekBar.getProgress();

            videoPlayNowTime = progress;
            isPlaying = true;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
            isPlaying = false;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // TODO Auto-generated method stub

        }
    };

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case GET_VIDEO_PARAMS: {
                    int videotime = (Integer) msg.obj;
                    seekBar_Player.setProgress(0);
                    seekBar_Player.setMax(videotime);
                }
                break;

                case UPDATE_FRAME: {
                    int timestamp = (Integer) msg.obj;
                    seekBar_Player.setProgress(timestamp);
                    tv_Player_Time.setText(doFormatTimeString(timestamp) + "/" + videoTimeString);
                }
                break;
                case PLAYER_PAUSE: {
                    isPlaying = false;
                    iv_Player_Play.setImageResource(R.drawable.player_play);
                }
                break;

                default:
                    break;
            }
            return true;
        }
    });

    private void initVideo() {
        isThreadStop = false;

        if (videoName != null) {
            playVideoAVI();

            isPlaying = true;
            iv_Player_Play.setImageResource(R.drawable.player_pause);
        }
    }

    private String doFormatTimeString(int time) {
        String timeString;

        int time_h = time / 3600;
        int time_m = (time % 3600) / 60;
        int time_s = time % 60;
        if (time_h > 0) {
            timeString = String.format("%02d", time_h) + ":" + String.format("%02d", time_m) + ":"
                    + String.format("%02d", time_s);
        } else {
            timeString = String.format("%02d", time_m) + ":" + String.format("%02d", time_s);
        }

        return timeString;
    }

    private void sendHandleMsg(int type, Object obj) {
        Message msg = handler.obtainMessage();
        msg.what = type;
        msg.obj = obj;
        handler.sendMessage(msg);
    }

    /**
     * 播放AVI的接口
     */
    private void playVideoAVI() {
        if (playThread == null || !playThread.isAlive()) {
            playThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if (!NativeLibs.nativeAVIOpen(videoName)) {
                        handler.sendEmptyMessage(PLAYER_PAUSE);
                        return;
                    }

                    int index = 0;
                    double videoTotalTime = NativeLibs.nativeAVIGetTotalTime();
                    double curNowTime = (double) System.currentTimeMillis() / (double) 1000;
                    double offsetTime; // 两个当前时间的差值

                    videoTimeString = doFormatTimeString((int) videoTotalTime);
                    sendHandleMsg(GET_VIDEO_PARAMS, (int) videoTotalTime);

                    while (!isThreadStop) {
                        if (isPlaying) {
                            if (videoPlayNowTime >= videoTotalTime || videoPlayNowTime < 0) {
                                msleep(1000); // 1s后才开始重头播放视频
                                videoPlayNowTime = 0;
                                curNowTime = (double) System.currentTimeMillis() / (double) 1000;
                                offsetTime = 0;
                            }

                            byte[] data = NativeLibs.nativeAVIGetFrameAtTime(videoPlayNowTime);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            if (bitmap != null) {
                                mSurfaceView.SetBitmap(bitmap);
                                if (index++ > 5) {
                                    sendHandleMsg(UPDATE_FRAME, (int) videoPlayNowTime);
                                    index = 0;
                                }
                            }

                            offsetTime = (double) System.currentTimeMillis() / (double) 1000 - curNowTime;
                            curNowTime = (double) System.currentTimeMillis() / (double) 1000;
                            videoPlayNowTime += offsetTime;

                            msleep(25);
                        } else {
                            msleep(25);
                            curNowTime = (double) System.currentTimeMillis() / (double) 1000;
                        }
                    }

                    NativeLibs.nativeAVIClose();
                }
            });
            playThread.start();
        }
    }

    private void stopVideo() {
        isThreadStop = true;
    }

    private void msleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void onBackButtonClick(View pressed) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        stopVideo();
        finish();
    }

}
