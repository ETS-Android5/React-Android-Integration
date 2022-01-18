package com.wifiview.config;

public class HandlerParams
{
	public static final int RECORD_START = 0x10;
	public static final int RECORD_STOP = 0x11;
	public static final int SDCARD_FULL = 0x12;
	public static final int UPDATE_RECORDTIME = 0x13;
	public static final int HAS_GET_STREAM = 0x14;

    public static final int REMOTE_TAKE_PHOTO = 0x15;
    public static final int REMOTE_TAKE_RECORD = 0x16;
	
	public static final int SET_OK = 0x21;
	public static final int SET_FAIL = 0x22;
	public static final int UPDATE_RESOLUTION = 0x23;
	public static final int UPDATE_VIDEOFORMAT = 0x24;
	public static final int SET_GET_SSIDHEAD = 0x25;

	public static final int REMOTE_GET_IP = 0x30;
}
