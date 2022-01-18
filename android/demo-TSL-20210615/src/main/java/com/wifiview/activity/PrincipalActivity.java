package com.wifiview.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.molink.demotsl.R;
import com.wifiview.MainActivity;
import com.wifiview.MainApplication;
import com.wifiview.config.Apps;
import com.wifiview.config.HandlerParams;
import com.wifiview.images.SurfaceView;
import com.wifiview.nativelibs.CmdSocket;
import com.wifiview.nativelibs.NativeLibs;
import com.wifiview.nativelibs.StreamSelf;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PrincipalActivity extends CheckPermissonActivity {
    private static final String TAG = "MainActivity";
    private SurfaceView mSurfaceView;

    private StreamSelf mStreamSelf;

    private int screenWidth;
    private int screenHeight;

    private int recTime;
    private int second;
    private Timer showTimer;
    private ShowTimerTask showTimerTask;

    private ImageView iv_Main_Background;
    private ImageView iv_Main_TakePhoto;
    private ImageView iv_Main_TakeVideo;
    private ImageView iv_Main_Playback;
    private ImageView iv_Main_Setting;
    private TextView txt_Main_RecordTime;
    private LinearLayout layout_Left_Menubar;

    private long mKeyTime;
    private boolean isShowView = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        Log.d(TAG, screenWidth + "*" + screenHeight);

        mSurfaceView = (SurfaceView) findViewById(R.id.mSurfaceView);
        mSurfaceView.setOnClickListener(clickListener);

        mStreamSelf = new StreamSelf(getApplicationContext(), mSurfaceView, handler);

        widgetInit();
        showTimer = new Timer();
        Apps.cmdSocket.setHandler(handler);
    }

    private void widgetInit() {
        iv_Main_Background = (ImageView) findViewById(R.id.iv_main_background);
        iv_Main_TakePhoto = (ImageView) findViewById(R.id.iv_Main_TakePhoto);
        iv_Main_TakePhoto.setOnClickListener(clickListener);
        iv_Main_TakeVideo = (ImageView) findViewById(R.id.iv_Main_TakeVideo);
        iv_Main_TakeVideo.setOnClickListener(clickListener);
        iv_Main_Playback = (ImageView) findViewById(R.id.iv_Main_Playback);
        iv_Main_Playback.setOnClickListener(clickListener);
        iv_Main_Setting = (ImageView) findViewById(R.id.iv_Main_Setting);
        iv_Main_Setting.setOnClickListener(clickListener);
        txt_Main_RecordTime = (TextView) findViewById(R.id.txt_Record_Time);
        layout_Left_Menubar = (LinearLayout) findViewById(R.id.layout_Left_Menubar);

        RelativeLayout.LayoutParams rlp;
        int h = screenHeight;
        rlp = new RelativeLayout.LayoutParams(h, h);
        rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mSurfaceView.setLayoutParams(rlp);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mStreamSelf.startStream();
        Apps.cmdSocket.startRunKeyThread();
    }


    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.mSurfaceView:
                    if (isShowView) {
                        isShowView = false;
                        layout_Left_Menubar.setVisibility(View.INVISIBLE);
                    } else {
                        isShowView = true;
                        layout_Left_Menubar.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.iv_Main_TakePhoto:
                    mStreamSelf.takePhoto();
                    break;
                case R.id.iv_Main_TakeVideo:
                    mStreamSelf.takeRecord();
                    break;
                case R.id.iv_Main_Playback:
                    startIntent(PrincipalActivity.this, PlaybackActivity.class);
                    break;
                case R.id.iv_Main_Setting:
                    int ver = NativeLibs.nativeCmdGetRemoteVersion(Apps.defaultIpAddr);
                    Log.e(TAG, "version:" + ver);
                    startIntent(PrincipalActivity.this, SetActivity.class);
                    break;

                default:
                    break;
            }
        }
    };

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case HandlerParams.RECORD_START:
                    initTime();
                    txt_Main_RecordTime.setVisibility(View.VISIBLE);
                    iv_Main_TakeVideo.setImageResource(R.drawable.main_takevideo_recording);
                    txt_Main_RecordTime.setText("REC:" + String.format("%02d", 0) + ":" + String.format("%02d", 0) + ":" + String.format("%02d", 0));
                    startShowTimer();
                    break;
                case HandlerParams.RECORD_STOP:
                    clearTime();
                    iv_Main_TakeVideo.setImageResource(R.drawable.main_takevideo_state);
                    txt_Main_RecordTime.setVisibility(View.INVISIBLE);
                    break;
                case HandlerParams.SDCARD_FULL:
                    Toast.makeText(PrincipalActivity.this, R.string.SD_card_full, Toast.LENGTH_LONG).show();
                    break;
                case HandlerParams.UPDATE_RECORDTIME:
                    setTime();
                    break;
                case HandlerParams.HAS_GET_STREAM:
                    iv_Main_Background.setVisibility(View.GONE);
                    break;
                case HandlerParams.REMOTE_TAKE_PHOTO:
                    mStreamSelf.takePhoto();
                    break;
                case HandlerParams.REMOTE_TAKE_RECORD:
                    mStreamSelf.takeRecord();
                    break;

                default:
                    break;
            }
            return true;
        }
    });

    private void setTime() {
        Time t = new Time();
        t.setToNow();
        int s = t.second;
        if (second != s) {
            second = s;
            recTime++;
            int time_h = recTime / 3600;
            int time_m = (recTime % 3600) / 60;
            int time_s = recTime % 60;
            txt_Main_RecordTime.setText("REC:" + String.format("%02d", time_h) + ":" + String.format("%02d", time_m) + ":" + String.format("%02d", time_s));
        }

    }

    private void initTime() {
        recTime = 0;
        Time time = new Time();
        time.setToNow();
        second = time.second;
    }

    private void clearTime() {
        second = 0;
    }

    // what the timer task do
    private class ShowTimerTask extends TimerTask {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            handler.sendEmptyMessage(HandlerParams.UPDATE_RECORDTIME);
        }
    }

    // start the timer task
    private void startShowTimer() {
        if (showTimer != null) {
            if (showTimerTask != null) {
                showTimerTask.cancel();
            }
        }

        showTimerTask = new ShowTimerTask();
        showTimer.schedule(showTimerTask, 500, 500); // 500ms one time
    }

    ;

    private void startIntent(Activity activity, Class<?> cls) {
        finish();
        Intent intent = new Intent(activity, cls);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mKeyTime) > 2000) {
                mKeyTime = System.currentTimeMillis();
                Toast.makeText(this, R.string.txt_select_onemorepress, Toast.LENGTH_SHORT).show();
            } else {
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        mStreamSelf.stopStream();
        Apps.cmdSocket.stopRunKeyThread();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mStreamSelf.destroy();
    }
}
