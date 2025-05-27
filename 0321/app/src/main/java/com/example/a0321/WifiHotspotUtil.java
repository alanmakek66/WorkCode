package com.example.a0321;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

public class WifiHotspotUtil {
    private WifiManager wifiManager;
    private Context context;

    public WifiHotspotUtil(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public boolean setWifiApEnabled(boolean enabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (enabled) {
                // 启用 LocalOnlyHotspot
                wifiManager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {
                    @Override
                    public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                        Log.d("WifiHotspotUtil", "Hotspot started successfully.");
                        // 可以在这里保存 reservation 对象，以便将来停止热点
                    }

                    @Override
                    public void onStopped() {
                        Log.d("WifiHotspotUtil", "Hotspot stopped.");
                    }

                    @Override
                    public void onFailed(int reason) {
                        Log.e("WifiHotspotUtil", "Hotspot failed to start: " + reason);
                    }
                }, new Handler());
            } else {
                Log.d("WifiHotspotUtil", "Stopping the hotspot is not directly supported with LocalOnlyHotspot.");
            }
        } else {
            // 对于较旧版本，使用反射（不推荐用于生产环境）
            Log.e("WifiHotspotUtil", "Hotspot management is not supported on this Android version.");
        }
        return enabled;
    }
}