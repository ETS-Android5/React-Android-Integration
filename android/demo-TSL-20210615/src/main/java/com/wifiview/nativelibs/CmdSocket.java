package com.wifiview.nativelibs;


import java.util.ArrayList;

import com.wifiview.config.Apps;
import com.wifiview.config.HandlerParams;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

public class CmdSocket
{
	private static final String TAG = "CmdSocket";
	public static final int CMD_SET_WIFI_NAME = 0x0001;
	public static final int CMD_SET_WIFI_PASSWORD = 0x0002;
	public static final int CMD_SET_WIFI_CLEAR_PASSWORD = 0x0003;
	public static final int CMD_SET_REBOOT = 0x0004;
	public static final int CMD_SET_CAMERA_RESOLUTION = 0x0008;
	public static final int CMD_GET_CAMERA_RESOLUTION = 0x0009;

	private Handler handler;
	private Context context;

    public static final int REMOTE_VALUE_PHOTO = 1;
    public static final int REMOTE_VALUE_VIDEO = 2;

    private boolean keyThreadRunning = false;

	private ArrayList<String> params = new ArrayList<String>();

	public CmdSocket(Context context)
	{
		this.context = context;
	}

	public void setHandler(Handler handler) {
        this.handler = handler;
    }

	public void sendCommand(final CmdParams mCmdParams) {
		StreamSelf.EXECUTER.execute(new Runnable()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				int ret;
				switch (mCmdParams.cmdType) {
					case CMD_SET_WIFI_NAME:
					{
						ret = NativeLibs.nativeCmdSetName(Apps.defaultIpAddr, mCmdParams.wifiName);
						if (ret > 0) {
							handler.sendEmptyMessage(HandlerParams.SET_OK);
						} else {
							handler.sendEmptyMessage(HandlerParams.SET_FAIL);
						}
					}
					break;
					case CMD_SET_WIFI_PASSWORD:
					{
						ret = NativeLibs.nativeCmdSetPassword(Apps.defaultIpAddr, mCmdParams.wifiPass);
						if (ret > 0) {
							handler.sendEmptyMessage(HandlerParams.SET_OK);
						} else {
							handler.sendEmptyMessage(HandlerParams.SET_FAIL);
						}
					}
					break;
					case CMD_SET_WIFI_CLEAR_PASSWORD:
					{
						ret = NativeLibs.nativeCmdClearPassword(Apps.defaultIpAddr);
						if (ret > 0) {
							handler.sendEmptyMessage(HandlerParams.SET_OK);
						} else {
							handler.sendEmptyMessage(HandlerParams.SET_FAIL);
						}
					}
					break;
					case CMD_SET_REBOOT:
					{
						ret = NativeLibs.nativeCmdSendReboot(Apps.defaultIpAddr);
						Log.d(TAG, "ret:" + ret);
						if (ret > 0) {
							handler.sendEmptyMessage(HandlerParams.SET_OK);
						} else {
							handler.sendEmptyMessage(HandlerParams.SET_FAIL);
						}
					}
					break;
					case CMD_SET_CAMERA_RESOLUTION:
					{
						ret = NativeLibs.nativeCmdSetResolution(Apps.defaultIpAddr, mCmdParams.width, mCmdParams.height, 30);
						if (ret > 0) {
							handler.sendEmptyMessage(HandlerParams.SET_OK);
						} else {
							handler.sendEmptyMessage(HandlerParams.SET_FAIL);
						}
					}
					break;
					case CMD_GET_CAMERA_RESOLUTION:
					{
						params.clear();
						byte[] buf_tmp = NativeLibs.nativeCmdGetResolution(Apps.defaultIpAddr);
						if (buf_tmp != null && buf_tmp.length > 0) {
							int mWidth, mHeight, fps;
							int count = buf_tmp[0];
							for (int i = 0; i < count; i++) {
								mWidth = byteToInt(buf_tmp[i * 5 + 1]) + (buf_tmp[i * 5 + 2] << 8);
								mHeight = byteToInt(buf_tmp[i * 5 + 3]) + (buf_tmp[i * 5 + 4] << 8);
								fps = buf_tmp[i * 5 + 5];
								params.add(mWidth + "*" + mHeight);
								Log.e(TAG, Integer.toString(mWidth) + "*" + mHeight + " " + fps);
							}
							// update the spinner
							Message msg = new Message();
							msg.obj = params;
							msg.what = HandlerParams.UPDATE_RESOLUTION;
							handler.sendMessage(msg);
						}
					}
					break;
					default:
					break;
				}
			}
		});
	}

	private static int byteToInt(byte x) {
		return x >= 0 ? (int) x : (int) (x + 256);
	}


	public void stopSocket() {
        keyThreadRunning = false;
	}

	public class CmdParams
	{
		public int cmdType;
		public String wifiName;
		public String wifiPass;
		public int width;
		public int height;
		public int video_format;
	}


    /**
     * 按键&串口透传线程demo
     */
    public void startRunKeyThread() {
        keyThreadRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int type;
                int key_value;

                while(keyThreadRunning) {

                        Log.e(TAG, "Device support [KEY]");

                        while (keyThreadRunning) {
                            key_value = NativeLibs.nativeCmdGetRemoteKey(Apps.defaultIpAddr);
                            if (key_value == REMOTE_VALUE_PHOTO)
                                handler.sendEmptyMessage(HandlerParams.REMOTE_TAKE_PHOTO);
                            else if (key_value == REMOTE_VALUE_VIDEO)
                                handler.sendEmptyMessage(HandlerParams.REMOTE_TAKE_RECORD);

                            SystemClock.sleep(200);
                        }
                }
            }
        }).start();
    }

    public void stopRunKeyThread() {
        keyThreadRunning = false;
    }
}
