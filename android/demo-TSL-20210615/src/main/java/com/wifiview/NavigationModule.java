package com.wifiview;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.wifiview.activity.CheckPermissonActivity;
import com.wifiview.activity.ImagePagerActivity;
import com.wifiview.activity.LoadingActivity;
import com.wifiview.activity.PlaybackActivity;
import com.wifiview.activity.PrincipalActivity;
import com.wifiview.activity.SelectActivity;
import com.wifiview.activity.SetActivity;
import com.wifiview.activity.VideoPlayer;
import com.wifiview.config.Apps;
import com.wifiview.config.BootReceiver;

import java.security.Principal;

public class NavigationModule  extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;
    private Intent intent;

    NavigationModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    @NonNull
    @Override
    public String getName() {
        return "NavigationModule";
    } //The name of the component when it is called in the RN code

    @ReactMethod
    public void navigateToNative(){
        ReactApplicationContext context = getReactApplicationContext();
        intent = new Intent(context, LoadingActivity.class);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            intent.setFlags((Intent.FLAG_ACTIVITY_NEW_TASK));
            context.startActivity(intent);
        }
    }
}