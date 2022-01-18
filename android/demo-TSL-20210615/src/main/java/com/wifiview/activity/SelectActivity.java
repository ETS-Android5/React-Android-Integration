package com.wifiview.activity;

import com.molink.demotsl.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

public class SelectActivity extends Activity
{
	public static final String TAG = "SelectActivity";
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// TODO Auto-generated method stub
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:

			break;
		case MotionEvent.ACTION_UP:
			finish();
			Intent intent = new Intent(SelectActivity.this, PrincipalActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}
	
	
}
