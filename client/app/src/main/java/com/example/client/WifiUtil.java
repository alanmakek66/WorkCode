package com.example.client;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.DhcpInfo;
import android.util.Log;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class WifiUtil {
    private WifiManager wifiManager;
    private Context context;

    public WifiUtil(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    // Get the device's IP address
    public String getDeviceIpAddress() {
        try {
            // Get DHCP info from the connected Wi-Fi network
            DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
            if (dhcpInfo == null) {
                return null;
            }

            // The device's IP is stored as an integer in dhcpInfo.ipAddress
            int ipAddressInt = dhcpInfo.ipAddress;
            if (ipAddressInt == 0) {
                return null; // No IP address available
            }

            // Convert the integer IP to a human-readable string
            byte[] ipBytes = new byte[] {
                    (byte) (ipAddressInt & 0xFF),
                    (byte) ((ipAddressInt >> 8) & 0xFF),
                    (byte) ((ipAddressInt >> 16) & 0xFF),
                    (byte) ((ipAddressInt >> 24) & 0xFF)
            };

            InetAddress ipAddress = InetAddress.getByAddress(ipBytes);
            return ipAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get the gateway IP address
    public String getGatewayIpAddress() {
        try {
            // Get DHCP info from the connected Wi-Fi network
            DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
            if (dhcpInfo == null) {
                return null;
            }

            // The gateway IP is stored as an integer in dhcpInfo.gateway
            int gatewayInt = dhcpInfo.gateway;
            if (gatewayInt == 0) {
                return null; // No gateway available
            }

            // Convert the integer IP to a human-readable string
            byte[] gatewayBytes = new byte[] {
                    (byte) (gatewayInt & 0xFF),
                    (byte) ((gatewayInt >> 8) & 0xFF),
                    (byte) ((gatewayInt >> 16) & 0xFF),
                    (byte) ((gatewayInt >> 24) & 0xFF)
            };

            InetAddress gatewayAddress = InetAddress.getByAddress(gatewayBytes);

            return gatewayAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }
    public int checkWifiConnection() {

            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int networkId = wifiInfo.getNetworkId();

            if (networkId == -1) {
                // Wi-Fi 沒有連接
                Log.d("WiFi", "Not connected to any Wi-Fi network");
                return  -1;
            } else {
                // Wi-Fi 已連接
                Log.d("WiFi", "Connected to Wi-Fi network: " + wifiInfo.getSSID());
                return 1;
            }
        }



}
