package com.wifiview.nativelibs;

import com.wifiview.config.Apps;


public class NativeLibs
{
    private long mNativePtr;

    static {
        System.loadLibrary("mlcamera-2.0");
    }

    public NativeLibs()
    {
        // TODO Auto-generated constructor stub
        mNativePtr = nativeCreateCamera(Apps.defaultIpAddr);
    }
    public void destroyCamera() {
        nativeDestroyCamera(mNativePtr);
        mNativePtr = 0;
    }

    /**
     * 获取lib的版本号
     * @return
     */
    public static native String nativeGetVersion();

    public boolean startPreview() {
        return nativeStartPreview(mNativePtr);
    }

    public void stopPreview() {
        nativeStopPreview(mNativePtr);
    }

    public byte[] getOneFrameBuffer() {
        return nativeGetFrameBuffer(mNativePtr);
    }

    public boolean nativeIsDeviceConnected() {
        return nativeIsDeviceConnected(mNativePtr);
    }

    public float getFrameDegree() {
        return nativeGetFrameDegree(mNativePtr);
    }

    /* Below for Live Stream */
    private static native long nativeCreateCamera(String ip);

    private static native void nativeDestroyCamera(long id_cam);

    private static native boolean nativeStartPreview(long id_cam);

    private static native void nativeStopPreview(long id_cam);

    private static native byte[] nativeGetFrameBuffer(long id_cam);

    private static native boolean nativeIsDeviceConnected(long id_cam);

    public native static float nativeGetFrameDegree(long id_cam);

    /* Below for AVI Record */
    public native static boolean nativeAVIRecStart(String absPath);

    public native static void nativeAVIRecStop();

    public native static void nativeAVIRecSetParams(int w, int h, int fps);

    public native static void nativeAVIRecSetAudioParams(int channel, int sampleRate, int bit);

    public native static void nativeAVIRecAddData(byte[] data, int size);

    public native static void nativeAVIRecAddWav(byte[] data, int size);

    public native static int nativeAVIRecGetTimestamp();

    /* Below for AVI Play */
    public native static boolean nativeAVIOpen(String absPath);

    public native static void nativeAVIClose();

    public native static double nativeAVIGetTotalTime();

    public native static int nativeAVIGetTotalFrame();

    public native static byte[] nativeAVIGetFrameAtIndex(int index);

    public native static byte[] nativeAVIGetFrameAtTime(double time);

    public native static byte[] nativeAVIGetVoiceAtTime(double time);


    /* Below for Commands */
    /**
     * 设置设备的wifi名称
     * @param ip    设备ip地址
     * @param wifiName
     * @return
     */
    public native static int nativeCmdSetName(String ip, String wifiName);

    /**
     * 设置设备的wifi密码
     * @param ip    设备ip地址
     * @param password
     * @return
     */
    public native static int nativeCmdSetPassword(String ip, String password);

    /**
     * 清除密码
     * @param ip    设备ip地址
     * @return
     */
    public native static int nativeCmdClearPassword(String ip);

    /**
     * 重启设备
     * @param ip    设备ip地址
     * @return
     */
    public native static int nativeCmdSendReboot(String ip);

    /**
     * 设置分辨率
     * @param ip    设备ip地址
     * @param width
     * @param height
     * @param frameRate
     * @return
     */
    public native static int nativeCmdSetResolution(String ip, int width, int height, int frameRate);

    /**
     * 获取设备分辨率，如何解析byte[]成分辨率参考CmdSocket中的代码
     * @param ip    设备ip地址
     * @return
     */
    public native static byte[] nativeCmdGetResolution(String ip);

    /**
     * 获取按键状态
     * @param ip    设备ip地址
     * @return  1 拍照，2 录像
     */
    public native static int nativeCmdGetRemoteKey(String ip);

    /**
     * get device battery voltage
     * @param ip device ip address
     * @return voltage * 1000, -1 error or timeout
     *  int vol = nativeCmdGetBattery(ip);
     *  if(vol == -1)
     *      return;
     *  int batVal = vol & 0xFFFF;
     *  int status = (vol >> 16);
     *  if(status & 1) {
     *      Log.d(TAG, "Charging now, Battery vol:" + batVal);
     *  }else {
     *      Log.d(TAG, "Not Charging now, Battery vol:" + batVal);
     *  }
     */
    public native static int nativeCmdGetBattery(String ip);


    /**
     * 获取设备的版本号
     * @param ip    设备ip地址
     * @return  -1为获取失败
     */
    public native static int nativeCmdGetRemoteVersion(String ip);

}
