package com.siemens.dolphin.SLCMicoEasyLink;

import android.content.Context;
import android.util.Log;

import com.mico.micoapi.MiCOEasyLink;
import com.mico.micoapi.MiCOmDNS;
import com.mxchip.callbacks.EasyLinkCallBack;
import com.mxchip.callbacks.SearchDeviceCallBack;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SLCMicoEasyLink extends CordovaPlugin {

    private static final String TAG = "SLCMicoEasyLink";

    private static final String ACTION_WIFI_SSID = "wifiSSID";
    private static final String ACTION_START_WIFI_CONFIG_WITH_PWD = "startWifiConfigWithPwd";
    private static final String ACTION_STOP_WIFI_CONFIG = "stopWifiConfig";
    private static final int RUN_SECOND = 60000;
    private static final int SLEEP_TIME = 20;
    private static final String DISCOVER_SERVICE = "_easylink._tcp.local.";

    private MiCOEasyLink easyLink;
    private MiCOmDNS mDNS;
    private Context context;
    private boolean isSearched;
    private HashMap<String, JSONObject> devices;

    CallbackContext discoverCallback;

    /**
     * Constructor.
     */
    public SLCMicoEasyLink() {

    }

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        context = this.cordova.getActivity().getApplicationContext();
        easyLink = new MiCOEasyLink();
        mDNS = new MiCOmDNS();
    }

    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) {
        if (action.equals(ACTION_WIFI_SSID)) {
            callbackContext.success(getWifiSSID());
            return true;
        }

        if (action.equals(ACTION_START_WIFI_CONFIG_WITH_PWD)) {
            this.cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        startWifiConfigWithPwd(callbackContext, args.getString(0), args.getString(1));
                    } catch (JSONException e) {
                        Thread t = Thread.currentThread();
                        t.getUncaughtExceptionHandler().uncaughtException(t, e);
                    }
                }
            });
            return true;
        }

        if (action.equals(ACTION_STOP_WIFI_CONFIG)) {
            stopWifiConfig();
            return true;
        }

        return false;
    }

    public String getWifiSSID() {
        String ssid = easyLink.getSSID(context);
        return (ssid.length() > 0 ? ssid : "No ssid found.");
    }

    public void startWifiConfigWithPwd(CallbackContext callbackContext, String password, String info) {
        isSearched = false;
        discoverCallback = callbackContext;
        devices = null;

        this.easyLink.startEasyLink(
                context,                         // context
                this.easyLink.getSSID(context),  // SSID
                password,                        // password
                RUN_SECOND,                      // run second
                SLEEP_TIME,                      // sleep time
                new EasyLinkCallBack() {         //  EasyLinkCallBack
                    @Override
                    public void onSuccess(String s) {
                        Log.d(TAG, "startEasyLink onSuccess: " + s);
                        if (!isSearched) {
                            startSearchDevice();
                            isSearched = true;
                        } else {
                            stopSearchDevice();
                        }
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Log.d(TAG, "startEasyLink onFailure: " + i + ", " + s);
                    }
                },
                info
        );
    }

    public void stopWifiConfig() {
        this.easyLink.stopEasyLink(new EasyLinkCallBack() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, "stopEasyLink onSuccess: " + s);
            }

            @Override
            public void onFailure(int i, String s) {
                Log.d(TAG, "stopEasyLink onFailure: " + i + ", " + s);
            }
        });
    }

    private void startSearchDevice() {
        mDNS.startMdnsService(context, DISCOVER_SERVICE, new SearchDeviceCallBack() {
            @Override
            public void onSuccess(String message) {
                super.onSuccess(message);
            }

            /**
             * If start search device failed, stop searching.
             */
            @Override
            public void onFailure(int code, String message) {
                super.onFailure(code, message);
                stopSearchDevice();
            }

            @Override
            public void onDevicesFind(JSONArray deviceStatus) {
                super.onDevicesFind(deviceStatus);
                Log.d(TAG, "onDeviceFind: " + deviceStatus.toString());
                try {
                    if (devices != null) {
                        for (int i = 0; i < deviceStatus.length(); i++) {
                            JSONObject device = deviceStatus.getJSONObject(i);
                            if (!devices.containsKey(device.getString("MAC"))) {
                                discoverCallback.success(device);
                                stopSearchDevice();
                                break;
                            }
                        }
                    } else {
                        devices = new HashMap<String, JSONObject>();
                        for (int i = 0; i < deviceStatus.length(); i++) {
                            JSONObject device = deviceStatus.getJSONObject(i);
                            devices.put(device.getString("MAC"), device);
                        }
                    }
                } catch (JSONException e) {
                    Thread t = Thread.currentThread();
                    t.getUncaughtExceptionHandler().uncaughtException(t, e);
                }
            }
        });
    }

    private void stopSearchDevice() {
        mDNS.stopMdnsService(new SearchDeviceCallBack() {
            @Override
            public void onSuccess(String message) {
                super.onSuccess(message);
                Log.d(TAG, "stopSearchDevice onSuccess: " + message);
            }

            @Override
            public void onFailure(int code, String message) {
                super.onFailure(code, message);
                Log.d(TAG, "stopSearchDevice onFailure: " + code + ", " + message);
            }
        });
    }
}
