package com.wifiview.config;

import com.molink.demotsl.R;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class Media
{
	private MediaPlayer mpShtter = null;
	private final String TAG = "Media";

	public Media(Context context)
	{
		mpShtter = MediaPlayer.create(context, R.raw.shutter);
	}

	public void playShutter()
	{
		try
		{
			if (mpShtter != null)
			{
				mpShtter.stop();
			}
			mpShtter.prepare();
			mpShtter.start();
			Log.e(TAG, "media play shutter!");
		} catch (Exception e)
		{
			Log.e(TAG, "music error");
			e.printStackTrace();
		}
	}

}
