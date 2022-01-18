package com.wifiview.activity;

import java.util.ArrayList;

import com.molink.demotsl.R;
import com.wifiview.config.Apps;
import com.wifiview.config.HandlerParams;
import com.wifiview.nativelibs.CmdSocket;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

public class SetActivity extends Activity
{
	private static final String TAG = "SettingActivity";

	private ImageView iv_Return;
	private Button btn_Change_Name;
	private Button btn_Change_Password;
	private Button btn_Reboot;
	private Button btn_Clear_Password;
	private EditText edt_Change_Name;
	private EditText edt_Change_Password;
	private Spinner spin_Resolution;
	private Button btn_Change_Resolution;

	private Button btn_Jump_Setting;

	private ArrayAdapter<String> mAdapter;

    private ArrayList<String> mResParams = new ArrayList<String>();
	private CmdSocket.CmdParams mCmdParams;
    private boolean hasGerResolution = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

        Apps.cmdSocket.setHandler(handler);
		widgetInit();

		initSpinner();
	}

	private void widgetInit() {
		// find the widget
		Log.d(TAG, "widget init now...");
		iv_Return = (ImageView) findViewById(R.id.iv_Return);
		iv_Return.setOnClickListener(clickListener);
		btn_Change_Name = (Button) findViewById(R.id.btn_Change_Name);
		btn_Change_Name.setOnClickListener(clickListener);
		btn_Change_Password = (Button) findViewById(R.id.btn_Change_Password);
		btn_Change_Password.setOnClickListener(clickListener);
		btn_Reboot = (Button) findViewById(R.id.btn_Reboot);
		btn_Reboot.setOnClickListener(clickListener);
		btn_Clear_Password = (Button) findViewById(R.id.btn_Clear_Password);
		btn_Clear_Password.setOnClickListener(clickListener);
		edt_Change_Name = (EditText) findViewById(R.id.edt_Change_Name);
		edt_Change_Password = (EditText) findViewById(R.id.edt_Change_Password);
		spin_Resolution = (Spinner) findViewById(R.id.spin_resolution);
		btn_Change_Resolution = (Button) findViewById(R.id.btn_Change_Resolution);
		btn_Change_Resolution.setOnClickListener(clickListener);

		btn_Jump_Setting = (Button) findViewById(R.id.btn_jump_setting);
		btn_Jump_Setting.setOnClickListener(clickListener);
	}

	private void initSpinner() {
        mAdapter = new ArrayAdapter<>(SetActivity.this, android.R.layout.simple_spinner_item, mResParams);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_Resolution.setAdapter(mAdapter);
        spin_Resolution.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                    mCmdParams = Apps.cmdSocket.new CmdParams();
                    mCmdParams.cmdType = CmdSocket.CMD_GET_CAMERA_RESOLUTION;
                    Apps.cmdSocket.sendCommand(mCmdParams);
                    Thread.sleep(300);
                }catch (InterruptedException e) {

                }
            }
        });
	}


	private OnClickListener clickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
				case R.id.iv_Return:
					onBackPressed();
				break;
				case R.id.btn_Change_Name:
					String wifiName = edt_Change_Name.getText().toString();
					if (wifiName.length() > 0) {
						mCmdParams.cmdType = CmdSocket.CMD_SET_WIFI_NAME;
						mCmdParams.wifiName = wifiName;
                        Apps.cmdSocket.sendCommand(mCmdParams);
					} else {
						WIFI_Dialog(SetActivity.this.getString(R.string.str_set_namenull));
					}
				break;
				case R.id.btn_Reboot:
					mCmdParams.cmdType = CmdSocket.CMD_SET_REBOOT;
                    Apps.cmdSocket.sendCommand(mCmdParams);
				break;
				case R.id.btn_Change_Password:
					String password = edt_Change_Password.getText().toString();
					if (password.length() >= 8) {
						mCmdParams.cmdType = CmdSocket.CMD_SET_WIFI_PASSWORD;
						mCmdParams.wifiPass = password;
                        Apps.cmdSocket.sendCommand(mCmdParams);
					} else {
						WIFI_Dialog(SetActivity.this.getString(R.string.str_set_lessthaneight));
					}
				break;
				case R.id.btn_Clear_Password:
					mCmdParams.cmdType = CmdSocket.CMD_SET_WIFI_CLEAR_PASSWORD;
                    Apps.cmdSocket.sendCommand(mCmdParams);
				break;
				case R.id.btn_Change_Resolution:
					int resSelectedItem = spin_Resolution.getSelectedItemPosition();
					String resStr = spin_Resolution.getItemAtPosition(resSelectedItem).toString();
					int index = resStr.indexOf("*");
					int width = Integer.parseInt(resStr.substring(0, index));
					int height = Integer.parseInt(resStr.substring(index + 1, resStr.length()));

					Log.d(TAG, resSelectedItem + "  " + width + " " + height);
					if (width > 0 && height > 0) {
						mCmdParams.cmdType = CmdSocket.CMD_SET_CAMERA_RESOLUTION;
						mCmdParams.width = width;
						mCmdParams.height = height;
                        Apps.cmdSocket.sendCommand(mCmdParams);
						setDefaultResolution(resSelectedItem);
					} else {
						WIFI_Dialog(SetActivity.this.getString(R.string.str_set_wrongres));
					}
				break;

				case R.id.btn_jump_setting:
				{
					startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
				}
				break;

				default:
				break;
			}
		}
	};


	private Handler handler = new Handler(new Handler.Callback()
	{

		@SuppressWarnings("unchecked")
		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
				case HandlerParams.SET_OK:
					WIFI_Dialog(SetActivity.this.getString(R.string.str_set_setok));
				break;
				case HandlerParams.SET_FAIL:
					WIFI_Dialog(SetActivity.this.getString(R.string.str_set_setfail));
				break;
				case HandlerParams.UPDATE_RESOLUTION:
				    ArrayList<String> ress = new ArrayList<>();
					ress = (ArrayList<String>) msg.obj;
                    if(ress.size() > 0) {
                        hasGerResolution = true;
                        mResParams.clear();
                        for (int i = 0; i < ress.size(); i++) {
                            for (int j = ress.size() - 1; j > i; j--) {
                                if(ress.get(i).equals(ress.get(j)))
                                    ress.remove(j);
                            }
                        }

                        for (int t = 0; t < ress.size(); t++) {
                            mResParams.add(ress.get(t));
                        }
                        mAdapter.notifyDataSetChanged();
                        int index = getDefaultResolution();
                        if (index >= mResParams.size())
                            spin_Resolution.setSelection(0);
                        else
                            spin_Resolution.setSelection(index);
                    }
				break;
				case HandlerParams.SET_GET_SSIDHEAD:
					edt_Change_Name.setText(msg.obj.toString());
				break;
				default:
				break;
			}
			return true;
		}
	});

	private void WIFI_Dialog(String buffer) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(SetActivity.this);
		dialog.setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage(buffer)
				.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}

				}).setNegativeButton("", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				}).create().show();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}


	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		Intent intent = new Intent(SetActivity.this, PrincipalActivity.class);
		startActivity(intent);
	};

	private void setDefaultResolution(int index) {
		SharedPreferences sp = this.getSharedPreferences("DefaultResolution", Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putInt("DefaultResolution", index);
		edit.commit();
	}

	private int getDefaultResolution() {
		int index;
		SharedPreferences user = this.getSharedPreferences("DefaultResolution", Context.MODE_PRIVATE);
		index = user.getInt("DefaultResolution", 0);
		return index;
	}
}
