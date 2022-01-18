package com.wifiview.activity;

import com.molink.demotsl.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class LoadingActivity extends Activity
{
	public static final String TAG = "LoadingActivity";
	private static final int JUMP_MAIN = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);

		startJumpThread();

	}

	private void startJumpThread()
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				try
				{
					Thread.sleep(1500);
					handler.sendEmptyMessage(JUMP_MAIN);
					Log.e(TAG, "ThreadSleep");
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	private Handler handler = new Handler(new Handler.Callback()
	{

		@Override
		public boolean handleMessage(Message msg)
		{	Log.e(TAG, "handleMessage:" + msg);
			// TODO Auto-generated method stub
			if (msg.what == JUMP_MAIN)
			{
				finish();
				Intent intent = new Intent(LoadingActivity.this, PrincipalActivity.class);
				startActivity(intent);
			}
			return true;
		}
	});
}
